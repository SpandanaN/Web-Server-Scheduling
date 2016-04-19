import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class ServerThreadPool{
	ServerThread[] serverThread;
	private Server server;
	int nThreads;
	int maxCores;
	Boolean first=true;
	
	public ServerThreadPool(int nThreads,Server server,int maxCores)
	{
		this.server=server;
		this.maxCores=maxCores;
		this.nThreads=nThreads;
		serverThread=new ServerThread[nThreads];
		for(int i=0;i<nThreads;i++)
		{
			this.serverThread[i]=new ServerThread();
		}
	}
	
	void setUpThreads(){
		ArrayList<Double> weights=new ArrayList<Double>();
		HashMap<BlockingQueue<Request>,Integer> queueToCore=new HashMap<BlockingQueue<Request>,Integer>();
		HashMap<BlockingQueue<Request>,Double> queueToWeight=new HashMap<BlockingQueue<Request>,Double>();
		
		int cpuid=1;
		
		weights.add(server.ggQueueWeight);
		weights.add(server.fbQueueWeight);
		weights.add(server.amQueueWeight);
		
		Collections.sort(weights);
		
		queueToWeight.put(server.getggQueue(), server.getggQueueWeight());
		queueToWeight.put(server.getfbQueue(), server.getfbQueueWeight());
		queueToWeight.put(server.getamQueue(), server.getamQueueWeight());
		
		for(Double weight : weights )
		{
			BlockingQueue<Request> tempQueue=null;
			for(BlockingQueue<Request> queue:queueToWeight.keySet())
			{
				Double w=queueToWeight.get(queue);
				if(w.equals(weight))
				{
					tempQueue=queue;
				}
			}
			
			if(tempQueue!=null){
				queueToWeight.remove(tempQueue);
			}
			
			
			queueToCore.put(tempQueue,cpuid++);
			
			
			if(cpuid>maxCores){
				cpuid=1;
			}
		}
		
		Double totalWeight=server.getggQueueWeight() + server.getfbQueueWeight() + server.getamQueueWeight();
		int ggThreads=(int) (server.getggQueueWeight()/totalWeight*nThreads);
		int fbThreads=(int) (server.getfbQueueWeight()/totalWeight*nThreads);
		int amThreads=(int) (server.getamQueueWeight()/totalWeight*nThreads);
		
		
		if(ggThreads==0){
			ggThreads=1;
		}
		
		if(fbThreads==0){
			fbThreads=1;
		}
		
		if(amThreads==0){
			amThreads=1;
		}
		int i;
		
		for(i=0;i<ggThreads;i++)			
		{
			serverThread[i].setMainQueue(Server.googleQueue);
			serverThread[i].setCoreAffinity(queueToCore.get(Server.googleQueue));
		}
		
		System.out.println("Assigned "+ggThreads+" to Google Queue on "+queueToCore.get(server.getggQueue())+" core.");
		
		int limit=i;
		
		for(;i<fbThreads+limit;i++)
		{
			serverThread[i].setMainQueue(Server.facebookQueue);
			serverThread[i].setCoreAffinity(queueToCore.get(Server.facebookQueue));
		}
		
		System.out.println("Assigned "+fbThreads+" to Facebook Queue on "+queueToCore.get(server.getfbQueue())+" core.");
		
		limit=i;
		
		for(;i<amThreads+limit;i++)
		{
			serverThread[i].setMainQueue(Server.amazonQueue);
			serverThread[i].setCoreAffinity(queueToCore.get(Server.amazonQueue));
		}
		
		System.out.println("Assigned "+amThreads+" to Amazon Queue on "+queueToCore.get(server.getamQueue())+" core.");
		
		
		for(int j=0;j<i;j++)
		{
			if(first)
			{
				serverThread[j].start();
			}
			else
			{
				serverThread[j].interrupt();
			}
		}
		
		if(first)
		{
			first=false;
		}
		
		
	}
}
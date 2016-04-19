import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;



public class Server{
	
	int port;
	ServerSocket ss;
	
	static BlockingQueue<Request> googleQueue=new LinkedBlockingQueue<Request>();
	static BlockingQueue<Request> facebookQueue=new LinkedBlockingQueue<Request>();
	static BlockingQueue<Request> amazonQueue=new LinkedBlockingQueue<Request>();
	
	double ggQueueWeight=0.3;
	double fbQueueWeight=0.5;
	double amQueueWeight=0.2;
	
	static int googleQueueCounter=0;
	static int facebookQueueCounter=0;
	static int amazonQueueCounter=0;
	
	
	private final ScheduledExecutorService scheduler =Executors.newScheduledThreadPool(1);
	
	public Server(int port, ServerSocket ss) throws IOException 
	{
		this.port= port;
		this.ss=ss;
	}
	
	BlockingQueue getggQueue(){
		return googleQueue;
	}
	BlockingQueue getfbQueue(){
		return facebookQueue;
	}
	BlockingQueue getamQueue(){
		return amazonQueue;
	}
	
	Double getggQueueWeight(){
		return ggQueueWeight;
	}
	Double getfbQueueWeight(){
		return fbQueueWeight;
	}
	Double getamQueueWeight(){
		return amQueueWeight;
	}
	
	void setggQueueWeight(double ggQueueWeight)
	{
		this.ggQueueWeight=ggQueueWeight;
	}
	void setfbQueueWeight(double fbQueueWeight)
	{
		this.fbQueueWeight=fbQueueWeight;
	}
	void setamQueueWeight(double amQueueWeight)
	{
		this.amQueueWeight=amQueueWeight;
	}
	
	
	/* request from jmeter is in this format: 'GET /google HTTP/1.1'
	 * We need to filter out google from it so that we can identify 
	 * and process the required request.
	 */
	Request processRequest(Socket socket) throws IOException, URISyntaxException{
		String request;
		BufferedReader reader = new BufferedReader(new
				InputStreamReader(socket.getInputStream()));
		request=reader.readLine();
		String requestSubstring=request.substring(5);
		String spaceSplitArray[]=requestSubstring.split(" ", 2);
		
		request=spaceSplitArray[0];
		
		if(request.equals("google"))
		{
			Request ggReq=new GoogleRequest(socket);
			googleQueue.add(ggReq);
			googleQueueCounter++;
			return ggReq;
		}
		if(request.equals("facebook"))
		{
			Request fbReq=new FacebookRequest(socket);
			facebookQueue.add(fbReq);
			facebookQueueCounter++;
			return fbReq;
		}
		if(request.equals("amazon"))
		{
			Request azReq=new AmazonRequest(socket);
			amazonQueue.add(azReq);
			amazonQueueCounter++;
			return azReq;
		}
		return null;
	}
	
	public ServerSocket getSs() {
		return ss;
		}
	
	public static void main(String [] args) throws IOException, URISyntaxException{
		ServerSocket ss = null;
		int portNumber= 3000;
		int count=0;
		
		ServerSocket socket = new ServerSocket(portNumber);
		Server myServer=new Server(portNumber,socket);
		
		
		final int nThreads=100;
		final int nCores=4;
		
		ServerThreadPool tp=new ServerThreadPool(nThreads,myServer,nCores);
		tp.setUpThreads();
	
		
		ArrayList<String> queids=new ArrayList<String>();
		queids.add("Google Request");
		queids.add("Facebook Request");
		queids.add("Amazon Request");
		CalculateWeights c = new CalculateWeights(queids);
		
		long endTime=0;
		long startTime=0;
		long totalTime=0;
		int counter=0;
		while(true)
		{
			counter=counter+1;
			count++;
			
			Socket newSocket = myServer.getSs().accept();
			Request req=myServer.processRequest(newSocket);
			
			
			if(count==50)
			{
				ArrayList<Float> weights = c.getWeights();
				myServer.setggQueueWeight(weights.get(0));
				myServer.setfbQueueWeight(weights.get(1));
				myServer.setamQueueWeight(weights.get(2));
				
				System.out.println("Queue:Google\t\tFacebook\t\tAmazon");
				System.out.println("W:    "+weights.get(0)+"\t\t"+weights.get(1)+"\t\t\t"+weights.get(2));
				
				tp.setUpThreads();
				count=0;
			}
			
		}
	}
}
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ServerThread extends Thread{
	BlockingQueue<Request> mainQueue=new LinkedBlockingQueue<Request>();
	BlockingQueue<Request> updatedQueue=new LinkedBlockingQueue<Request>();
	long affinityMask=1;
	
	void setMainQueue(BlockingQueue<Request> queue){
		this.mainQueue=queue;
	}
	
	void setCoreAffinity(int coreId){
		long mask=1;
		this.affinityMask=mask<<coreId;
	}
	
	public void run()
	{
		
		Request r=null;
		
		while(true)
		{
			
			try
			{
				r=mainQueue.take();
			}
			catch(InterruptedException e)
			{

				Thread.currentThread().isInterrupted();
				continue;
			}
			
			try {
				
				String response=r.requestProcessing();
				
			} catch (IOException e1) {
				
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				
				e1.printStackTrace();
			}
			
			BufferedWriter outputResponse=null;
			
			
			try {
				outputResponse = new BufferedWriter(new OutputStreamWriter(r.getSocket().getOutputStream()));
				outputResponse.write("HTTP/1.1 200 OK");
				outputResponse.write("Response: Success");
				outputResponse.flush();
				outputResponse.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
	}
	

	
}
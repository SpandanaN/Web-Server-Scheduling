import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

public class FCFSServer
{
	int port;
	ServerSocket ss;
	
	final ScheduledExecutorService scheduler =Executors.newScheduledThreadPool(1);
	
	 FCFSServer(int port, ServerSocket ss) throws IOException 
	{
		this.port= port;
		this.ss=ss;
	}
	 
	 Request processRequest(Socket socket) throws IOException, URISyntaxException
	 {
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
				ggReq.requestProcessing();
				return ggReq;
			}
			if(request.equals("facebook"))
			{
				Request fbReq=new FacebookRequest(socket);
				fbReq.requestProcessing();
				return fbReq;
			}
			if(request.equals("amazon"))
			{
				Request azReq=new AmazonRequest(socket);
				azReq.requestProcessing();
				return azReq;
			}
			return null;
	 }
	 
	 public ServerSocket getSs() 
	 {
			return ss;
	 }
		
	public static void main(String [] args) throws IOException, URISyntaxException
	{
		
		ServerSocket ss = null;
		int portNumber= 3000;
		int count=0;
		
		System.out.println(count);
		
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(portNumber);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		Server myServer=new Server(portNumber,socket);
		
		BufferedWriter outputResponse=null;
		long endTime=0;
		long startTime=0;
		long totalTime=0;
		
		while(true){
			count++;
			
			Socket newSocket = myServer.getSs().accept();
			Request req=myServer.processRequest(newSocket);
			req.requestProcessing();			
			outputResponse = new BufferedWriter(new OutputStreamWriter(req.getSocket().getOutputStream()));
			outputResponse.write("HTTP/1.1 200 OK");
			outputResponse.write("Response: Success");
			outputResponse.flush();
			outputResponse.close();
			
		}
		
		
		
		
		
		
	}
}
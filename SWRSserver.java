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



public class SWRSserver{
	
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
			
		}
	}
}
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;

public class Request{
	private Socket socket;
	
	Request(Socket socket){
		this.socket=socket;
	}
	
	String requestProcessing() throws IOException, URISyntaxException{
		return "OK";
	}
	
	Socket getSocket(){
		return socket;
	}
}


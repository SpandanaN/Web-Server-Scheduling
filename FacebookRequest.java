import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class FacebookRequest extends Request{
	Socket socket;
	
	FacebookRequest(Socket socket) {
		super(socket);
	}
	
	String requestProcessing() throws IOException, URISyntaxException{
		
		URL url = new URL("http://www.facebook.com");
        URLConnection urlConnection = url.openConnection();
        HttpURLConnection connection = null;
        if(urlConnection instanceof HttpURLConnection)
        {
           connection = (HttpURLConnection) urlConnection;
        }
        else
        {
           System.out.println("Please enter an HTTP URL.");
    
        }
        BufferedReader in = new BufferedReader(
        new InputStreamReader(connection.getInputStream()));
        String urlString = "";
        String current;
        while((current = in.readLine()) != null)
        {
           urlString += current;
        }
		
        return "OK";
	}
	
}

import java.net.*;
import java.io.*;

// the BulletinClient class is used within the BulletinGUI upon successful connection to Server.
public class BulletinClient {

    private Socket client_socket;
    private PrintWriter out;
    private BufferedReader in;
    

    // The BulletinClient is intialized using Client specified hostname and port number.
    public BulletinClient(String host, int port) throws IOException {
        this.client_socket = new Socket(host, port);
        this.out = new PrintWriter(client_socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
    }

    // Retrieves the initial server configuration message and relays it to the Client
    public String get_initial_message() throws IOException {
        return in.readLine();
    }

    // The send_request() method sends the Client request to the BulletinClientHandler.
    // The handler returns a String response which may be multi-line or a single line.
    // In the case of a multi-line response (GET messages), each line is read repeatedly from the newline
    // In single line, the message is simply returned.
    public String send_request(String request) throws Exception {
        out.println(request);

        String server_line = in.readLine();        
        if(server_line == null) return null;

        String[] parsed_line = server_line.split(" ");  

        // Multi-line response always have form OK X where X is number of objects returned
        if(server_line.startsWith("OK ") && parsed_line.length==2){
            String output = "SERVER: " + server_line + "\n";
            int line_number = Integer.parseInt(parsed_line[1]); 

            for(int i=0; i < line_number; i++){
                output += in.readLine() + "\n";
            }

            // skip the empty line
            in.readLine();

            return output;

        }
        else{
            return "SERVER: " + server_line + "\n";
        }
        
    }

    // attempts to close the Client socket and streams.
    public void disconnect() throws IOException {

        if (out != null) out.println("DISCONNECT");
        if (out != null) out.close();
        if (in != null) in.close();
        if (client_socket != null) client_socket.close();

    }

    
    
}
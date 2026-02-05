
import java.net.*;
import java.io.*;

public class BulletinClient {

    private Socket client_socket;
    private PrintWriter out;
    private BufferedReader in;
    

    public BulletinClient(String host, int port) throws IOException {
        this.client_socket = new Socket(host, port);
        this.out = new PrintWriter(client_socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
    }

    public String get_initial_message() throws IOException {
        return in.readLine();
    }

    public String send_request(String request) throws Exception {
        out.println(request);

        String server_line = in.readLine();        
        if(server_line == null) return null;

        String[] parsed_line = server_line.split(" ");  

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

    public void disconnect() throws IOException {

        if (out != null) out.println("DISCONNECT");
        if (out != null) out.close();
        if (in != null) in.close();
        if (client_socket != null) client_socket.close();

    }

    
    
}
import java.io.*;
import java.net.*;


public class BulletinClientHandler implements Runnable {

    // a BulletinClientHandler object is created by a BulletinBoardServer for each successful Client request.
    private Socket client_socket = null;
    private String client_IP = null;
    private BulletinBoardServer bulletin_server = null;

    public BulletinClientHandler(Socket client_socket, String client_IP, BulletinBoardServer bulletin_server) {
        this.client_socket = client_socket;
        this.client_IP = client_IP;
        this.bulletin_server = bulletin_server;
    }


    // Method is invoked on thread start.
    @Override
    public void run() {
        try {
            process_request();
        }
        catch(Exception e) {
            System.err.println("ERROR PROCESSING CLIENT [" + client_IP + "]: " + e.getMessage());
        }

    }

    /*
    The process_request listens to Client requests. Each request is sent to the BulletinProtocol which
    validates the request and calls the respective Server method and response or error message.
    */ 
    public void process_request() {

        PrintWriter out = null;
        BufferedReader in = null;

        try {

            out = new PrintWriter(client_socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            out.println(bulletin_server.get_initial_message());

            BulletinProtocol protocol = new BulletinProtocol();
            String request_line;


            while((request_line = in.readLine()) != null) {
                
                String response = protocol.handle_request(request_line, bulletin_server);

                // the response relayed from the Server is written back to the Client socket
                out.println(response);

                if(response.equals("DISCONNECT")) {
                    break;
                }

            }

        }
        
        catch(IOException e) {
            // Handle I/O exceptions (connection refused, socket creation failed, etc.)
            System.err.println("ERROR: " + e.getMessage());
        }

        finally {
            // Attempts to close BulletinClientHandler message streams and socket.
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (client_socket != null) client_socket.close();
            }
            catch(IOException e){
                System.err.println("ERROR CLOSING CONNECTION: " + e.getMessage());
            }

        }

    }

}

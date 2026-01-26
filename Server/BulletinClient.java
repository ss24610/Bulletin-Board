import java.io.*;
import java.net.*;


public class BulletinClient implements Runnable {

    private Socket client_socket = null;
    private String client_IP = null;
    private BulletinBoardServer bulletin_server = null;

    public BulletinClient(Socket client_socket, String client_IP, BulletinBoardServer bulletin_server) {
        this.client_socket = client_socket;
        this.client_IP = client_IP;
        this.bulletin_server = bulletin_server;
    }


    @Override
    public void run() {
        // TODO: Implement client logic
        try {
            process_request();
        }
        catch(Exception e) {
            System.err.println("[" + client_IP + "] Error processing request: " + e.getMessage());
        }

    }

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

                out.println(response);

                if(response.equals("DISCONNECT")) {
                    break;
                }

                


            }




        }
        
        catch(IOException e) {
            System.err.println("Error creating output and input streams for server client handler.");
        }

        finally {

            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (client_socket != null) client_socket.close();
            }
            catch(IOException e){
                System.err.println("Error closing connection: " + e.getMessage());
            }

        }


    }


    



}


import java.net.*;
import java.util.*;
import java.io.*;

public class BulletinClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 4444;

    public static void main(String[] args) throws Exception {
        

        // Parse command-line arguments for host and port
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
                if (port < 1024 || port > 65535) {
                    System.err.println("Error: Port must be between 1024 and 65535. Using default port " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid port number '" + args[1] + "'. Using default port " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        Socket client_socket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try{

            client_socket = new Socket(host, port);
            out = new PrintWriter(client_socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));

            // stdin is just for testing stuff
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            String initial_message = in.readLine();

            // so that client can parse the server data
            System.out.println(initial_message);

            //System.out.println(in.readLine());
            String userInput;

            while (true) {
                System.out.print("> ");
                userInput = stdin.readLine();
        
                if (userInput == null) break;

                // optional quit
                if (userInput.equalsIgnoreCase("quit") ||
                    userInput.equalsIgnoreCase("disconnect")) {
                    break;
                }
        
                // send to server
                out.println(userInput);
        
                // read response
                String serverLine = in.readLine();
                if (serverLine == null) break;
                String[] parsed_line = serverLine.split(" ");  

                if(serverLine.startsWith("OK ") && parsed_line.length==2){
                    String output = "SERVER: " + serverLine + "\n";
                    int line_number = Integer.parseInt(parsed_line[1]); 

                    for(int i=0; i < line_number; i++){
                        output += in.readLine() + "\n";
                    }
                    in.readLine();
                    System.out.print(output);

                    
                }
                else{
                    System.out.println("SERVER: " + serverLine);
                }

            }

        }

        catch(UnknownHostException e){
            System.err.println("ERROR IP address of host could not be determined.");
        }

        catch(IOException e) {
            System.err.println("ERROR creating output and input streams for client socket.");
        }

        finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (client_socket != null) client_socket.close();
            }
            catch(IOException e){
                System.err.println("Error closing client side connection: " + e.getMessage());
            }
        }

    }

}
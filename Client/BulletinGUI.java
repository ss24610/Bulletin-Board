package Client;

import java.net.*;
import java.util.*;
import java.io.*;

public class BulletinGUI {

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
                if (port < 1 || port > 65535) {
                    System.err.println("Error: Port must be between 1 and 65535. Using default port " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid port number '" + args[1] + "'. Using default port " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        try{

            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        
            //System.out.println(in.readLine());
            String userInput;

            while (true) {
                System.out.print("> ");
                userInput = stdin.readLine();
        
                if (userInput == null) break;
        
                // send to server
                out.println(userInput);
        
                // read response
                String serverLine = in.readLine();
                if (serverLine == null) break;
        
                System.out.println("SERVER: " + serverLine);
        
                // optional quit
                if (userInput.equalsIgnoreCase("quit") ||
                    userInput.equalsIgnoreCase("disconnect")) {
                    break;
                }
            }

        }

        catch(Error e) {
            System.out.println(e);
        }

    }

}
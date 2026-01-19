import java.util.*;

public final class BulletinBoardServer {



    public static void main(String[] args) throws Exception {

        /* should we use a default port instead of terminating on invalid port? */
        int DEFAULT_PORT = 4444;

        if (args.length < 6) {

            System.err.println("Usage: <port> <board_width> <board_height> <note_width> <note_height> <color1> ... <colorN>");
            System.err.println("Example: 4444 10 10 2 2 blue");
            System.exit(1);

        }

        int server_port = 0;
        int board_width = 0;
        int board_height = 0;
        int note_height = 0;
        int note_width = 0;

        ArrayList colours = new ArrayList<String>();


        try {
            
            server_port = Integer.parseInt(args[0]);

            if(server_port < 1024 || server_port > 65535) {
                System.err.println("Port number must be an integer between 1024 and 65535");
                System.exit(1);

            }

        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + args[0]);
            System.err.println("Must be an integer between 1024 and 65535");
            System.exit(1);
        }

        try {
            
            board_height = Integer.parseInt(args[1]);
            board_width = Integer.parseInt(args[2]);

            if(board_height < 0) {
                System.err.println("Board height must be positive");
                System.exit(1);

            }

            if(board_width < 0) {
                System.err.println("Board width must be positive");
                System.exit(1);

            }

        } catch (NumberFormatException e) {
            System.err.println("Board dimensions must be positive integers");
            System.exit(1);
        }


        try {
            
            note_height = Integer.parseInt(args[1]);
            note_width = Integer.parseInt(args[2]);

            if(note_height < 0) {
                System.err.println("Note height must be positive");
                System.exit(1);

            }

            if(note_width < 0) {
                System.err.println("Note width must be positive");
                System.exit(1);

            }

        } catch (NumberFormatException e) {
            System.err.println("Note dimensions must be positive integers");
            System.exit(1);
        }


        

        
    }
    
}

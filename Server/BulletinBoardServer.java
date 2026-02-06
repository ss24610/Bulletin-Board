import java.net.*;
import java.util.*;
import java.io.*;

public final class BulletinBoardServer {

    /*
    BulletinBoard configuration data is select by a user during Server startup.
    The notes array contains the bulletin board 'state', all of the BulletinNotes currently
    managed by the Server.
    */
    private final int board_width;
    private final int board_height;
    private final int note_height;
    private final int note_width;
    private final ArrayList<String> colours;
    private ArrayList<BulletinNote> notes = new ArrayList<BulletinNote>();

    public BulletinBoardServer (int board_width, int board_height, int note_height, int note_width, ArrayList<String> colours) {
        this.board_width = board_width;
        this.board_height = board_height;
        this.note_height = note_height;
        this.note_width = note_width;
        this.colours = colours;
    }
    

    /*
    Upon successful validation from the BulletinProtocol, the post_note() method is invoked. The method 
    validates against complete overlap between TWO notes (occupying the same rectangular region). Upon 
    validation, a BulletinNote object is created using Client specified data and added the the notes array. 
    */
    public synchronized String post_note(String note_content, String note_color, int note_x, int note_y) {

        for(BulletinNote note: notes){
            
            int[] current_note_position = note.get_note_position();
            int current_note_x = current_note_position[0];
            int current_note_y = current_note_position[1];

            if(current_note_x == note_x && current_note_y == note_y) {
                return "ERROR COMPLETE OVERLAP";
            }

        }

        BulletinNote new_note = new BulletinNote(note_content, note_color, note_x, note_y);

        notes.add(new_note);

        return "OK NOTE POSTED";

    }

    /*
    Upon successful validation from the BulletinProtocol, the place_pin() method is invoked.
    First all potential notes where a pin can be placed are determined, from each of these notes we
    determine if any contain a pin with the same coordiantes (duplicate pin). If all checks are passed,
    the Pin is placed on all respective notes. 
    */
    public synchronized String place_pin(int x, int y) {

        BulletinPin new_pin = new BulletinPin(x,y);

        // iterate over notes to determine which Notes contain the coordinate of the pin
        ArrayList<BulletinNote> affected_notes = new ArrayList<>();
        for (BulletinNote note : notes) {
            int[] note_dimensions = note.get_note_position();
            int note_x = note_dimensions[0];
            int note_y = note_dimensions[1];

            boolean valid_pin = (x >= note_x && x <= note_x+note_width) && (y >= note_y && y <= note_y+note_height);

            if (valid_pin) {
                affected_notes.add(note);
            }
        }

        // if no note are affected, the pin missed therefore a pin miss error message 
        // is relayed to the Client
        if(affected_notes.size() ==0){
            return "ERROR PIN MISS NO NOTE WITHIN GIVEN COORDINATES";
        }

        // for all the affected notes, check to see if any note already has that pin
        // if any note already contains a pin on that coordinate, it triggers a
        // duplicate pin error and the pin is not placed.
        for (BulletinNote note: affected_notes){
            if(note.contains_pin(new_pin)){
                return "ERROR PIN ALREADY EXISTS";
            }
        }

        // if passed the previous checks, pin can be placed over all affected notes
        for (BulletinNote note: affected_notes){
            note.place_pin(new_pin);
        }

        return "OK PIN PLACED";

    }

    /*
    Upon successful validation from the BulletinProtocol, the remove_pin() method is invoked.
    We iterate over all the BulletinNotes managed by the Server. If any contain a BulletinPin
    at the specified coordiantes, it is removed from the BulletinNote.
    */
    public synchronized String remove_pin(int x, int y) {

        boolean pin_missed = true;
        BulletinPin pin = new BulletinPin(x, y);

        for(BulletinNote note: notes){
            if(note.contains_pin(pin)){
                pin_missed = false;
                note.remove_pin(pin);
            }
        }

        if(pin_missed){
            return "ERROR PIN NOT FOUND NO PIN EXISTS AT GIVEN COORDINATES";
        }
        else{
            return "OK REMOVED PIN";
        }

    }

    /*
    Upon successful validation from the BulletinProtocol, the shake() method is invoked.
    We iterate over all the BulletinNotes managed by the Server. Any BulletinNotes that aren't
    pinned are placed in an array. These notes are then removed from the Server's notes array.
    */
    public synchronized String shake() {

        if(notes.size()==0){
            return "ERROR NO NOTES EXIST";
        }

        ArrayList<BulletinNote> unpinned_notes = new ArrayList<>();


        for(BulletinNote note: notes) {
            if(!note.is_pinned()){
                unpinned_notes.add(note);
            }
        }

        notes.removeAll(unpinned_notes);

        return "OK SHAKE COMPLETE";

    }

    /*
    Upon successful validation from the BulletinProtocol, the clear() method is invoked.
    All of the notes managed by the server are removed.
    */
    public synchronized String clear() {

        if(notes.size()==0){
            return "ERROR NO NOTES EXIST";
        }
        
        notes.clear();

        return "OK NOTES CLEARED";
    }

    /*
    Upon successful validation from the BulletinProtocol, the get_notes() method is invoked. If
    no notes are managed by the Server or no notes managed by the Server match the Clients filters, 
    an error message is relayed to the Client. We iterate over the notes managed by the Server and
    compare them agains the Client filters. A string representation of all notes that match the
    Clients filters are returned to the Client.
    */
    public synchronized String get_notes(String colour, int contains_x, int contains_y, String refers_to) {

        if(notes.size() == 0){
            return "ERROR NO NOTES EXIST";
        }

        else{

            String response = "";
            ArrayList<BulletinNote> filtered_notes = new ArrayList<>();

            for(BulletinNote note: notes){
        
                int[] note_base = note.get_note_position();

                // We compare each note to the clients filters or sentinel value (if the filter
                // was not specified)
                boolean colour_filter = (note.get_note_colour().equals(colour) || colour.equals("ALL"));
                boolean dimension_filter = ((contains_x >= note_base[0] && contains_x < note_base[0]+note_width &&
                                            contains_y >= note_base[1] && contains_y < note_base[1]+note_height)
                                            || (contains_x == -1 && contains_y ==-1));
                boolean substring_filter = ((note.get_note_content().contains(refers_to) || refers_to.equals("ALL")));
                
                if(colour_filter && dimension_filter && substring_filter){
                    filtered_notes.add(note);
                }

            }

            if(filtered_notes.size()==0){
                return "ERROR NO NOTES EXIST WITH SPECIFIED FILTERS";
            }
            
            response += "OK " + filtered_notes.size() + "\n";
            for(BulletinNote note: filtered_notes){
                response += note.display_note();
            }

            return response;

        }

    }

    /*
    Upon successful validation from the BulletinProtocol, the get_pins() method is invoked. If
    no notes are managed by the Server or no pins exist, we return an error response to the 
    Client. Otherwise, a string representation of each pin is relayed to the Client.
    */
    public synchronized String get_pins() {

        if(notes.size()==0){
            return "ERROR NO NOTES EXIST";
        }

        String response = "OK ";
        ArrayList<BulletinPin> placed_pins = new ArrayList<>();

        for(BulletinNote note: notes){
            for(BulletinPin pin: note.get_pins()){
                if(!placed_pins.contains(pin)){
                    placed_pins.add(pin);
                }
            }
        }

        if(placed_pins.size()==0){
            return "ERROR NO PINS EXIST";
        }

        response += placed_pins.size() + "\n";

        for(BulletinPin pin: placed_pins){
            response += pin.display_pin();
        }

        return response;

    }

    // Used to retrieve the dimensions of the board, used within the BulletinProtocol.
    public synchronized int[] get_board_dimensions(){
        return new int[] {board_width, board_height}; 
    }

    // Used to retrieve the dimensions of notes, used within the BulletinProtocol.
    public synchronized int[] get_note_dimensions(){
        return new int[] {note_width, note_height}; 
    }

    // Used to retrieve the list of valid colours, used within the BulletinProtocol.
    public synchronized ArrayList<String> get_colours() {
        return colours;
    }

    // Used to relay an initial message to the client, specifing Bulletin Board configuration data.
    public synchronized String get_initial_message() {

        
        String s = "";
        System.err.println("Usage: <port> <board_width> <board_height> <note_width> <note_height> <color1> ... <colorN>");

        s += "SERVER: Board Width: " + this.board_width + " Board Height: " + this.board_height + " Note Width: " + this.note_width + " Note Height: " + this.note_height;

        s += " Colours: ";
        for(String colour: this.colours) {
            s += " " + colour;
        }

        return s;
    }

    // Main Server loop
    public static void main(String[] args) throws Exception {

        /* The Server requires at least 6 initialization parameters:
        <port> <board_width> <board_height> <note_width> <note_height> <color1>.... */
        if (args.length < 6) {

            System.err.println("ERROR INSUFFICIENT ARGUMENTS");
            System.err.println("Usage: <port> <board_width> <board_height> <note_width> <note_height> <color1> ... <colorN>");
            System.err.println("Example: 4444 10 10 2 2 blue");
            System.exit(1);

        }

        int server_port = 0;
        int board_width = 0;
        int board_height = 0;
        int note_height = 0;
        int note_width = 0;
        ArrayList<String> colours = new ArrayList<String>();

        // Ensures that a valid integer port number was supplied, otherwise terminates Server. 
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

        // Ensures that a valid integer for board/note dimensions was supplied, otherwise terminates Server.
        try {
            board_width = Integer.parseInt(args[1]);
            board_height = Integer.parseInt(args[2]);
            note_width = Integer.parseInt(args[3]);
            note_height = Integer.parseInt(args[4]);

            if(board_height < 0 || board_width < 0 || note_height < 0 || note_width < 0) {
                System.err.println("ERROR BOARD/NOTE DIMENSIONS MUST BE POSITIVE INTEGERS");
                System.exit(1);

            }

        } catch (NumberFormatException e) {
            System.err.println("ERROR BOARD/NOTE DIMENSIONS MUST BE POSITIVE INTEGERS");
            return;
        }

        for(int i = 5; i < args.length; i++) {
            colours.add(args[i].toLowerCase());
        }

        // Creates a BulletinBoardServer object once all Server configuration data is validated
        BulletinBoardServer server = new BulletinBoardServer(board_width, board_height, note_height, note_width, colours);
        ServerSocket server_socket = null;
        try {

            server_socket = new ServerSocket(server_port);
            System.out.println("BulletinBoardServer started on localhost and port number: " + server_port);
            System.out.println("Close server using Ctrl+C");
            System.out.println("---------------------------------------------------");

            while (true) {

                // Server socket listens for incoming Client requests. 
                // A BulletinClientHandler is assigned to each Client connection and is designated
                // to a specific thread.
                Socket client_socket = server_socket.accept();

                String client_ip = client_socket.getInetAddress().getHostAddress();

                System.out.println("Client connected from: " + client_ip);

                BulletinClientHandler client_connection = new BulletinClientHandler(client_socket, client_ip, server);
                
                Thread thread = new Thread(client_connection);

                thread.start();

            }

        } catch (BindException e) {
            // Handle socket binding exceptions 
            System.err.println("ERROR SERVER PORT: " + server_port + " IS ALREADY IN USE");
            System.exit(1);
        }

        catch (IOException e) {
            // Handle I/O exceptions (connection refused, socket creation failed, etc.)
            System.err.println("ERROR UNABLE TO CREATE SERVER SOCKET ON PORT: " + server_port);
            System.exit(1);
        }

        finally{
            if(server_socket!=null) server_socket.close();
        }

    }
    
}

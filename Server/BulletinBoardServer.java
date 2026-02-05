
import java.net.*;
import java.util.*;
import java.io.*;

public final class BulletinBoardServer {

    private final int server_port;
    private final int board_width;
    private final int board_height;
    private final int note_height;
    private final int note_width;
    private final ArrayList<String> colours;
    private ArrayList<BulletinNote> notes = new ArrayList<BulletinNote>();

    public BulletinBoardServer (int server_port, int board_width, int board_height, int note_height, int note_width, ArrayList<String> colours) {
        this.server_port = server_port;
        this.board_width = board_width;
        this.board_height = board_height;
        this.note_height = note_height;
        this.note_width = note_width;
        this.colours = colours;
    }
    

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


    public synchronized String place_pin(int x, int y) {

        BulletinPin new_pin = new BulletinPin(x,y);

        //iterate over notes to determine which notes can contain the Pin
        ArrayList<BulletinNote> affected_notes = new ArrayList<>();
        for (BulletinNote note : notes) {
            int[] note_dimensions = note.get_note_position();
            int note_x = note_dimensions[0];
            int note_y = note_dimensions[1];

            boolean valid_pin = (x >= note_x && x < note_x+note_width) && (y >= note_y && y <= note_y+note_height);

            if (valid_pin) {
                affected_notes.add(note);
            }
        }

        // if no note are affected, the pin missed therefore error
        if(affected_notes.size() ==0){
            return "ERROR PIN MISS NO NOTE WITHIN GIVEN COORDINATES";
        }

        // for all the affected notes, check to see if any note already has that pin
        // duplicate pin error
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


    public synchronized String shake() {

        ArrayList<BulletinNote> unpinned_notes = new ArrayList<>();


        for(BulletinNote note: notes) {
            if(!note.is_pinned()){
                unpinned_notes.add(note);
            }
        }

        notes.removeAll(unpinned_notes);

        return "OK SHAKE COMPLETE";

    }

    public synchronized String clear() {
        notes.clear();

        return "OK NOTES CLEARED";
    }


    public synchronized String get_notes(String colour, int contains_x, int contains_y, String refers_to) {
        //color=<color> contains=<x> <y> refersTo=<substring>

        if(notes.size() == 0){
            return "ERROR NO NOTES EXIST";
        }

        else{

            String response = "";
            ArrayList<BulletinNote> filtered_notes = new ArrayList<>();

            for(BulletinNote note: notes){
        
                int[] note_base = note.get_note_position();

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

    public synchronized int[] get_board_dimensions(){
        return new int[] {board_width, board_height}; 
    }

    public synchronized int[] get_note_dimensions(){
        return new int[] {note_width, note_height}; 
    }

    public synchronized ArrayList<String> get_colours() {
        return colours;
    }

    public synchronized String get_initial_message() {

        
        String s = "";
        System.err.println("Usage: <port> <board_width> <board_height> <note_width> <note_height> <color1> ... <colorN>");

        s += this.board_width + " " + this.board_height + " " + this.note_width + " " + this.note_height;

        for(String colour: this.colours) {
            s += " " + colour;
        }

        return s;
    }

    public static void main(String[] args) throws Exception {

        /* should we use a default port instead of terminating on invalid port? */
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
            board_width = Integer.parseInt(args[1]);
            board_height = Integer.parseInt(args[2]);
            note_width = Integer.parseInt(args[3]);
            note_height = Integer.parseInt(args[4]);

            if(board_height < 0 || board_width < 0 || note_height < 0 || note_width < 0) {
                System.err.println("Error: Board/note dimensions must be positive integers");
                System.exit(1);

            }

        } catch (NumberFormatException e) {
            System.err.println("Board and note dimensions must be positive integers");
            return;
        }

        for(int i = 5; i < args.length; i++) {
            colours.add(args[i].toLowerCase());
        }

        BulletinBoardServer server = new BulletinBoardServer(server_port, board_width, board_height, note_height, note_width, colours);
        ServerSocket server_socket = null;
        try {

            server_socket = new ServerSocket(server_port);
            System.out.println("BulletinBoardServer started on localhost and port: " + server_port);
            System.out.println("Close server using Ctrl+C");
            System.out.println("---------------------------------------------------");


            while (true) {

                Socket client_socket = server_socket.accept();

                String client_ip = client_socket.getInetAddress().getHostAddress();

                System.out.println("Client connected from: " + client_ip);

                BulletinClientHandler client_connection = new BulletinClientHandler(client_socket, client_ip, server);
                
                Thread thread = new Thread(client_connection);

                thread.start();

            }

        } catch (BindException e) {
            System.err.println("Error: " + server_port + " is already in use.");
            System.exit(1);
        }

        catch (IOException e) {
            // Handle I/O exceptions (connection refused, socket creation failed, etc.)
            System.err.println("Error: unable to create server socket on port " + server_port);
            System.exit(1);
        }

        finally{
            if(server_socket!=null) server_socket.close();
        }

    }
    
}

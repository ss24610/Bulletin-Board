public class BulletinProtocol {

    /* 
    the handle_request method contains the main logic for the BulletinProtocol. Each client request
    if validated through a sequence of conditional logic. If the Client request is valid, the BulletinProtocol
    calls the respective Server method and returns the Server response. Otherwise, the BulletinProtocol returns
    an error message informing the Client what went wrong.
    */

    public String handle_request(String request_line, BulletinBoardServer server) {
        

        if (request_line == null || request_line.isBlank()) {
            return "ERROR EMPTY REQUEST";
        }

        String[] tokens = request_line.split(" ");

        // Client submitted a POST request
        if (tokens[0].equalsIgnoreCase("post")){

            if(tokens.length < 5){
                return "ERROR INVALID FORMAT POST REQUIRES COORDINATES, COLOUR, AND MESSAGE";
            }
            
            try{
                
                int x = Integer.parseInt(tokens[1]);
                int y = Integer.parseInt(tokens[2]);

                // retrieve server data board/note dimension data
                int[] board_dimensions = server.get_board_dimensions();
                int board_width = board_dimensions[0];
                int board_height = board_dimensions[1];

                int[] note_dimensions = server.get_note_dimensions();
                int note_width = note_dimensions[0];
                int note_height = note_dimensions[1];

                // We first determine whether the coordinates of the note are valid and within the board dimensions.
                if(y < 0 || x < 0) {
                    return "ERROR INVALID FORMAT NOTE COORDINATES MUST BE POSITIVE";
    
                }
        
                if(x + note_width > board_width || y + note_height > board_height){
                    return "ERROR NOTE OUT OF BOUNDS";
                }
                
                String color = tokens[3];

                // Validate if the colour is supported by the server.
                if(!server.get_colours().contains(color)){
                    return "ERROR COLOUR NOT SUPPORTED " + color + " IS NOT A VALID COLOUR";
                }

                String content = request_line.substring(request_line.indexOf(color) + color.length() + 1);
                
                return server.post_note(content, color, x, y);
            }

            catch (NumberFormatException e) {
                return "ERROR NOTE COORDINATES MUST BE POSITIVE INTEGERS WITHIN BOARD BOUNDARIES";
            }

        }

        else if (tokens[0].equalsIgnoreCase("get")) {

            if (tokens.length > 1 && tokens[1].equalsIgnoreCase("pins")){
                return server.get_pins();
            }

            else{

                // Sentinel values, if a Client get message does not include certain filters, 
                // these sentinel values will be used by the Server to indicate ALL
                String color = "ALL";
                String refers_to = "ALL";
                int contains_x = -1;
                int contains_y = -1;

                try{

                    // We iterate over every token to determine the filter being used
                    for(int i = 1; i < tokens.length; i++){

                        if(tokens[i].startsWith("color=")){
                            color = tokens[i].substring(6);
                        }

                        else if(tokens[i].startsWith("contains=")){
                            contains_x = Integer.parseInt(tokens[i].substring(9));
                            contains_y = Integer.parseInt(tokens[i+1]);
                            i++;

                        }

                        // refersTo is inferred to be the last filter of the POST request (specified in RFC),
                        // it retrieves the entire substring to search for using indexing
                        else if(tokens[i].startsWith("refersTo=")){
                            refers_to = request_line.substring(request_line.indexOf("refersTo=") + 9);
                        }

                    }

                    // the get_notes() method is called to retrieve Notes with the specified filters.
                    return server.get_notes(color, contains_x, contains_y, refers_to);
                }

                catch(NumberFormatException e){
                    return "ERROR COORDINATES MUST BE SUPPLIED AS POSITIVE INTEGERS";
                }

            }
            
        }

        // Shake and Clear requests just invoke the server method.
        else if (tokens[0].equalsIgnoreCase("shake")) {
            return server.shake();
        }

        else if (tokens[0].equalsIgnoreCase("clear")) {
            return server.clear();
        }


        else if  (tokens[0].equalsIgnoreCase("pin")) {

            if(tokens.length != 3){
                return "ERROR INVALID FORMAT PIN REQUIRES X AND Y COORDINATES";
            }
            
            int x;
            int y;

            // We determine if the pin coordinates are valid (positive) integers and whether they
            // are within the dimensions of the board
            try {
                x=Integer.parseInt(tokens[1]);
                y=Integer.parseInt(tokens[2]);

                int[] board_dimensions = server.get_board_dimensions();
                int board_x = board_dimensions[0];
                int board_y = board_dimensions[1];

                if(y < 0 || x < 0) {
                    return "ERROR INVALID FORMAT PIN COORDINATES MUST BE POSITIVE";
    
                }

                else if(x > board_x || y > board_y){
                    return "ERROR PIN OUT OF BOUNDS";
                }

                // Invoke the respective server method with the validated coordinates.
                return server.place_pin(x,y);

            }

            catch (NumberFormatException e) {
                return "ERROR INVALID FORMAT PIN COORDINATES MUST BE SUPPLIED AS POSITIVE INTEGER";
            }
            

        }

        else if (tokens[0].equalsIgnoreCase("unpin")) {

            if(tokens.length != 3){
                return "ERROR INVALID FORMAT UNPIN REQUIRES X AND Y COORDINATES";
            }
            
            int x;
            int y;

            // We determine if the pin coordinates are valid (positive) integers and whether they
            // are within the dimensions of the board
            try {
                x=Integer.parseInt(tokens[1]);
                y=Integer.parseInt(tokens[2]);

                int[] board_dimensions = server.get_board_dimensions();
                int board_x = board_dimensions[0];
                int board_y = board_dimensions[1];

                if(y < 0 || x < 0) {
                    return "ERROR INVALID FORMAT UNPIN COORDINATES MUST BE POSITIVE";
    
                }

                else if(x > board_x || y > board_y){
                    return "ERROR UNPIN OUT OF BOUNDS";
                }

                // Invoke the respective server method with the validated coordinates.
                return server.remove_pin(x,y);

            }

            catch (NumberFormatException e) {
                return "ERROR UNPIN COORDINATES MUST BE SUPPLIED AS POSITIVE INTEGER";
            }
            

        }

        // If the request method does not match any of the above, it is invalid.
        else{
            return "INVALID REQUEST METHOD DOES NOT EXIST";
        }

    }

}

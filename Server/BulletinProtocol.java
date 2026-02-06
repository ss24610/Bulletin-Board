public class BulletinProtocol {

    public String handle_request(String request_line, BulletinBoardServer server) {
        

        if (request_line == null || request_line.isBlank()) {
            return "ERROR EMPTY REQUEST";
        }

        String[] tokens = request_line.split(" ");

        if (tokens[0].equalsIgnoreCase("post")){

            if(tokens.length < 5){
                return "ERROR INVALID FORMAT POST REQUIRES COORDINATES, COLOUR, AND MESSAGE";
            }
            
            try{
                
                int x = Integer.parseInt(tokens[1]);
                int y = Integer.parseInt(tokens[2]);

                int[] board_dimensions = server.get_board_dimensions();
                int board_width = board_dimensions[0];
                int board_height = board_dimensions[1];

                int[] note_dimensions = server.get_note_dimensions();
                int note_width = note_dimensions[0];
                int note_height = note_dimensions[1];

                if(y < 0 || x < 0) {
                    return "ERROR INVALID FORMAT NOTE COORDINATES MUST BE POSITIVE";
    
                }
        
                if(x + note_width > board_width || y + note_height > board_height){
                    return "ERROR NOTE OUT OF BOUNDS";
                }
                
                String color = tokens[3];

                if(!server.get_colours().contains(color)){
                    return "ERROR COLOUR NOT SUPPORTED " + color + " IS NOT A VALID COLOR";
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

                String color = "ALL";
                String refers_to = "ALL";
                int contains_x = -1;
                int contains_y = -1;

                //color=<color> contains=<x> <y> refersTo=<substring>
                try{

                
                    for(int i = 1; i < tokens.length; i++){

                        if(tokens[i].startsWith("color=")){
                            color = tokens[i].substring(6);
                        }

                        else if(tokens[i].startsWith("contains=")){
                            contains_x = Integer.parseInt(tokens[i].substring(9));
                            contains_y = Integer.parseInt(tokens[i+1]);
                            i++;

                        }
                        else if(tokens[i].startsWith("refersTo=")){
                            refers_to = request_line.substring(request_line.indexOf("refersTo=") + 9);
                        }

                    }

                    return server.get_notes(color, contains_x, contains_y, refers_to);
                }

                catch(NumberFormatException e){
                    return "ERROR COORDINATES MUST BE SUPPLIED AS POSITIVE INTEGERS";
                }

            }
            
        }

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

                return server.remove_pin(x,y);

            }

            catch (NumberFormatException e) {
                return "ERROR UNPIN COORDINATES MUST BE SUPPLIED AS POSITIVE INTEGER";
            }
            

        }

        else{
            return "INVALID REQUEST METHOD DOES NOT EXIST";
        }

        
    }



}
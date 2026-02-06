import java.util.*;

public class BulletinNote {

    /*
    A BulletinNote object is created by a successful client POST request. The attributes
    of the BulletinNote are chosen and fixed during initialization.
    */
    private String note_content = null;
    private String note_colour = null;
    private int note_x = 0;
    private int note_y = 0;
    private ArrayList<BulletinPin> pins = new ArrayList<BulletinPin>();

    public BulletinNote(String note_content, String note_colour, int note_x, int note_y) {
        this.note_content = note_content;
        this.note_colour = note_colour;
        this.note_x = note_x;
        this.note_y = note_y;
    }

    /*
    Note Pin Status, derived from whether a BulletinNote has a BulletinPin contained in its
    'pins' array. 
    */
    public boolean is_pinned() {
        return pins.size() > 0;
    }

    /*
    The get_pins() method retrieves the pins array of the BulletinNote object. 
    */
    public ArrayList<BulletinPin> get_pins() {
        return pins;
    }

    /*
    The get_note_colour() method retrieves the colour of the BulletinNote chosen during the POST method.
    This method is invokved within the BulletinBoardServer/BulletinProtocol whenever the Colour of a note is needed.
    */
    public String get_note_colour() {
        return note_colour;
    }

    /*
    The get_note_position() method retrieves the coordinate of the BulletinNote in the form of an integer array.
    This method is invokved within the BulletinBoardServer and BulletinBoardProtocol when when a comparison
    against a Notes position is required.
    */
    public int[] get_note_position() {
        return new int[] {note_x, note_y};
    }

    /*
    The get_note_content() method retrieves the message assigned to the BulletinNote during the POST method.
    This method is invokved within the BulletinBoardServer to determine equality during the GET method.
    */
    public String get_note_content() {
        return note_content;
    }

    /*
    The display_note() method returns a string reprentation of the BulletinNote. This method is 
    invoked during GET requests to provide Clients with an overview the BulletinNote object.
    */
    public String display_note() {
        return "NOTE " + this.note_x + " " + this.note_y + " " + this.note_colour + " " + this.note_content + " PINNED: "+ this.is_pinned() + "\n";
    }

    /*
    The contains_pin() method is used to determine if a BulletinPin object currently exists within
    the pins array of the specific BulletinNote object. This method is used within the place_pin() 
    and remove_pin() SERVER methods to determine if a pin is duplicated or if a pin is to be
    removed from a note, respectively.
    */
    public boolean contains_pin(BulletinPin pin){
        return pins.contains(pin);
    }

    /*
    The place_pin() method is used to add a BulletinPin object to the pins array of a BulletinNote.
    */
    public void place_pin(BulletinPin pin){

        pins.add(pin);

    }

    /*
    The remove_pin() method is used to remove a BulletinPin object from the pins array of a BulletinNote.
    */
    public void remove_pin(BulletinPin pin){
        pins.remove(pin);

    }

}

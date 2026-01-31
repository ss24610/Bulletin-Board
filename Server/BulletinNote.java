import java.util.*;

public class BulletinNote {

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

    public boolean is_pinned() {
        return pins.size() > 0;
    }

    public ArrayList<BulletinPin> get_pins() {
        return pins;
    }

    public String get_note_colour() {
        return note_colour;
    }

    public int[] get_note_position() {
        return new int[] {note_x, note_y};
    }

    public String get_note_content() {
        return note_content;
    }

    public String display_note() {
        return "NOTE " + this.note_x + " " + this.note_y + " " + this.note_colour + " " + this.note_content + " PINNED: "+ this.is_pinned() + "\n";
    }

    public boolean contains_pin(BulletinPin pin){
        return pins.contains(pin);
    }


    public void place_pin(BulletinPin pin){

        pins.add(pin);

    }

    public void remove_pin(BulletinPin pin){
        pins.remove(pin);

    }

}
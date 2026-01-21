import java.util.*;

public class BulletinNote {

    private String note_content = null;
    private String note_color = null;
    private int note_width = 0;
    private int note_height = 0;
    private int note_x = 0;
    private int note_y = 0;
    private final ArrayList<BulletinPin> pins = new ArrayList<BulletinPin>();

    public BulletinNote(String note_content, String note_color, int note_width, int note_height, int note_x, int note_y) {
        this.note_content = note_content;
        this.note_color = note_color;
        this.note_width = note_width;
        this.note_height = note_height;
        this.note_x = note_x;
        this.note_y = note_y;
    }

    public boolean is_pinned() {
        return pins.size() > 0;
    }

    public ArrayList<BulletinPin> get_pins() {
        return pins;
    }

    public String get_note_color() {
        return note_color;
    }

    public int[] get_note_position() {
        return new int[] {note_x, note_y};
    }

    public int[] get_note_dimensions() {
        return new int[] {note_width, note_height};
    }

    public String get_note_content() {
        return note_content;
    }

}
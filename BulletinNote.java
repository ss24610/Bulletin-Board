public class BulletinNote {

    private String note_content = null;
    private String note_color = null;
    private int note_width = 0;
    private int note_height = 0;
    private int note_x = 0;
    private int note_y = 0;
    private boolean note_status = false;

    public BulletinNote(String note_content, String note_color, int note_width, int note_height, int note_x, int note_y, boolean note_status) {
        this.note_content = note_content;
        this.note_color = note_color;
        this.note_width = note_width;
        this.note_height = note_height;
        this.note_x = note_x;
        this.note_y = note_y;
        this.note_status = note_status;
    }


    public boolean is_pinned() {
        return note_status;
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
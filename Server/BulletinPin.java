public class BulletinPin {

    
    private final int pin_x;
    private final int pin_y;

    public BulletinPin(int pin_x, int pin_y) {
        this.pin_x = pin_x;
        this.pin_y = pin_y;
    }
    
    public int get_pin_x() {
        return pin_x;
    }

    public int get_pin_y() {
        return pin_y;
    }

    public String display_pin() {
        return "PIN " + pin_x + " " + pin_y + "\n";
    }

    @Override
    public boolean equals(Object other_pin) {
        if (this == other_pin) return true;
        if (other_pin == null || getClass() != other_pin.getClass()) return false;
        BulletinPin pin = (BulletinPin) other_pin;
        return this.pin_x == pin.pin_x && this.pin_y == pin.pin_y;
    }

}

public class BulletinPin {

    /*
    A BulletinPin object is created by a successful client PIN request. The coordinates
    of the BulletinPin are chosen and fixed during initialization.
    */
    private final int pin_x;
    private final int pin_y;

    /*
    A BulletinPin object is created by a successful client PIN request. The attributes
    of the BulletinPin are chosen and fixed during initialization.
    */
    public BulletinPin(int pin_x, int pin_y) {
        this.pin_x = pin_x;
        this.pin_y = pin_y;
    }
    
    /*
    The get_pin_x() method retrieves the x coordinate of the BulletinPin object.
    */
    public int get_pin_x() {
        return pin_x;
    }

    /*
    The get_pin_y() method retrieves the y coordinate of the BulletinPin object.
    */
    public int get_pin_y() {
        return pin_y;
    }

    /*
    The display_pin() method returns a string reprentation of the BulletinPin. This method is 
    invoked during the GET PINS request to provide a Client of an overview of all BulletinPin's
    that are stored by the Server.
    */
    public String display_pin() {
        return "PIN " + pin_x + " " + pin_y + "\n";
    }

    /*
    The equals() override is provided to allow for equality comparisons within the java Collections
    package. Specifically, this method allows us to easily detect if a particular BulletinPin exists
    within a BulletinNotes' 'pin' array > if a BulletinPin 'pins' a BulletinNote.
    */
    @Override
    public boolean equals(Object other_pin) {
        if (this == other_pin) return true;
        if (other_pin == null || getClass() != other_pin.getClass()) return false;
        BulletinPin pin = (BulletinPin) other_pin;
        return this.pin_x == pin.pin_x && this.pin_y == pin.pin_y;
    }

}

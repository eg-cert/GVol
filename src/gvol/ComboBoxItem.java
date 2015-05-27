package gvol;

public class ComboBoxItem {

    final private int ID;
    final private String text;

    ComboBoxItem(int ID, String text) {
        this.ID = ID;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public int getID() {
        return ID;
    }

}

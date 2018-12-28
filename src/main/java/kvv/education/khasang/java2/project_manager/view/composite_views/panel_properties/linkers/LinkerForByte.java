package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

public class LinkerForByte extends LinkerForNumber {
    @Override
    public Byte giveObject() {
        if (number.getText().isEmpty()) {
            return null;
        } else {
            return Byte.valueOf(this.number.getText());
        }
    }
}

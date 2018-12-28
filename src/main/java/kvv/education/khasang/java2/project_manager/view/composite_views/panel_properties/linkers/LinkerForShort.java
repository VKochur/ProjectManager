package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

public class LinkerForShort extends LinkerForNumber {
    @Override
    public Short giveObject() {
        if (number.getText().isEmpty()) {
            return null;
        } else {
            return Short.valueOf(this.number.getText());
        }
    }
}

package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

public class LinkerForFloat extends LinkerForNumber {
    @Override
    public Float giveObject() {
        if (number.getText().isEmpty()) {
            return null;
        } else {
            return Float.valueOf(number.getText());
        }
    }
}

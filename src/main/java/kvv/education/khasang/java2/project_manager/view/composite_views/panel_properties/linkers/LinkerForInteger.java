package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

public class LinkerForInteger extends LinkerForNumber {
    @Override
    public Integer giveObject() {
        if (number.getText().isEmpty()) {
            return null;
        } else {
            return Integer.valueOf(this.number.getText());
        }
    }
}

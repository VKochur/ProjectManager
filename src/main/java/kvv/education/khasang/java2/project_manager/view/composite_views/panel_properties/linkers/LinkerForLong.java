package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

public class LinkerForLong extends LinkerForNumber {
    @Override
    public Long giveObject() {
        if (number.getText().isEmpty()) {
            return null;
        } else {
            return Long.valueOf(this.number.getText());
        }
    }
}

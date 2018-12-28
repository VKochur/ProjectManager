package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

public class LinkerForDouble extends LinkerForNumber {
    @Override
    public Double giveObject() {
        if (number.getText().isEmpty()) {
            return null;
        } else {
            return Double.valueOf(this.number.getText());
        }
    }
}

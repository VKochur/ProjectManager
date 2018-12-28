package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;

import javax.swing.*;
import java.awt.*;

public abstract class LinkerForNumber implements Linker<Number> {
    JTextField number;
    JPanel panel;
    JLabel label;

    public LinkerForNumber() {
        number = new JTextField();
        panel = new JPanel();
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x6E697C)));
        label = new JLabel();
        panel.add(label, BorderLayout.WEST);
        panel.add(number, BorderLayout.CENTER);
    }

    @Override
    public void setTitle(String text) {
        label.setText(text + ":  ");
    }

    @Override
    public JComponent giveComponent() {
        return panel;
    }

    @Override
    public void takeObject(Number object) {
        if (object == null) {
            number.setText("");
        } else {
            number.setText(object.toString());
        }
    }

    @Override
    public void setEditableForComponent(Boolean value) {
        number.setEditable(value);
    }
}

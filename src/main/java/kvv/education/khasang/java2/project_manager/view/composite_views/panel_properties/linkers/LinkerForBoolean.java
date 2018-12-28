package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;

import javax.swing.*;
import java.awt.*;

public class LinkerForBoolean implements Linker<Boolean> {

    JPanel panel;
    JLabel label;
    JComboBox<Boolean> comboBox;

    public LinkerForBoolean() {
        panel = new JPanel();
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE0DCDF)));
        panel.setLayout(new BorderLayout());
        label = new JLabel();
        comboBox = new JComboBox<>();
        //обеспечим вид комбо в режиме доступом/недоступном для редактирования
        comboBox.setBackground(new Color(255, 255, 255));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public void paint(Graphics g) {
                if (isEnabled()) {
                    setForeground(new Color(0));
                } else {
                    setForeground(new Color(0));
                }
                super.paint(g);
            }
        });
        //заполним комбо
        comboBox.addItem(null);
        comboBox.addItem(true);
        comboBox.addItem(false);
        panel.add(label, BorderLayout.WEST);
        panel.add(comboBox, BorderLayout.CENTER);
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
    public Boolean giveObject() {
        return (Boolean) comboBox.getSelectedItem();
    }

    @Override
    public void takeObject(Boolean object) {
        comboBox.setSelectedItem(object);
    }

    @Override
    public void setEditableForComponent(Boolean value) {
        comboBox.setEnabled(value);
    }
}

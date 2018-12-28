package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;

import javax.swing.*;
import java.awt.*;

public class LinkerForEnum implements Linker<Object> {
    Class clazz;
    Object[] objs;
    JPanel panel;
    JLabel label;
    JComboBox<Object> comboBox;


    public LinkerForEnum(Class clazz) {
        this.clazz = clazz;
        objs = clazz.getEnumConstants();
        panel = new JPanel();
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x6E697C)));
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
        //добавим значения комбо
        comboBox.addItem(null);
        for (Object obj : objs) {
            comboBox.addItem(obj);
        }
        panel.setLayout(new BorderLayout());
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
    public Object giveObject() {
        return comboBox.getSelectedItem();
    }

    @Override
    public void takeObject(Object object) {
        comboBox.setSelectedItem(object);
    }

    @Override
    public void setEditableForComponent(Boolean value) {
        comboBox.setEnabled(value);
    }
}

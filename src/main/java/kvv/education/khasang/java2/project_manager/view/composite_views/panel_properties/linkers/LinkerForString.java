package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LinkerForString implements Linker<String> {

    JScrollPane jScrollPane;
    JPanel panel;
    JTextArea textArea;
    JLabel label;
    JButton button;

    public LinkerForString() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x6E697C)));
        textArea = new JTextArea();


        jScrollPane = new JScrollPane(textArea);
        label = new JLabel();
        panel.add(label, BorderLayout.WEST);
        button = new JButton("...");
        button.setPreferredSize(new Dimension(27, 27));
        panel.add(button, BorderLayout.EAST);
        panel.add(jScrollPane, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        //при нажатии на кнопку
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setVisible(false);
                panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            }
        });

        textArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                button.setVisible(true);

            }
        });
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
    public String giveObject() {
        return textArea.getText();
    }

    @Override
    public void takeObject(String object) {
        textArea.setText(object);
    }

    @Override
    public void setEditableForComponent(Boolean value) {
        textArea.setEditable(value);
        //обеспечим вид при доступно/недоступно редактированию
        if (textArea.isEditable()) {
            textArea.setBackground(new Color(255, 255, 255));
        } else {
            textArea.setBackground(new Color(235, 235, 235));
        }
    }
}

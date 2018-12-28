package kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.example;

import kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.TreeNodeable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Пример класса для демонстрации TreePanel
 */
class SampleTreeNodeable implements TreeNodeable {

    String name;
    List<TreeNodeable> subs;

    public SampleTreeNodeable() {
        subs = new ArrayList<>();
    }

    @Override
    public List<TreeNodeable> getChilds() {
        return subs;
    }

    @Override
    public Component getDefaultViewForTree() {
        JLabel label = new JLabel(name);
        if (name.contains("1")) {
            label.setForeground(new Color(0xB8004E));
        } else {
            label.setForeground(new Color(0x4CB888));
        }
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x0C1F7D)));
        panel.add(label);
        return panel;
    }

    @Override
    public Component getSelectedViewForTree() {
        JLabel label = new JLabel(name);
        if (name.contains("1")) {
            label.setForeground(new Color(0xB8004E));
        } else {
            label.setForeground(new Color(0x1E3FB8));
        }
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x88C08C));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x0C1F7D)));
        panel.add(label);
        return panel;
    }

    @Override
    public String toString() {
        return "SampleTreeNodeable1: " + name;
    }
}

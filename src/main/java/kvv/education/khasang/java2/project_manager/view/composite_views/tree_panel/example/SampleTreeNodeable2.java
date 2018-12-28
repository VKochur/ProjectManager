package kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.example;

import kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.TreeNodeable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Пример класса для демонстрации TreePanel
 */
class SampleTreeNodeable2 implements TreeNodeable {

    String name;
    List<TreeNodeable> subs;

    public SampleTreeNodeable2() {
        subs = new ArrayList<>();
    }

    @Override
    public List<TreeNodeable> getChilds() {
        return subs;
    }

    @Override
    public Component getDefaultViewForTree() {
        JLabel label = new JLabel(name);
        return label;
    }

    @Override
    public Component getSelectedViewForTree() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(name));
        panel.setPreferredSize(new Dimension(50, 40));
        return panel;
    }

    @Override
    public String toString() {
        return "SampleTreeNodeable2: " + name;
    }
}

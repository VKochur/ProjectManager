package kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.example;

import kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.TreeNodeable;
import kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.TreePanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Демонстрация работы с TreePanel
 */
public class SampleTreePanel extends JFrame {

    SampleTreeNodeable root;

    //колво созданных новых элементов
    private int newEltsCount;

    public SampleTreePanel() {

        setTitle("Пример панели с tree");
        initContent();
        JLabel label = new JLabel();
        TreePanel treePanel = new TreePanel(root) {
            @Override
            public void afterChangeCurrentTreeNode() {
                if (getCurrentTreeNodeable() == null) {
                    label.setText("Не выбран текущий элемент");
                } else {
                    label.setText("Текущий элемент: " + getCurrentTreeNodeable().toString() + "; Колво потомков: " + getCurrentTreeNode().getChildCount());
                }
            }
        };
        add(treePanel);

        add(label, BorderLayout.SOUTH);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel, BorderLayout.WEST);

        setSize(900, 600);

        //поменять дерево
        JButton restart = new JButton("Новое дерево");
        panel.add(restart);
        restart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initContent();
                treePanel.setRoot(root);
            }
        });

        //удалить текущую
        JButton deleteCurrent = new JButton("Удалить текущий");
        panel.add(deleteCurrent);
        deleteCurrent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //удаление текущего элемента
                TreeNodeable current = treePanel.getCurrentTreeNodeable();
                TreeNodeable parent = treePanel.getParentCurrentTreeNodeable();
                parent.getChilds().remove(current);
                //перестраиваем
                treePanel.updateBranchFromParentOfCurrentNode();
            }
        });

        //удалить 1 в подчинении
        JButton deleteFirstChild = new JButton("Удалить 1 в подчинении");
        panel.add(deleteFirstChild);
        deleteFirstChild.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //удаление 1го в подчинении объекта
                TreeNodeable current = treePanel.getCurrentTreeNodeable();
                List<TreeNodeable> childs = current.getChilds();
                if (!childs.isEmpty()) {
                    current.getChilds().remove(0);
                }
                //перестраиваем
                treePanel.updateBranchFromCurrentNode();
            }
        });

        //добавить новый
        JButton addNew = new JButton("Добавить новый в подчинении");
        panel.add(addNew);
        addNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //добавление нового
                TreeNodeable current = treePanel.getCurrentTreeNodeable();
                List<TreeNodeable> childs = current.getChilds();
                TreeNodeable newEl = createNewELt();
                childs.add(newEl);
                //перестраиваем
                treePanel.updateBranchFromCurrentNode();
                //позиционируемся на новом (добавился то он в конец)
                DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) treePanel.getCurrentTreeNode().getLastChild();
                treePanel.setCurrentTreeNode(newNode);
            }
        });

        //снять выделение. т.е. обнулить инфо о текущем
        JButton setZeroCurrent = new JButton("Снять инфо о текущем");
        panel.add(setZeroCurrent);
        setZeroCurrent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treePanel.setToZeroCurrent();
            }
        });

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //развернуть текущий
        JButton expandCurrent = new JButton("Развернуть текущий");
        panel.add(expandCurrent);
        expandCurrent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treePanel.expandCurrentTreeNode();
            }
        });

        //свернуть текущий
        JButton colapseCurrent = new JButton("Cвернуть текущий");
        panel.add(colapseCurrent);
        colapseCurrent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                treePanel.collapseCurrentTreeNode();
            }
        });
    }

    //элемент для добавления
    private TreeNodeable createNewELt() {
        SampleTreeNodeable sampleTreeNodeable = new SampleTreeNodeable();
        sampleTreeNodeable.name = "new " + String.valueOf(newEltsCount++);
        return sampleTreeNodeable;
    }

    private void initContent() {
        if (root != null) {
        }
        root = new SampleTreeNodeable();
        root.name = "здесь все начнется по кругу";
        for (int i = 0; i < 5; i++) {
            SampleTreeNodeable level1 = new SampleTreeNodeable();
            level1.name = String.valueOf(i);
            for (int j = 0; j < 3; j++) {
                SampleTreeNodeable2 level2 = new SampleTreeNodeable2();
                level2.name = String.format("%d -> %d", i, j);
                level1.getChilds().add(level2);
                for (int k = 0; k < 2; k++) {
                    SampleTreeNodeable level3 = new SampleTreeNodeable();
                    level3.name = String.format("%d -> %d -> %d", i, j, k);
                    level2.getChilds().add(level3);
                }
                //и зациклим все на начало
                level2.getChilds().get(level2.getChilds().size() - 1).getChilds().add(root);
            }
            root.getChilds().add(level1);
        }
    }

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                new SampleTreePanel();
            }
        });
    }
}



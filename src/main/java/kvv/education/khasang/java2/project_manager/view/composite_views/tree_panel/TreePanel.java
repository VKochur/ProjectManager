package kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;


/**
 * Панель по отображению дерева вложенных объектов instanceof TreeNodeable
 * <p>
 * По клику по узлу, структура дерева обновляется в соответствии с текущей ситуацией,
 * т.е. если были изменены TreeNodeable с момента последнего обновления ветки, то это отобразится в дереве
 * <p>
 * Чтобы посмотреть структуру ветки, необходимо кликнуть по ней (это построит ее содержимое в соответствии с текущей ситуацией)
 * Двойной клик по узлу помимо перестройки содержимого, также открывает ветку до следующего уровня
 * <p>
 * Если ветка была открыта то чтобы свернуть ее, но не перестраивать не надо кликать по самим узлам ветки,
 * необходимо кликать по "ниткам", показывающим связи. Тогда ветку можно свернуть и развернуть без ее перестроения.
 * <p>
 * Пример использования -  SampleTreePanel
 */
public class TreePanel extends JPanel {
    private static final int SECOND_LEVEL = 2;
    private static final int FIRST_LEVEL = 1;

    //корень
    private TreeNodeable root;
    DefaultMutableTreeNode rootMutableTreeNode;
    JScrollPane treeScrollPane;
    JTree tree;
    private DefaultMutableTreeNode currentTreeNode;
    private DefaultTreeCellRenderer renderer;

    public TreePanel() {
        super(new BorderLayout());
    }

    public TreePanel(TreeNodeable root) {
        this();
        setRoot(root);
    }

    /**
     * Устанавливает корень дерева в панель отображения
     *
     * @param root
     */
    public void setRoot(TreeNodeable root) {
        this.root = root;
        rootMutableTreeNode = new DefaultMutableTreeNode(root);
        tree = new JTree(rootMutableTreeNode);
        if (treeScrollPane != null) {
            remove(treeScrollPane);
            updateUI();
        }
        treeScrollPane = new JScrollPane(tree);
        add(treeScrollPane);
        renderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
                TreeNodeable treeNodeable = (TreeNodeable) treeNode.getUserObject();
                //возвращаемый для отображения объекта
                Component component = (value == currentTreeNode) ? treeNodeable.getSelectedViewForTree() : treeNodeable.getDefaultViewForTree();
                return component;
            }
        };

        tree.setCellRenderer(renderer);

        //вынесем отдельно события обработки действий мыши для удобства переопределения
        tree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                onMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                onMouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                onMouseExited(e);
            }
        });
    }

    public void onMouseExited(MouseEvent e) {
    }

    public void onMouseEntered(MouseEvent e) {
    }

    public void onMouseReleased(MouseEvent e) {
    }

    public void onMousePressed(MouseEvent e) {

    }

    public void onMouseClicked(MouseEvent e) {
        int selRow = tree.getRowForLocation(e.getX(), e.getY());
        if (selRow != -1) {
            TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

            //меняем значение текущего узла
            setCurrentTreeNode(currentNode);

            //строим ветку с текущего узла
            updateBranchFromCurrentNode();
        }
    }

    /**
     * Строим в дереве ветку начинающуюся с treeNode. Та ветка которая была, удаляется. т.е. информация обновляется
     * Важно! : в качестве объекта связанного с treeNode д.б. instanceof TreeNodeable
     *
     * @param treeNode
     */
    private void updateBranch(DefaultMutableTreeNode treeNode) {
        {
            List<TreeNodeable> treeNodeables;
            //стоим на каком-то узле
            if (treeNode.getUserObject() instanceof TreeNodeable) {
                TreeNodeable treeNodeable = (TreeNodeable) treeNode.getUserObject();
                //определим подузлы
                treeNodeables = treeNodeable.getChilds();
                //создаем ветку подзадач, до 2го уровня вложенности, чтобы в дереве сразу отобразились подзадачи как ветки или листы
                createBranch(treeNode, treeNodeables, SECOND_LEVEL);

            } else {
                throw new IllegalArgumentException("Не корректное использование метода. treeNode д.б. instanceof TreeNodeable");
            }
        }
    }

    /**
     * Обновление ветки исходящую из указанного узла, в соответствии с указанными подузлами
     *
     * @param root          узел
     * @param treeNodeables ветка подузлов
     * @param depth         глубина разворачивания узла на подзадачи.
     */
    private void createBranch(DefaultMutableTreeNode root, List<? extends TreeNodeable> treeNodeables, int depth) {
        //чистим узел от предыдущей инфо, при этом если при чистке удаляем узел считающийся текущим, то текущий узел обнуляем
        for (int i = root.getChildCount() - 1; i >= 0; i--) {
            if (root.getChildAt(i) == getCurrentTreeNode()) {
                setCurrentTreeNode(null);
            }
            root.remove(i);
        }

        //добавляем задачи
        for (TreeNodeable treeNodeable : treeNodeables) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(treeNodeable);
            root.add(child);
            if (depth > FIRST_LEVEL) {
                List<TreeNodeable> subTreeNodeables;
                subTreeNodeables = treeNodeable.getChilds();
                createBranch(child, subTreeNodeables, depth - 1);
            }
        }
        //обновляем изображение
        tree.updateUI();
    }

    /**
     * Действия перед изменением текущего узла в дереве. Может быть использована для переопределения.
     */
    public void beforeChangeCurrentTreeNode() {

    }

    /**
     * Действия после изменения текущего узла в дереве. Может быть использована для переопределения.
     */
    public void afterChangeCurrentTreeNode() {

    }


    /**
     * Текущий элемент в дереве
     * Здесь инкапсулировано доставание объекта из текущего узла
     *
     * @return null, если не указан текущий узел
     */
    public TreeNodeable getCurrentTreeNodeable() {
        if (currentTreeNode == null) {
            return null;
        } else {
            return (TreeNodeable) (currentTreeNode).getUserObject();
        }
    }

    /**
     * Родительский элемент для текущего в дереве
     * Здесь инкапсулировано получение родительского узла и получение объекта из него
     *
     * @return null, если не указан текущий узел, или у текущего узла нет родителя
     */
    public TreeNodeable getParentCurrentTreeNodeable() {
        if (currentTreeNode == null) {
            return null;
        }
        TreeNode parent = currentTreeNode.getParent();
        if (parent != null) {
            return (TreeNodeable) ((DefaultMutableTreeNode) parent).getUserObject();
        } else {
            return null;
        }
    }

    /**
     * Текущий узел
     *
     * @return
     */
    public DefaultMutableTreeNode getCurrentTreeNode() {
        return currentTreeNode;
    }

    public void setCurrentTreeNode(DefaultMutableTreeNode currentTreeNode) {
        beforeChangeCurrentTreeNode();
        this.currentTreeNode = currentTreeNode;
        afterChangeCurrentTreeNode();
    }

    /**
     * Обнулить информацию о текущем узле. т.е. снять метку текущего
     */
    public void setToZeroCurrent() {
        setCurrentTreeNode(null);
        tree.updateUI();
    }

    /**
     * Получить объект на вершине
     *
     * @return
     */
    public TreeNodeable getRoot() {
        return root;
    }

    /**
     * Получить верхний узел (корень)
     *
     * @return
     */
    public DefaultMutableTreeNode getRootMutableTreeNode() {
        return rootMutableTreeNode;
    }

    /**
     * Обновляет ветку с узла являющегося родительским по отношению к текущему
     */
    public void updateBranchFromParentOfCurrentNode() {
        //а есть ли вообще текущиий выбранный узел
        if (getCurrentTreeNode() != null) {
            //а есть ли родитель у текущего узла
            TreeNode parent = getCurrentTreeNode().getParent();
            if (parent != null) {
                updateBranch((DefaultMutableTreeNode) parent);
            }
        }
    }

    /**
     * Обновляет ветку с текущего узла
     */
    public void updateBranchFromCurrentNode() {
        //а есть ли текущий выбранный узел
        if (getCurrentTreeNode() != null) {
            updateBranch(getCurrentTreeNode());
        }
    }

    /**
     * Разворачивает текущий узел (не строит, в разворачивает. Строит updateBranchFromCurrentNode)
     */
    public void expandCurrentTreeNode() {
        DefaultMutableTreeNode defaultMutableTreeNode = getCurrentTreeNode();
        if (defaultMutableTreeNode != null) {
            TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
            tree.expandPath(treePath);
        }
    }

    /**
     * Сворачивает текущий узел
     */
    public void collapseCurrentTreeNode() {
        DefaultMutableTreeNode defaultMutableTreeNode = getCurrentTreeNode();
        if (defaultMutableTreeNode != null) {
            TreePath treePath = new TreePath(defaultMutableTreeNode.getPath());
            tree.collapsePath(treePath);
        }
    }


}

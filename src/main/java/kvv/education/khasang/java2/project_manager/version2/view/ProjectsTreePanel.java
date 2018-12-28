package kvv.education.khasang.java2.project_manager.version2.view;

import kvv.education.khasang.java2.project_manager.model.entity.Curator;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.version2.Controller;
import kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.TreePanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Панель дерева проектов
 */
public class ProjectsTreePanel extends JPanel {

    //контроллер в моделе MVC
    private Controller controller;

    //разделитель панели дерева и информационной панели
    private JSplitPane jSplitPane;
    //панель дерева
    private TreePanel treePanel;
    //информационная панель о текущей задаче
    private JPanel currentTaskPanel;

    private JPanel aboutTaskPanel;
    //панель отображения/редактирования текущей задачи
    private TaskPropertiesPanel taskPropertiesPanel;
    //панель для выбора/отображения куратора выборанной задачи
    private JPanel comboCuratorsPanel;
    private JComboBox<Curator> curatorsCombo;
    private JLabel curatorLabel;

    /**
     * @param controller контроллер, содержащий в т.числе инфо об используемой бд
     * @throws SQLException
     */
    public ProjectsTreePanel(Controller controller) throws SQLException {
        setLayout(new BorderLayout());
        //связываем контроллер (в части работы с деревом проектов) с данной панелью
        this.controller = controller;
        controller.setProjectsTreePanel(this);
        //создаем информационную панель
        configInfoPanel();
        //создаем панель дерева проектов
        configTreePanel();
        //создаем меню
        configMenu();
        jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        add(jSplitPane);
        jSplitPane.setLeftComponent(treePanel);
        jSplitPane.setRightComponent(currentTaskPanel);
        jSplitPane.setDividerSize(3);
        jSplitPane.setResizeWeight(0.85);
        setVisible(true);
    }

    /**
     * Создание панели дерева
     */
    private void configTreePanel() {
        /**
         * Переопределяем метод выполняющийся при перевыборе в панеле дереве элемента
         */
        treePanel = new TreePanel() {
            @Override
            public void afterChangeCurrentTreeNode() {
                //передаем информацию о выделенной задаче и корне дерева
                controller.takeCurrentTaskInTree(getCurrentTreeNodeable());
            }
        };
        treePanel.setPreferredSize(new Dimension(600, 400));
        //установим в панель узел с которого все начнется
        treePanel.setRoot(controller.getRootForTree());
        //сделаем текущим верхний узел
        treePanel.setCurrentTreeNode(treePanel.getRootMutableTreeNode());
        //перестроим ветку с текущего узла
        treePanel.updateBranchFromCurrentNode();
        //и надо бы ее еще развернуть, чтобы сразу видеть проекты
        treePanel.expandCurrentTreeNode();
    }

    /**
     * Создание информационной панели
     *
     * @throws SQLException
     */
    private void configInfoPanel() throws SQLException {
        currentTaskPanel = new JPanel();
        currentTaskPanel.setLayout(new BorderLayout());
        //панель отображения/редактирования свойств
        aboutTaskPanel = new JPanel();
        currentTaskPanel.add(aboutTaskPanel, BorderLayout.CENTER);
        aboutTaskPanel.setLayout(new BoxLayout(aboutTaskPanel, BoxLayout.Y_AXIS));
        taskPropertiesPanel = TaskPropertiesPanel.getInstance();
        //закроем для редактирование поле id
        taskPropertiesPanel.setUnabled(TaskPropertiesPanel.ID_TASK, false);
        aboutTaskPanel.add(taskPropertiesPanel);
        curatorsCombo = new JComboBox<>();
        curatorsCombo.setMaximumSize((new Dimension(Integer.MAX_VALUE, 25)));
        //актуализируем инфо по кураторам в бд
        updateContentCuratorsCombo();
        //панель для выбора куратора
        comboCuratorsPanel = new JPanel();
        comboCuratorsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        curatorLabel = new JLabel(Decor.LABEL_CURATOR_TEXT);
        comboCuratorsPanel.add(curatorLabel);
        comboCuratorsPanel.add(curatorsCombo);
        aboutTaskPanel.add(comboCuratorsPanel);
    }

    /**
     * Создание меню
     */
    private void configMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu(Decor.CMD_MENU);
        menuBar.add(menu);

        JMenuItem add = new JMenuItem(Decor.CMD_CREATE_TASK);
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                creatingTaskByInfoPanel();
            }
        });
        menu.add(add);

        JMenuItem update = new JMenuItem(Decor.CMD_UPDATE_TASK);
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatingTaskByInfoPanel();
            }
        });
        menu.add(update);

        JMenuItem del = new JMenuItem(Decor.CMD_DEL_TASK);
        del.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTask();
            }
        });
        menu.add(del);

        add(menuBar, BorderLayout.NORTH);
    }

    /**
     * Удаление текущей выденной задачи
     */
    private void deleteSelectedTask() {
        //делаем только если выделена задача
        if (selectedInTreeIsTask()) {
            Task currentTask = ((TaskForTreePanel) treePanel.getCurrentTreeNodeable()).getTask();
            controller.delTask(currentTask);
            //узел родителя
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treePanel.getCurrentTreeNode().getParent();
            //устанавливаем текущий на узел родителя
            treePanel.setCurrentTreeNode(parentNode);
            //перестраиваем ветку
            treePanel.updateBranchFromCurrentNode();
            //раскрываем ветку
            treePanel.expandCurrentTreeNode();
        }
    }

    /**
     * Изменение выделенной в дереве задачи, согласно данным введеным на инфо панеле
     */
    private void updatingTaskByInfoPanel() {
        //если введеные данные проходят проверку
        if (validUserDate()) {
            //запомним родителя текущего объекта
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) treePanel.getCurrentTreeNode().getParent();
            //создадим образец задачи по данным указанным на панеле свойст
            Task exampleForEditTask = taskPropertiesPanel.getObjectInstance();
            exampleForEditTask.setCurator((Curator) curatorsCombo.getSelectedItem());
            //отредактируем текущую задачу по образу данных указанных на панеле свойств задачи
            controller.editTask(exampleForEditTask);
            //перестраиваем ветку исходящую из родителя, чтобы перестроилось в дереве название
            treePanel.updateBranchFromParentOfCurrentNode();
            //позиционируемся на родителе чтобы его раскрыть
            treePanel.setCurrentTreeNode(parentNode);
            //раскрываем текущую ветку
            treePanel.expandCurrentTreeNode();
            //позиционируемся на объекте, который меняли
            //для этого найдем по по id задачи, которая менялась
            treePanel.setCurrentTreeNode(findTreeNodeByTaskId(parentNode, exampleForEditTask.getId()));
            //перестраиваем ветку из измененной задачи
            treePanel.updateBranchFromCurrentNode();
        }
    }

    /**
     * Создание новой задачи, по данным введеным на инфо панеле
     */
    private void creatingTaskByInfoPanel() {
        //если введеные данные проходят проверку
        if (validUserDate()) {
            Task exampleForNewTask = taskPropertiesPanel.getObjectInstance();
            exampleForNewTask.setCurator((Curator) curatorsCombo.getSelectedItem());
            //создадим новую в подчинении к текущей задачу по образу данных указанных на панеле свойств задачи
            int idNewTask = controller.createNewTask(treePanel.getCurrentTreeNodeable(), exampleForNewTask);
            //перестраиваем ветку из текущего узла
            treePanel.updateBranchFromCurrentNode();
            //разворачиваем текущий узел
            treePanel.expandCurrentTreeNode();
            //позиционируемся на новой задаче, предварительно найдя ее по id
            treePanel.setCurrentTreeNode(findTreeNodeByTaskId(treePanel.getCurrentTreeNode(), idNewTask));
            //перестраиваем ветку из созданной задачи
            treePanel.updateBranchFromCurrentNode();
        }
    }

    /**
     * Проверка корректности введеных данных
     * всегда д.б введены название задачи, срок начала, длительность, статус
     *
     * @return
     */
    private boolean validUserDate() {
        Task example;
        try {
            //получаем задачу на основе введеных данных
            example = taskPropertiesPanel.getObjectInstance();
        } catch (Exception e) {
            showMsg(Decor.NOT_VALID_USERS_DATA + e.getMessage());
            return false;
        }

        if (example.getName().isEmpty()) {
            showMsg(Decor.NOT_VALID_USERS_DATA_NEED_NAME_TASK);
            return false;
        }

        if (example.getStatus() == null) {
            showMsg(Decor.NOT_VALID_USERS_DATA_NEED_STATUS);
            return false;
        }

        if (example.getStarted() == null) {
            showMsg(Decor.NOT_VALID_USERS_DATA_NEED_DATE);
            return false;
        }

        if (example.getDuring() < 1) {
            showMsg(Decor.NOT_VALID_USERS_DATA_NEED_DURING);
            return false;
        }

        //проверки прошли успешно
        return true;
    }

    private void showMsg(String notValidUsersDataNeedDuring) {
        JOptionPane.showMessageDialog(this, notValidUsersDataNeedDuring);
    }

    /**
     * Проверка является ли выбранный элемент в дереве задачей
     *
     * @return
     */
    public boolean selectedInTreeIsTask() {
        return (treePanel.getCurrentTreeNodeable() != null && treePanel.getCurrentTreeNodeable() != treePanel.getRoot());
    }

    /**
     * Актуализация в комбо списка кураторов
     *
     * @throws SQLException
     */
    private void updateContentCuratorsCombo() throws SQLException {
        Curator current = (Curator) curatorsCombo.getSelectedItem();
        List<Curator> curators = controller.getCuratorsForChoose();
        curatorsCombo.removeAllItems();
        for (Curator curator : curators) {
            curatorsCombo.addItem(curator);
        }

        //установим ранее установленное значение
        int count = curatorsCombo.getItemCount();
        for (int i = 0; i < count; i++) {
            if (curatorsCombo.getItemAt(i) == current) {
                curatorsCombo.setSelectedIndex(i);
            }
        }
    }

    /**
     * Показать на панеле свойств информацию по указанной задаче
     *
     * @param currentTaskInTree
     */
    public void showInfoAbout(Task currentTaskInTree) {
        if (currentTaskInTree == null) {
            cleanInfoPanel();
        } else {
            Curator curator = currentTaskInTree.getCurator();
            //покажем в комбо куратора
            curatorsCombo.setSelectedItem(curator);
            //покажем свойства на панеле
            taskPropertiesPanel.showOnPanel(currentTaskInTree);
        }
    }

    /**
     * Очистить всю информацию на панеле свойств
     */
    private void cleanInfoPanel() {
        //очистим информацию по текущему куратору в комбо
        curatorsCombo.setSelectedItem(null);
        //очистим панель свойств задачи, передав туда задачу "пустышку"
        taskPropertiesPanel.showOnPanel(new Task());
    }

    /**
     * Ищет узел содержащий задачу с укаанным id внутри заданного узла на 1м уровне вложенности
     *
     * @param idTask
     * @return узел дерева, содержащий задачу с указанным id, null в случае не нахождения.
     * <p>
     * Важно: узел ищется только на 1м уровне вложенности указанного узла. Если узел с искомой задачей не является прямым потомком указанного узла, он не найдется.
     */
    private DefaultMutableTreeNode findTreeNodeByTaskId(DefaultMutableTreeNode parentNode, int idTask) {
        int countChilds = parentNode.getChildCount();
        for (int i = 0; i < countChilds; i++) {
            DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            TaskForTreePanel currentWrappleTask = (TaskForTreePanel) currentChild.getUserObject();
            Task currentTask = currentWrappleTask.getTask();
            if (currentTask.getId() == idTask) {
                return currentChild;
            }
        }
        return null;
    }

    /**
     * Панель дерева
     *
     * @return
     */
    public TreePanel getTreePanel() {
        return treePanel;
    }

    public void getMsgExcp(String message) {
    }
}

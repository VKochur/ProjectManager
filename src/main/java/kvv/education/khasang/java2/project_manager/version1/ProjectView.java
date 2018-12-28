package kvv.education.khasang.java2.project_manager.version1;

import kvv.education.khasang.java2.project_manager.model.*;
import kvv.education.khasang.java2.project_manager.model.selections.Selection;

import javax.swing.*;

import javax.swing.tree.*;

import java.awt.*;

import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Вью менеджера проектов
 * Depricated, используйте kvv.education.khasang.java2.project_manager.version2.View
 */
@Deprecated
public class ProjectView extends JFrame {

    //уровни вложенности разворачивания дерева задач
    private static final int SECOND_LEVEL = 2;
    private static final int FIRST_LEVEL = 1;
    private static final String BUTTON_TITLE = "Получить выборку";
    private static final String ERROR_CONDITION_MSG = "Ошибка в указанных параметрах";
    private static final Color ERROR_MSG_COLOR = new Color(0x95080A);
    private static final Color MSG_COLOR = new Color(0x0B1A95);
    private static final String TEXT_MSG = "Количество строк: ";
    private static final String BORDER_PROPERTIES_TEXT = "Свойства";
    private static final Color OVERDUE_TASK_COLOR = new Color(0x761005);
    private static final Color OPEN_TASK_COLOR = new Color(0x010E67);
    private static final Color CLOSED_TASK_COLOR = new Color(0x095A00);
    private static final String CONDIT_TITLE_CRITERIA = "Параметр выборки";
    private static final String CONDIT_TITLE_VALUE = "Значение";
    private static final Color CONDIT_TABLE_COLOR = new Color(0x13712D);
    private static final String PROPER_TITLE_KEY = "Свойство";
    private static final String PROPER_TITLE_VALUE = "Значение";
    private static final String MAIN_TITLE = "Менеджер проектов";


    private JSplitPane splitSelectionPane;
    private JTree treeTasks;
    private DefaultMutableTreeNode root;
    private DefaultTreeCellRenderer renderer;
    private JPanel panelForSelection;
    private JComboBox<TasksProducer> filtersForTree;
    private JTable conditionsForSelection;
    private JButton runSelectionButton;
    private JTable tableSelectionResults;
    private JPanel panelForTree;
    private JComboBox<Selection> selections;
    private JPanel capSelectionPanel;
    private JLabel aboutSelectionResults;
    private JPanel panelForPaneSelectionResults;
    private JScrollPane scrollPaneSelectionResults;
    private JScrollPane scrollPaneSelectionConditions;
    private JPanel panelPropertiesTask;
    private JSplitPane splitPropertiesPane;
    private JTable tableProperties;
    private JScrollPane scrollPaneProperties;
    private JPanel northOfCapSelectionPanel;

    private Controller controller;

    public ProjectView(Controller controller) throws HeadlessException {
        setTitle(MAIN_TITLE);
        setController(controller);
        controller.setProjectView(this);
        setLocation(200, 200);
        setMinimumSize(new Dimension(500, 400));
        setSize(700, 600);
        initJComponents();

        splitSelectionPane.setDividerSize(2);
        splitSelectionPane.setResizeWeight(0.65);
        add(splitSelectionPane);
        configPanelTree();
        configPanelSelection();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Инициализация компонентов
     */
    private void initJComponents() {
        splitSelectionPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        treeTasks = new JTree();
        panelForSelection = new JPanel();
        filtersForTree = new JComboBox<>();
        panelForPaneSelectionResults = new JPanel();
        panelPropertiesTask = new JPanel();
        splitPropertiesPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        scrollPaneSelectionConditions = new JScrollPane();
        aboutSelectionResults = new JLabel();
        selections = new JComboBox<>();
        scrollPaneSelectionResults = new JScrollPane();
        runSelectionButton = new JButton(BUTTON_TITLE);
        northOfCapSelectionPanel = new JPanel();
        capSelectionPanel = new JPanel();
        panelForTree = new JPanel();
        scrollPaneProperties = new JScrollPane();
    }

    /**
     * Конфигурирование панели содержащей инфомацию о производимых выборках
     */
    private void configPanelSelection() {
        splitSelectionPane.setRightComponent(panelForSelection);
        //указываем мин размер панели
        panelForSelection.setPreferredSize(new Dimension(0, 300));
        panelForSelection.setLayout(new BorderLayout());

        capSelectionPanel.setLayout(new BorderLayout());
        capSelectionPanel.add(northOfCapSelectionPanel, BorderLayout.NORTH);
        //при указании типа выборки
        //в CENTER панели capSelectionPanel будет встраиваться еще таблица критериев определяющих конкретную выборку
        northOfCapSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        northOfCapSelectionPanel.add(selections);
        northOfCapSelectionPanel.add(runSelectionButton);

        panelForSelection.add(capSelectionPanel, BorderLayout.NORTH);
        panelForSelection.add(panelForPaneSelectionResults, BorderLayout.CENTER);
        panelForPaneSelectionResults.setLayout(new BorderLayout());
        panelForPaneSelectionResults.add(aboutSelectionResults, BorderLayout.SOUTH);

        selections.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Selection selection = (Selection) selections.getSelectedItem();
                controller.setCurrentSelection(selection);
            }
        });

        for (Selection selection : controller.getSelections()) {
            selections.addItem(selection);
        }

        runSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<String, String> args = new HashMap<>();
                int rowCount = conditionsForSelection.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    String key = (String) conditionsForSelection.getValueAt(i, 0);
                    String value = (String) conditionsForSelection.getValueAt(i, 1);
                    args.put(key, value);
                }
                controller.setArgsForSelection(args);

                try {
                    List<String> titles = controller.getSelectionTitlesColumns();
                    List<List<String>> context = null;
                    try {
                        context = controller.getSelectionResults();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    updateTableResults(titles, context, StatusQuery.QUERY_DONE);
                } catch (NumberFormatException e1) {
                    updateTableResults(null, null, StatusQuery.ERROR_QUERY);
                }
            }
        });
    }

    /**
     * Обновляет таблицу результатов выборки
     *
     * @param titles      названия столбцов таблицы
     * @param context     содержание таблицы
     * @param statusQuery информация о том, в результате чего обновляется таблица результатов выборки
     */
    private void updateTableResults(List<String> titles, List<List<String>> context, StatusQuery statusQuery) {
        panelForPaneSelectionResults.remove(scrollPaneSelectionResults);
        switch (statusQuery) {
            case ERROR_QUERY:
                aboutSelectionResults.setForeground(ERROR_MSG_COLOR);
                aboutSelectionResults.setText(ERROR_CONDITION_MSG);
                break;
            case WAITING_RUN:
                aboutSelectionResults.setText("");
                break;
            case QUERY_DONE:
                aboutSelectionResults.setForeground(MSG_COLOR);
                tableSelectionResults = createTableResults(titles, context);
                scrollPaneSelectionResults = new JScrollPane(tableSelectionResults);
                panelForPaneSelectionResults.add(scrollPaneSelectionResults, BorderLayout.CENTER);
                aboutSelectionResults.setText(TEXT_MSG + String.valueOf(tableSelectionResults.getRowCount()));
                break;
            default:
                throw new IllegalArgumentException("Недопустимое значение параметра: " + statusQuery);
        }
        panelForSelection.updateUI();
    }

    /**
     * Создает таблицу результатов выборки
     *
     * @param titles  название столбцов таблицы
     * @param context содержание таблицы
     * @return
     */
    private JTable createTableResults(List<String> titles, List<List<String>> context) {
        Object[] titlesColumn;
        titlesColumn = titles.toArray();
        Object[][] data = new Object[context.size()][titles.size()];
        int numberRow = 0;
        for (List<String> strings : context) {
            data[numberRow] = strings.toArray();
            numberRow++;
        }
        JTable tableResults = new JTable(data, titlesColumn);
        return tableResults;
    }

    /**
     * Конфигурирование панели с информацией по дереву задач
     */
    private void configPanelTree() {
        panelForTree.setLayout(new BorderLayout());
        splitSelectionPane.setLeftComponent(panelForTree);
        addTree();
        addFilters();
        addProperties();
    }

    /**
     * Добавление панели свойств
     */
    private void addProperties() {
        panelPropertiesTask.setLayout(new BorderLayout());
        panelPropertiesTask.setBorder(BorderFactory.createTitledBorder(BORDER_PROPERTIES_TEXT));
        //установить мин размеры панели
        panelPropertiesTask.setPreferredSize(new Dimension(120, 0));
        splitPropertiesPane.setRightComponent(panelPropertiesTask);
    }

    /**
     * Добавление фильтров отображения дерева задач
     */
    private void addFilters() {
        filtersForTree = new JComboBox<>();
        filtersForTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setCurrentTasksProducer((TasksProducer) filtersForTree.getSelectedItem());
                List<TaskInDB> tasks2 = new ArrayList<>();
                try {
                    tasks2 = controller.getCurrentTasksProducer().getTasks();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                updateTree(root, tasks2, SECOND_LEVEL);
                treeTasks.expandRow(0);
                //обновляем изображение
                treeTasks.updateUI();
            }
        });
        buildFilters();
        panelForTree.add(filtersForTree, BorderLayout.NORTH);
    }

    /**
     * Добавление дерева задач
     */
    private void addTree() {
        root = new DefaultMutableTreeNode("...");
        treeTasks = new JTree(root);
        splitPropertiesPane.setDividerSize(2);
        splitPropertiesPane.setResizeWeight(0.5);
        panelForTree.add(splitPropertiesPane, BorderLayout.CENTER);
        splitPropertiesPane.setLeftComponent(new JScrollPane(treeTasks));
        renderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                //возвращаемый для отображения объект по умолчанию, в дальнейшем поменяем
                Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
                if (treeNode.getUserObject() instanceof TaskInDB) {
                    TaskInDB taskInDB = (TaskInDB) treeNode.getUserObject();
                    ((DefaultTreeCellRenderer) component).setText(getView(taskInDB));
                    if (taskInDB.getStatus() == Status.OPEN) {
                        if (taskInDB.overdure()) {
                            component.setForeground(OVERDUE_TASK_COLOR);
                        } else {
                            component.setForeground(OPEN_TASK_COLOR);
                        }
                    } else {
                        component.setForeground(CLOSED_TASK_COLOR);
                    }
                }
                return component;
            }
        };
        treeTasks.setCellRenderer(renderer);
        treeTasks.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selRow = treeTasks.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = treeTasks.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
                    List<TaskInDB> subtasks;
                    if (selPath.getPathCount() > 1) {
                        //стоим на какой-то задаче
                        TaskInDB task = (TaskInDB) currentNode.getUserObject();
                        //определим подзадачи
                        subtasks = controller.getSubTasks(task);
                        //обновим инфо о свойствах
                        Map<String, String> properties = controller.getProperties(task);
                        updateProperties(properties);
                    } else {
                        //стоим на корне дерева
                        //получение списка задач по используемому фильтру
                        subtasks = new ArrayList<>();
                        try {
                            subtasks = ((TasksProducer) filtersForTree.getSelectedItem()).getTasks();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                        //обнуляем свойства
                        updateProperties(Collections.EMPTY_MAP);
                    }
                    //создаем ветку подзадач, до 2го уровня вложенности, чтобы в дереве сразу отобразились подзадачи как ветки или листы
                    updateTree(currentNode, subtasks, SECOND_LEVEL);
                }
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

            }
        });
    }


    private void buildFilters() {
        for (TasksProducer answer : controller.getTasksProducers()) {
            filtersForTree.addItem(answer);
        }
    }

    private String getView(TaskInDB taskInDB) {
        return String.format("%s (Старт задачи:%td.%tm.%ty, Длительность %d дней)", taskInDB.getName(), taskInDB.getStarted(), taskInDB.getStarted(), taskInDB.getStarted(), taskInDB.getDuring());
    }


    /**
     * Обновление ветки задач исходящую из указанного узла, в соответствии с указанными подзадачами
     *
     * @param root  узел
     * @param tasks ветка подзадач
     * @param depth глубина разворачивания узла на подзадачи.
     */
    private void updateTree(DefaultMutableTreeNode root, List<TaskInDB> tasks, int depth) {
        //чистим узел от предыдущей инфо
        root.removeAllChildren();
        //добавляем задачи
        for (TaskInDB task : tasks) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode(task);
            root.add(child);
            if (depth > FIRST_LEVEL) {
                List<TaskInDB> tasks1 = controller.getSubTasks(task);
                updateTree(child, tasks1, depth - 1);
            }
        }
        //обновляем изображение
        treeTasks.updateUI();
    }

    //пакетный доступ
    void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Обновление панели с инфо по параметрам выборки
     *
     * @param currentSelection
     */
    public void updateSelection(Selection currentSelection) {
        capSelectionPanel.remove(scrollPaneSelectionConditions);
        conditionsForSelection = createConditions(currentSelection);
        scrollPaneSelectionConditions = new JScrollPane(conditionsForSelection);
        if (conditionsForSelection.getRowCount() > 0) {
            scrollPaneSelectionConditions.setVisible(true);
            resizeViewTable(conditionsForSelection, 1, 5);
        } else {
            scrollPaneSelectionConditions.setVisible(false);
        }
        capSelectionPanel.add(scrollPaneSelectionConditions);
        capSelectionPanel.updateUI();

        updateTableResults(null, null, StatusQuery.WAITING_RUN);
    }

    /**
     * Конфигурирование отображения таблицы для JScroolPane
     *
     * @param table
     * @param minRows минимальное количество строк таблицы для отображения
     * @param maxRows максимальное количество строк таблицы для отображения
     */
    private void resizeViewTable(JTable table, int minRows, int maxRows) {
        int needRows = (table.getRowCount() > maxRows) ? maxRows : (table.getRowCount() < minRows) ? minRows : table.getRowCount();
        table.setPreferredScrollableViewportSize(new Dimension(table.getPreferredScrollableViewportSize().width, table.getRowHeight() * needRows));
    }

    /**
     * Создание таблицы, отображающей параметры выборки
     *
     * @param currentSelection
     * @return
     */
    private JTable createConditions(Selection currentSelection) {
        Map<String, String> maps = currentSelection.getArgs();
        String[] titles = {CONDIT_TITLE_CRITERIA, CONDIT_TITLE_VALUE};
        String[][] context = new String[maps.size()][2];
        Set<Map.Entry<String, String>> entry = maps.entrySet();
        int row = 0;
        for (Map.Entry<String, String> stringStringEntry : entry) {
            context[row] = new String[]{stringStringEntry.getKey(), stringStringEntry.getValue()};
            row++;
        }
        JTable tableConditions = new JTable(context, titles);
        tableConditions.setForeground(CONDIT_TABLE_COLOR);
        return tableConditions;
    }

    /**
     * Обновление панели содержащей информацию о свойствах выбранной задачи
     *
     * @param properties
     */
    private void updateProperties(Map<String, String> properties) {
        panelPropertiesTask.remove(scrollPaneProperties);
        String[] title = {PROPER_TITLE_KEY, PROPER_TITLE_VALUE};
        String[][] context = new String[properties.size()][2];
        Set<Map.Entry<String, String>> entrySet = properties.entrySet();
        int row = 0;
        for (Map.Entry<String, String> entry : entrySet) {
            context[row] = new String[]{entry.getKey(), entry.getValue()};
            row++;
        }
        tableProperties = new JTable(context, title);
        scrollPaneProperties = new JScrollPane(tableProperties);
        panelPropertiesTask.add(scrollPaneProperties);
        panelPropertiesTask.updateUI();
    }

    /**
     * Состояние выполнения выборки
     */
    private enum StatusQuery {
        //ошибка в параметрах запроса
        ERROR_QUERY,
        //ожидание выполнения запроса
        WAITING_RUN,
        //запрос сделан
        QUERY_DONE
    }
}

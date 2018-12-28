package kvv.education.khasang.java2.project_manager.version2;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.entity.*;
import kvv.education.khasang.java2.project_manager.model.selections.*;
import kvv.education.khasang.java2.project_manager.model.selections.version2.*;
import kvv.education.khasang.java2.project_manager.version2.view.Decor;
import kvv.education.khasang.java2.project_manager.version2.view.ProjectsTreePanel;
import kvv.education.khasang.java2.project_manager.version2.view.TaskForTreePanel;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.LinkerFactory;
import kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.TreeNodeable;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Задача Менеджер проектов.
 * Контроллер в моделе MVC как с V = View (когда в качестве V в MVC выступает View ), так и с V = ProjectTreePanel
 * т.е. ProjectTreePanel может выступать как самостоятельная View ( например v = JFrame(); v.add(new ProjectTreePanel(controller))) с данным контроллером
 */
public class Controller {
    //путь к бд
    private final String PATH_DB;
    //выборки используют operatorDB
    private final OperatorDB operatorDB;
    //дерево задачи использует dao
    private final CrudDAO crudDAO;

    //список доступных выборок
    private List<SelectionWrapple> selectionWrapples;
    //текущая выборка
    private SelectionWrapple currentSelection;

    //View в моделе MVC
    private View view;
    //панель отображения дерева проектов
    private ProjectsTreePanel projectsTreePanel;
    //текущая задача, выделеная в дереве проектов
    private Task currentTaskInTree;

    public Controller(String path_db) {
        PATH_DB = path_db;
        operatorDB = new OperatorDB(PATH_DB);
        crudDAO = new CrudDaoJDBC(path_db);
        //определяем список используемых выборок
        initSelections();
    }

    /**
     * @return пуь к используемой бд
     */
    public String getPATH_DB() {
        return PATH_DB;
    }

    /**
     * Установить связь с View
     * доступ пакетный, вызввается из View
     *
     * @param view
     */
    void setView(View view) {
        this.view = view;
        this.projectsTreePanel = view.getProjectsTreePanel();
    }

    /**
     * Инициализация списка доступных выборок
     */
    private void initSelections() {
        this.selectionWrapples = new ArrayList<>();
        selectionWrapples.add(new ActualTasks(new SelectionActualTaskForDate(operatorDB, new Date())));
        selectionWrapples.add(new OverdueTasks(new SelectionOverdueTasksForDate(operatorDB, new Date())));
        selectionWrapples.add(new CuratorsByOverdue(new SelectionCuratorsByOverdueForDate(operatorDB, new Date())));
        selectionWrapples.add(new OpenTasksByCurator(new SelectionOpenTasksByCurator(operatorDB)));
        selectionWrapples.add(new OpenSubtasksCount(new SelectionOpenSubtasksCountByTask(operatorDB)));
    }

    /*
    установка текущей выборки
    пакетный доступ из вью
    */
    void setCurrentSelection(SelectionWrapple currentSelection) {
        this.currentSelection = currentSelection;
        view.changeSelectionPanel();
    }

    /*
     получение текущей выборки
     пакетный доступ из вью
     */

    SelectionWrapple getCurrentSelection() {
        return currentSelection;
    }

    /*
     * получение списка доступных выборок
     * @return
     */
    List<SelectionWrapple> getSelectionWrapples() {
        return selectionWrapples;
    }

    /**
     * Получение полного списка кураторов из базы
     *
     * @return
     * @throws SQLException
     */
    List<Curator> getAllCurators() throws SQLException {
        return crudDAO.getAllCurators();
    }

    /**
     * Получение списка кураторов для выборе в вью (null + полный список кураторов из бд)
     *
     * @return
     * @throws SQLException
     */
    public List<Curator> getCuratorsForChoose() throws SQLException {
        List<Curator> curators = new ArrayList<>();
        //возможность выбора null
        curators.add(null);
        //и всех кураторов из базы
        curators.addAll(getAllCurators());
        return curators;
    }

    /**
     * Корень для дерева проектов
     *
     * @return
     */
    public TreeNodeable getRootForTree() {
        TreeNodeable root = new TaskForTreePanel(null, crudDAO) {

            /**
             * Здесь реализовано получение отображения всех проектов, т.е. в подчинении корню - все имеющиеся проекты
             * @return
             */
            @Override
            public List<TreeNodeable> getChilds() {
                //получим все проекты
                List<Project> allProjects;
                try {
                    allProjects = crudDAO.getAllProjects();
                } catch (SQLException e) {
                    return TaskForTreePanel.forSQLException();
                }

                //обернутые для изображения проекты
                List<TreeNodeable> projectsForTreePanel = new ArrayList<>();

                //обернем их для отображения
                for (Project project : allProjects) {
                    projectsForTreePanel.add(new TaskForTreePanel(project, crudDAO));
                }
                //выдадим список обернутых для изображения проектов
                return projectsForTreePanel;
            }

            @Override
            public JLabel getLabel() {
                return new JLabel(Decor.ROOT_TITLE);
            }
        };

        return root;
    }

    /**
     * Получить выделенную в дереве задачу
     *
     * @param currentTreeNodeable
     */
    public void takeCurrentTaskInTree(TreeNodeable currentTreeNodeable) {
        //есть ли выделенная задача в дереве
        if (!projectsTreePanel.selectedInTreeIsTask()) {
            changeCurrentTaskInTree(null);
        } else {
            changeCurrentTaskInTree(((TaskForTreePanel) currentTreeNodeable).getTask());
        }
    }

    //изменение текущей задачи в контроллере
    private void changeCurrentTaskInTree(Task task) {
        this.currentTaskInTree = task;
        updateInfoPanel();
    }

    //обновление информационной панели с информацией о задаче
    private void updateInfoPanel() {
        projectsTreePanel.showInfoAbout(currentTaskInTree);
    }

    /**
     * Изменение в базе задачи в соответствии с образцом.
     * Образец содержит id задачи, которую следует поменять, значение полей и куратора
     *
     * @param exampleForEditTask
     */
    public void editTask(Task exampleForEditTask) {
        try {
            //изменим значение полей
            crudDAO.updateTaskInDB(exampleForEditTask);
            //изменим значение куратора
            Integer idCurator = (exampleForEditTask.getCurator() == null) ? null : exampleForEditTask.getCurator().getId();
            crudDAO.setCuratorForTask(exampleForEditTask.getId(), idCurator);
        } catch (SQLException e) {
            processingExcp(e);
        }
    }

    public void processingExcp(Exception e) {
        sendMsgExcep(e.getMessage());
    }

    //тут плохо, ловля ексепшн нужно менять

    /**
     * Отправить сообщение об исключительно ситуации
     *
     * @param message
     */
    private void sendMsgExcep(String message) {
        if (view != null) {
            view.getMsgExcp(message);
        }
    }


    /**
     * Создает базе новую задачу в подчинении к указанному узлу в соответствии с образцом (если указанный узел - корень, то создается проект)
     *
     * @param currentTreeNodeable узел в дереве панели projectsTreePanel
     * @param exampleForNewTask   образец для создания подзадачи (образец в части значения полей)
     * @return id вновь созданной задачи, 0 при исключительной ситуации.
     */
    public int createNewTask(TreeNodeable currentTreeNodeable, Task exampleForNewTask) {
        try {
            Task newTask;
            if (currentTreeNodeable == projectsTreePanel.getTreePanel().getRoot()) {
                //если создаем проект, т.е. в подчинению корню дерева
                newTask = crudDAO.createProject(exampleForNewTask.getName(), exampleForNewTask.getStarted(), exampleForNewTask.getDuring(), exampleForNewTask.getStatus());
            } else {
                //создаем задачу
                Task parent = ((TaskForTreePanel) currentTreeNodeable).getTask();
                newTask = crudDAO.createTask(parent, exampleForNewTask.getName(), exampleForNewTask.getStarted(), exampleForNewTask.getDuring(), exampleForNewTask.getStatus());
            }
            //установим значение куратора
            Integer idCurator = (exampleForNewTask.getCurator() == null) ? null : exampleForNewTask.getCurator().getId();
            crudDAO.setCuratorForTask(newTask.getId(), idCurator);
            return newTask.getId();
        } catch (SQLException e) {
            processingExcp(e);
        }
        return 0;
    }

    /**
     * Удаление в базе задачи и всех ее подзадач насквозь
     *
     * @param currentTask
     */
    public void delTask(Task currentTask) {
        try {
            crudDAO.deleteTaskWithSubtasks(currentTask.getId());
        } catch (SQLException e) {
            processingExcp(e);
        }
    }

    public void setProjectsTreePanel(ProjectsTreePanel projectsTreePanel) {
        this.projectsTreePanel = projectsTreePanel;
    }


    /**
     * Фабрика линкеров, связывающих объект с графическим элементом
     * используется для построения панели отображения выборки
     *
     * @return
     */
    public LinkerFactory getLinkerFactory() {
        return new LinkerFactoryForSelection(crudDAO);
    }
}

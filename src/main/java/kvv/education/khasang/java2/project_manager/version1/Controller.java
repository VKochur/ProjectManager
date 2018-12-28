package kvv.education.khasang.java2.project_manager.version1;

import kvv.education.khasang.java2.project_manager.model.*;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.query_results.*;
import kvv.education.khasang.java2.project_manager.model.selections.*;

import java.sql.SQLException;
import java.util.*;

/**
 * Контроллер
 * Deprecated, используйте kvv.education.khasang.java2.project_manager.version2.Controller
 */
@Deprecated
public class Controller {

    //вью
    private ProjectView projectView;

    //выбранный фильтр для заглавных задач в дереве отображения
    private TasksProducer currentTasksProducer;
    //список доступных фильтров для заглавных объектов в дереве отображения
    private List<TasksProducer> tasksProducers;

    //выбранная выборка
    private Selection currentSelection;
    //список доступных выборок
    private List<Selection> selections;

    //тип фабрики инстанцирующей TaskProducer, которым определяются подзадачи в дереве
    private FactoryType factoryForSubtasks;

    //оператов взаимодействия с базой
    private OperatorDB operatorDB;

    public Controller(String pathToDB) {
        //вызываем конструктор предполагающий существование файла БД
        operatorDB = new OperatorDB(pathToDB);

        factoryForSubtasks = FactoryType.FOR_ALL_SUBTASKS;

        tasksProducers = new ArrayList<>();
        selections = new ArrayList<>();

        Date now =  new Date();

        //формирование списка доступных фильтров
        tasksProducers.add(new AllProjects(operatorDB));
        tasksProducers.add(new OpenProjects(operatorDB));
        tasksProducers.add(new OverdueTasksForDate(operatorDB, now));
        tasksProducers.add(new ActualTasksForDate(operatorDB, now));

        //формирование списка доступных выборок
        selections.add(new SelectionOpenProject(operatorDB));
        selections.add(new SelectionOpenSubtasksCountByTask(operatorDB));
        selections.add(new SelectionOpenTasksByCurator(operatorDB));
        selections.add(new SelectionActualTaskForDate(operatorDB, now));
        selections.add(new SelectionOverdueTasksForDate(operatorDB, now));
        selections.add(new SelectionCuratorsByOverdueForDate(operatorDB, now));
    }

    //-------далее пакетный доступ--------

    void setProjectView(ProjectView projectView) {
        this.projectView = projectView;
    }

    List<TasksProducer> getTasksProducers() {
        return tasksProducers;
    }

    TasksProducer getCurrentTasksProducer() {
        return currentTasksProducer;
    }

    void setCurrentTasksProducer(TasksProducer currentTasksProducer) {
        this.currentTasksProducer = currentTasksProducer;
    }

    void setCurrentSelection(Selection currentSelection) {
        this.currentSelection = currentSelection;
        projectView.updateSelection(currentSelection);
    }

    List<Selection> getSelections() {
        return selections;
    }

    void setArgsForSelection(Map<String, String> args) {
        currentSelection.setArgs(args);
    }

    List<String> getSelectionTitlesColumns() {
        return currentSelection.getTitles();
    }

    List<List<String>> getSelectionResults() throws SQLException {
        return currentSelection.getResults();
    }

    /**
     * Возвращает перечень свойств указанной задачи
     *
     * @param task задача, свойства которой нужно получить
     * @return название свойства - его значение
     */
    Map<String, String> getProperties(TaskInDB task) {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("id", String.valueOf(task.getId()));
        properties.put("Задача", String.valueOf(task.getName()));
        properties.put("Дата начала", String.format("%td.%tm.%ty", task.getStarted(), task.getStarted(), task.getStarted()));
        Calendar expectedEndTask = new GregorianCalendar();
        expectedEndTask.setTime(task.getStarted());
        expectedEndTask.add(Calendar.DAY_OF_MONTH, task.getDuring());
        properties.put("Требуемая дата окончания", String.format("%td.%tm.%ty", expectedEndTask, expectedEndTask, expectedEndTask));
        CuratorById curatorById = new CuratorById(operatorDB, task.getIdCurator());
        if (curatorById.getCountRows() == 0) {
            throw new IllegalStateException("Не нашлось ответсвенного с id = " + curatorById.getCuratorId());
        }
        try {
            properties.put("Ответственный", curatorById.getCuratorName() + " (" + curatorById.getCuratorContact() + ") ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String status = (task.getStatus()) == Status.CLOSED ? "Выполнено" : (new Date().compareTo(expectedEndTask.getTime()) == 1) ? "Просрочена" : "Зарегистрирована";
        properties.put("Состояние ", status);
        return properties;
    }

    //метод абстрактной фабрики
    private ProducerFactory getProducerFactory(Object arg) {
        if (factoryForSubtasks == FactoryType.FOR_ALL_SUBTASKS) {
            return new ProducerFactory(arg) {
                @Override
                public TasksProducer getProducerTasks() {
                    //в переданных параметрах д.б. TaskInDB
                    TaskInDB taskInDB = (TaskInDB) arg;
                    return new AllTasksByParent(operatorDB, taskInDB.getId());
                }
            };
        } else {
            throw new IllegalArgumentException("Не определена фабрика для получения списка подзадач");
        }
    }

    /**
     * Получает список подзадач
     *
     * @param arg информация необходимая для инициализации фабрики TasksProducer, производящего список подзадач
     * @return
     */
    List<TaskInDB> getSubTasks(Object arg) {
        TasksProducer producerSubTasks = getProducerFactory(arg).getProducerTasks();
        try {
            return producerSubTasks.getTasks();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Типы фабрик
     */
    private enum FactoryType {
        FOR_ALL_SUBTASKS
    }
}


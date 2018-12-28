package kvv.education.khasang.java2.project_manager.version2.view;

import kvv.education.khasang.java2.project_manager.model.Status;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.PropertiesPanel;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Панель свойств для Task
 * <p>
 * Отображает свойства задачи (кроме куратора)
 * Создает задачу по введенным свойствам (в создаваемой задаче не указан куратор)
 */
public class TaskPropertiesPanel extends PropertiesPanel<Task> {

    //названия отображаемых на панеле полей
    public static final String ID_TASK = "ID";
    public static final String NAME_TASK = "Задача";
    public static final String BEGIN_TASK = "Начало";
    public static final String DURING_TASK = "Продолжительность(дни)";
    public static final String STATUS_TASK = "Статус";

    //приватный конструктор, чтобы заранее определить для инстанцирования нужные поля
    private TaskPropertiesPanel(Map<String, Class> fields) {
        super(fields);
    }

    /**
     * Инстанцирует панель
     * Метод уже содержит набор необходимых полей, которые должна отображать панель
     *
     * @return
     */
    public static TaskPropertiesPanel getInstance() {
        //Linked - чтобы порядок полей сохранился
        Map<String, Class> fields = new LinkedHashMap<>();
        fields.put(ID_TASK, Integer.class);
        fields.put(NAME_TASK, String.class);
        fields.put(BEGIN_TASK, Date.class);
        fields.put(DURING_TASK, Integer.class);
        fields.put(STATUS_TASK, Status.class);

        return new TaskPropertiesPanel(fields);
    }

    /**
     * Создание новой задачи по введеным на панеле данным
     * Задача не создается в базе, создается просто объект Task с указанными в панеле свойствами
     *
     * @return Task
     */
    @Override
    public Task getObjectInstance() {
        Task task;
        Integer idTask = (Integer) linkers.get(ID_TASK).giveObject();
        String nameTask = (String) linkers.get(NAME_TASK).giveObject();
        Date begin = (Date) linkers.get(BEGIN_TASK).giveObject();
        Integer during = (Integer) linkers.get(DURING_TASK).giveObject();
        Status status = (Status) linkers.get(STATUS_TASK).giveObject();
        task = new Task();
        task.setId(idTask);
        task.setName(nameTask);
        task.setStatus(status);
        task.setStarted(begin);
        task.setDuring(during);
        return task;
    }

    /**
     * Отображение задачи на панеле
     *
     * @param obj
     */
    @Override
    public void showOnPanel(Task obj) {
        //последовательно получаем линкеры для каждого поля панели, и передаем им интересуемое значение поля Task
        Linker linker;
        linker = linkers.get(ID_TASK);
        linker.takeObject(obj.getId());
        linker = linkers.get(NAME_TASK);
        linker.takeObject(obj.getName());
        linker = linkers.get(BEGIN_TASK);
        linker.takeObject(obj.getStarted());
        linker = linkers.get(DURING_TASK);
        linker.takeObject(obj.getDuring());
        linker = linkers.get(STATUS_TASK);
        linker.takeObject(obj.getStatus());
    }
}

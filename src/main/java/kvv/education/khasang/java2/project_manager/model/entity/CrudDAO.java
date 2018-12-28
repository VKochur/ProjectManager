package kvv.education.khasang.java2.project_manager.model.entity;

import kvv.education.khasang.java2.project_manager.model.Status;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Интерфейс взаимодействия с БД по созданию, поиску, обновлению и удалению информации отражающей сущности Curator, Task, Project
 */
public interface CrudDAO {

    //------------------------------------------для Curator-----------------------------------------------------------------
    /**
     * Создает в базе информацию по новому куратору с указанным именем и контактом
     * @param name
     * @param contact
     * @return объект Curator, соответветствующий хранящейся в бд информации по созданному куратору
     * @throws SQLException
     */
    Curator createCurator(String name, String contact) throws SQLException;

    /**
     * Удаляет в базе информацию по куратору с указанным id
     * @param id
     * @return
     * @throws SQLException
     */
    boolean deleteCuratorById(int id) throws SQLException;

    /**
     * Получает объект Curator соответствующий хранящейся в базе информацией по куратору с указанным id
     * @param id
     * @return null, в случае если нет информации для указанного id
     * @throws SQLException
     */
    Curator getCuratorById(int id) throws SQLException;

    /**
     * Обновляет данные в базе по куратору
     * @param curator объект, данные по которому должны быть обновлены в базе, т.е. данные в базе приводятся в соответствие с указанным объектом
     * @return true если информация в базе изменена, false в случае если нет информации для указанного curator (нет инфо в базе по curator.id)
     * @throws SQLException
     */
    boolean updateCuratorInDB(Curator curator) throws SQLException;

    /**
     * Получает список всех кураторов
     * @return
     * @throws SQLException
     */
    List<Curator> getAllCurators() throws SQLException;

    //-----------------------------------------для Project--------------------------------------------------------------

    /**
     * Создает в бд проект с указаным названием, датой начала, продолжительность и статусом
     * @param nameProject
     * @param started
     * @param during
     * @param status
     * @return
     * @throws SQLException
     */
    Project createProject(String nameProject, Date started, int during, Status status) throws SQLException;

    /**
     * Возвращает из базы проект по указанному id
     * @param id
     * @return null, если не найден проект с указанным id
     * @throws SQLException
     */
    Project getProjectById(int id) throws SQLException;

    /**
     * Получить список всех проектов
     * @return
     * @throws SQLException
     */
    List<Project> getAllProjects() throws SQLException;

   //--------------------------------------------для Task--------------------------------------------------------------------------

    /**
     * Получить список id подзадач
     * @param task
     * @return
     * @throws SQLException
     */
    List<Integer> getIdSubtasks(Task task) throws SQLException;

    /**
     * Создает в бд задачу, которая является подзадачей с указанной датой старта, продолжительностью, статусом
     * @param parentTask задача, для которой создаваемая задача является подзадачей
     * @param nameTask название создаваемой задачи
     * @param started дата старта
     * @param during продолжительность
     * @param status статус
     * @return
     * @throws SQLException
     */
    Task createTask(Task parentTask, String nameTask, Date started, int during, Status status) throws SQLException;

    /**
     * Получает из бд задачу по id
     * @param id
     * @return null, если задача не нашлась
     * @throws SQLException
     */
    Task getTaskById(int id) throws SQLException;

    /**
     * Удаляет из бд задачу по id со всеми ее подзадачами насквозь
     * @param idTask
     * @throws SQLException
     */
    void deleteTaskWithSubtasks(int idTask) throws SQLException;

    /**
     * Обновляет в бд информацию по задаче. т.е. информация в бд приводится в соответствие с task
     *
     * Обновление касается значения свойств полей и не затрагивает информацию о подзадачах.
     * Но в instanceof Task и не должна меняться инфо о подзадачах (метод protected addSubtask).
     * Подзадачи создаются методом данного интерфейса, и другой метод данного интерфейса выдает задачу по id с информацией о его подзадачах, и эта информация не должна меняться.
     * Если необходимо поменять сведения о подзадачах следует использовать createTask для добавления новых подзадач, либо deleteTaskWithSubtasks для удаления существующих
     * Актуализировать информацию по объекту следует task = getTaskById(task.getId()), это обновит и список подзадач
     *
     * @param task
     * @return false, если нет в бд информации по соответствующей задаче (соответствие по id)
     * @throws SQLException
     */
    boolean updateTaskInDB(Task task) throws SQLException;

    /**
     * Получить список всех задач
     * @return
     * @throws SQLException
     */
    List<Task> getAllTask() throws SQLException;

    /**
     * Получить список всех подзадач
     * @return
     * @throws SQLException
     */
    List<Task> getSubTasks(Task parentTask) throws SQLException;

    /**
     * Определить является ли задача проектов. т.е. в бд есть ли признак проекта у задачи
     * @param task
     * @return
     * @throws SQLException
     */
    Boolean isProject(Task task) throws SQLException;

    /**
     * Получает куратора указанной задачи из базы
     * @param idTask id задачи
     * @return возвращает null, в случае если куратор для задачи с указанным id не указан (если не найдена задача с id
     * или если задача найдена, но куратор не указан. Или если указан ид куратора, но по ид куратора куратор не находится
     * (чего произойти не должно))
     * @throws SQLException
     */
    Curator getCuratorByTask(Integer idTask) throws SQLException;

    /**
     * Устанавливает куратора для задачи
     * Для того чтобы убрать информацию о кураторе для задачи, следует передать idCurator = null
     *
     * Если idTask == null throws NullPointerException
     * throws IllegalArgumentException, в случае если в базе нет куратора с указанным id
     *
     * @param idTask id задачи
     * @param idCurator id куратора
     * @throws SQLException
     */
    void setCuratorForTask(Integer idTask, Integer idCurator) throws SQLException;
}

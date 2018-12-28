package kvv.education.khasang.java2.project_manager.model.entity;

import kvv.education.khasang.java2.project_manager.model.Status;

import java.util.*;

/**
 * Задача (POJO)
 */
public class Task {

    protected int id;
    protected String name;
    protected Date started;
    protected int during;
    protected Status status;
    protected Curator curator;

    protected List<Integer> idSubtasks;

    public Task() {
        idSubtasks = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public int getDuring() {
        return during;
    }

    public void setDuring(int during) {
        this.during = during;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Curator getCurator() {
        return curator;
    }

    public void setCurator(Curator curator) {
        this.curator = curator;
    }

    /**
     * Метод вызывается только из дао, т.е. только
     * @param idSubtask
     */
    protected void addSubtask(int idSubtask){
        idSubtasks.add(idSubtask);
    }

    /**
     * Список id подзадач
     * @return unmodifiableList
     */
    public List<Integer> getIdSubtasks() {
        return Collections.unmodifiableList(idSubtasks);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (getId() != task.getId()) return false;
        if (getDuring() != task.getDuring()) return false;
        if (getName() != null ? !getName().equals(task.getName()) : task.getName() != null) return false;
        if (getStarted() != null ? !getStarted().equals(task.getStarted()) : task.getStarted() != null) return false;
        if (getStatus() != task.getStatus()) return false;
        if (getCurator() != null ? !getCurator().equals(task.getCurator()) : task.getCurator() != null) return false;
        return getIdSubtasks() != null ? getIdSubtasks().equals(task.getIdSubtasks()) : task.getIdSubtasks() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getStarted() != null ? getStarted().hashCode() : 0);
        result = 31 * result + getDuring();
        result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
        result = 31 * result + (getCurator() != null ? getCurator().hashCode() : 0);
        result = 31 * result + (getIdSubtasks() != null ? getIdSubtasks().hashCode() : 0);
        return result;
    }

    /**
     * Проверка является ли задача просроченной
     * @return
     */
    public boolean overdure() {
        if (status == Status.CLOSED) {
            throw new IllegalStateException("Задача завершена. Определить просроченность нельзя");
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(started);
        calendar.add(Calendar.DAY_OF_MONTH, during);
        return (calendar.compareTo(new GregorianCalendar()) < 0);
    }

    @Override
    public String toString() {
        return name;
    }
}

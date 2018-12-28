package kvv.education.khasang.java2.project_manager.version1;

import kvv.education.khasang.java2.project_manager.model.Status;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Deprecated
public class TaskInDB {
    int id;
    boolean isProject;
    String name;
    Date started;
    int during;
    Status status;
    int idCurator;

    public TaskInDB(int id, boolean isProject, String name, Date started, int during, Status status, int idCurator) {
        this.id = id;
        this.isProject = isProject;
        this.name = name;
        this.started = started;
        this.during = during;
        this.status = status;
        this.idCurator = idCurator;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + started + " " + during;
    }

    public String getName() {
        return name;
    }

    public Date getStarted() {
        return started;
    }

    public int getDuring() {
        return during;
    }

    public int getIdCurator() {
        return idCurator;
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskInDB)) return false;

        TaskInDB task = (TaskInDB) o;

        return getId() == task.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}

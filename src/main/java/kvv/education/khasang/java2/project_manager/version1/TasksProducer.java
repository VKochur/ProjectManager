package kvv.education.khasang.java2.project_manager.version1;

import kvv.education.khasang.java2.project_manager.model.Status;
import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.query_results.Answerer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Поставщик задач
 */
@Deprecated
public interface TasksProducer {
    /**
     * Получить список задач
     *
     * @return
     */
    default List<TaskInDB> getTasks() throws SQLException {
        List<TaskInDB> tasks = new ArrayList<>();
        //список задач без детализации каждой задачи
        if (this instanceof Answerer) {
            List<List<String>> rows = ((Answerer) (this)).getAnswer();
            for (List<String> row : rows) {
                int id = Integer.parseInt(row.get(0));
                boolean isProject = Boolean.getBoolean(row.get(1));
                String name = row.get(2);
                Date started = Util.getDateByString(row.get(3)).getTime();
                int during = Integer.parseInt(row.get(4));
                Status status = Status.valueOf(row.get(5));
                int idCurator = Integer.parseInt(row.get(6));
                TaskInDB task = new TaskInDB(id, isProject, name, started, during, status, idCurator);
                tasks.add(task);
            }
        }
        return tasks;
    }
}

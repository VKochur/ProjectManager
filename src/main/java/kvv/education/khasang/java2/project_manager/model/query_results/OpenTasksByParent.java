package kvv.education.khasang.java2.project_manager.model.query_results;

import kvv.education.khasang.java2.project_manager.model.Status;
import kvv.education.khasang.java2.project_manager.version1.TaskInDB;
import kvv.education.khasang.java2.project_manager.version1.TasksProducer;
import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.queryes.Askor;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.queryes.OpenTasksByParentAscor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Ответчик на получения открытых подзадач по idTaskParent
 */
public class OpenTasksByParent extends Answerer implements TasksProducer {

    private static final String NAME = "Открытые подзадачи";
    private static final String[] NAME_COLUMNS = {
            Answerer.TITLE_ID_TASK,
            Answerer.TITLE_IS_PROJECT,
            Answerer.TITLE_NAME,
            Answerer.TITLE_STARTED,
            Answerer.TITLE_DURING,
            Answerer.TITLE_STATUS_TASK,
            Answerer.TITLE_ID_CURATOR};
    private Askor askor;

    public OpenTasksByParent(OperatorDB operatorDB, int idParent) {
        super(operatorDB);
        this.name = OpenTasksByParent.NAME;
        askor = new OpenTasksByParentAscor(idParent);
    }

    @Override
    public List<String> getNameColumns() {
        return Arrays.asList(NAME_COLUMNS);
    }

    @Override
    public List<List<String>> getAnswer() throws SQLException {
        return operatorDB.executeQuery(askor);
    }

    @Override
    public List<TaskInDB> getTasks() throws SQLException {
        List<TaskInDB> tasks = new ArrayList<>();
        List<List<String>> rows = getAnswer();
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
        return tasks;
    }
}

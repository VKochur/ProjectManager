package kvv.education.khasang.java2.project_manager.model.selections;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.query_results.OpenTasksCount;

import java.sql.SQLException;
import java.util.*;

public class SelectionOpenSubtasksCountByTask extends Selection {

    private static final String NAME = "Выборка количества открытых подзадач по задаче";
    public static final String KEY1 = "id задачи";
    private Map<String, String> args;

    public SelectionOpenSubtasksCountByTask(OperatorDB operatorDB) {
        super(operatorDB);
        //у выборки один уточняющий параметр
        args = new HashMap<>();
        args.put(SelectionOpenSubtasksCountByTask.KEY1, null);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, String> getArgs() {
        return args;
    }

    @Override
    public List<String> getTitles() {
        return Arrays.asList("Количество");
    }

    @Override
    public List<List<String>> getResults() throws NumberFormatException {
        String value = args.get(SelectionOpenSubtasksCountByTask.KEY1);
        try {
            Integer idTask = (value == null)? null : Integer.parseInt(value);
            return new OpenTasksCount(operatorDB, idTask).getAnswer();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}

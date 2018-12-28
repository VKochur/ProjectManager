package kvv.education.khasang.java2.project_manager.model.selections;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.query_results.OpenTasksByCurator;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectionOpenTasksByCurator extends Selection {

    public static final String NAME = "Выборка незавершенных задач по ответственному";
    public static final String KEY1 = "Имя ответственного";
    private Map<String, String> args;

    public SelectionOpenTasksByCurator(OperatorDB operatorDB) {
        super(operatorDB);
        //уточняющий параметр выборки
        args = new HashMap<>();
        args.put(KEY1, null);
    }

    @Override
    public String getName() {
        return SelectionOpenTasksByCurator.NAME;
    }

    @Override
    public List<List<String>> getResults() throws SQLException {
        String curator = args.get(KEY1);
        return new OpenTasksByCurator(operatorDB, curator).getAnswer();
    }

    @Override
    public List<String> getTitles() {
        String value = args.get(KEY1);
        return new OpenTasksByCurator(operatorDB, value).getNameColumns();
    }

    @Override
    public Map<String, String> getArgs() {
        return args;
    }

    @Override
    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}

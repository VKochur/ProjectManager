package kvv.education.khasang.java2.project_manager.model.selections;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.query_results.OpenProjects;

import java.sql.SQLException;
import java.util.*;

/**
 * Выборка по открытым проектам
 */
public class SelectionOpenProject extends Selection {
    private static final String NAME = "Выборка незавершенных проектов";

    public SelectionOpenProject(OperatorDB operatorDB) {
        super(operatorDB);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, String> getArgs() {
        //у выборки нет уточняющих параметров
        return Collections.EMPTY_MAP;
    }

    @Override
    public void setArgs(Map<String, String> args) {

    }

    @Override
    public List<String> getTitles() {
        return new OpenProjects(operatorDB).getNameColumns();
    }

    @Override
    public List<List<String>> getResults() throws SQLException {
        return new OpenProjects(operatorDB).getAnswer();
    }
}

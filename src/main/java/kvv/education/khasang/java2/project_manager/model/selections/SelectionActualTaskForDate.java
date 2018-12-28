package kvv.education.khasang.java2.project_manager.model.selections;


import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.query_results.ActualTasksForDate;

/**
 * Выборка по актуальным задачам на определенную дату
 */
public class SelectionActualTaskForDate extends Selection {
    private static final String NAME = "Выборка актуальных на данный момент задач";
    private Date forDate;

    public SelectionActualTaskForDate(OperatorDB operatorDB, Date forDate) {
        super(operatorDB);
        this.forDate = forDate;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, String> getArgs() {
        //у данной выборки нет уточняющих критериев
        return Collections.EMPTY_MAP;
    }

    @Override
    public void setArgs(Map<String, String> args) {

    }

    @Override
    public List<String> getTitles() {
        return new ActualTasksForDate(operatorDB, forDate).getNameColumns();
    }

    @Override
    public List<List<String>> getResults() throws SQLException {
        return new ActualTasksForDate(operatorDB, forDate).getAnswer();
    }
}

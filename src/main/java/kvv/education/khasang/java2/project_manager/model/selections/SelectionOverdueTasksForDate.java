package kvv.education.khasang.java2.project_manager.model.selections;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.query_results.OverdueTasksForDate;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Выборка по просроченным задачам
 */
public class SelectionOverdueTasksForDate extends Selection {
    private static final String NAME = "Выборка просроченных заданий";
    private final Date forDate;

    /**
     * Выборка по просроченным задачам к определенной базе, расчитываемых к определенной дате
     * @param operatorDB
     * @param forDate
     */
    public SelectionOverdueTasksForDate(OperatorDB operatorDB, Date forDate) {
        super(operatorDB);
        this.forDate = forDate;
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
        return new OverdueTasksForDate(operatorDB, new Date()).getNameColumns();
    }

    @Override
    public List<List<String>> getResults() throws SQLException {
        return new OverdueTasksForDate(operatorDB, forDate).getAnswer();
    }
}

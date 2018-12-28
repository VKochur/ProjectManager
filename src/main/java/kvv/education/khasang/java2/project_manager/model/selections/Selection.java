package kvv.education.khasang.java2.project_manager.model.selections;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Выборка данных соответствующих определенным критериям
 */
public abstract class Selection {

    protected OperatorDB operatorDB;

    public Selection(OperatorDB operatorDB) {
        this.operatorDB = operatorDB;
    }

    public OperatorDB getOperatorDB() {
        return operatorDB;
    }

    //название выборки
    public abstract String getName();

    //название критерия - значение критерия выборки
    public abstract Map<String, String> getArgs();

    public abstract void setArgs(Map<String, String> args);

    //заголовок столбцов результатов выборки
    public abstract List<String> getTitles();

    //содержание результатов выборки
    public abstract List<List<String>> getResults() throws SQLException;

    @Override
    public String toString() {
        return getName();
    }
}

package kvv.education.khasang.java2.project_manager.model.query_results;

import kvv.education.khasang.java2.project_manager.model.queryes.Askor;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.queryes.CountOpenTasksByParentAskor;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Ответчик на запрос количества открытых подзадач по idTaskParent
 */
public class OpenTasksCount extends Answerer {

    private static final String NAME = "Количество открытых подзадач";
    private Askor askor;

    public OpenTasksCount(OperatorDB operatorDB, int idParent) {
        super(operatorDB);
        this.name = OpenTasksCount.NAME;
        askor = new CountOpenTasksByParentAskor(idParent);
    }

    @Override
    public List<String> getNameColumns() {
        return Arrays.asList(Answerer.TITLE_COUNT);
    }

    @Override
    public List<List<String>> getAnswer() throws SQLException {
        return operatorDB.executeQuery(askor);
    }
}

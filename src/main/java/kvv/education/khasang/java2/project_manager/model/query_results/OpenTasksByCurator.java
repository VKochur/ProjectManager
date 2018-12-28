package kvv.education.khasang.java2.project_manager.model.query_results;

import kvv.education.khasang.java2.project_manager.version1.TasksProducer;
import kvv.education.khasang.java2.project_manager.model.queryes.Askor;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.queryes.OpenTasksByCuratorAskor;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Ответчик на получение списка открытых задач за определенным куратором
 */
public class OpenTasksByCurator extends Answerer implements TasksProducer {

    private static final String NAME = "Открытые задачи за ответственным";
    private static final String[] NAME_COLUMNS = {
            Answerer.TITLE_ID_TASK,
            Answerer.TITLE_IS_PROJECT,
            Answerer.TITLE_NAME,
            Answerer.TITLE_STARTED,
            Answerer.TITLE_DURING,
            Answerer.TITLE_STATUS_TASK,
            Answerer.TITLE_ID_CURATOR};
    private Askor askor;

    public OpenTasksByCurator(OperatorDB operatorDB, String curator) {
        super(operatorDB);
        this.name = OpenTasksByCurator.NAME;
        askor = new OpenTasksByCuratorAskor(curator);
    }

    @Override
    public List<String> getNameColumns() {
        return Arrays.asList(NAME_COLUMNS);
    }

    @Override
    public List<List<String>> getAnswer() throws SQLException {
        return operatorDB.executeQuery(askor);
    }
}

package kvv.education.khasang.java2.project_manager.model.query_results;

import kvv.education.khasang.java2.project_manager.model.queryes.Askor;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Ответчик на получение ответственного по id
 */
public class CuratorById extends Answerer {
    private static final String NAME = "Ответственный по id";
    private static final String[] NAME_COLUMNS = {
            Answerer.TITLE_ID_CURATOR,
            Answerer.TITLE_CURATOR,
            Answerer.TITLE_CONTACT_CURATOR};

    private final int idCurator;

    public CuratorById(OperatorDB operatorDB, int idCurator) {
        super(operatorDB);
        this.name = CuratorById.NAME;
        this.idCurator = idCurator;
    }

    @Override
    public List<String> getNameColumns() {
        return Arrays.asList(NAME_COLUMNS);
    }

    @Override
    public List<List<String>> getAnswer() throws SQLException {
        Askor askor = new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select " +
                        "curators.id, " +
                        "curators.name, " +
                        "curators.contact " +
                        "from " +
                        "curators " +
                        "where curators.id=?;";
            }

            @Override
            public String[] getArgs() {
                return new String[]{String.valueOf(idCurator)};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{ARG_TYPE_INT};
            }
        };
        return operatorDB.executeQuery(askor);
    }

    public int getCuratorId() {
        return this.idCurator;
    }

    public String getCuratorName() throws SQLException {
        if (getCountRows() > 1) {
            throw new IllegalStateException("В табл. curators по id нашлось несколько записей. id = " + this.idCurator);
        }
        if (getCountRows() == 0) {
            return null;
        } else {
            return getAnswer().get(0).get(1);
        }
    }

    public String getCuratorContact() throws SQLException {
        if (getCountRows() > 1) {
            throw new IllegalStateException("В табл. curators по id нашлось несколько записей. id = " + this.idCurator);
        }
        if (getCountRows() == 0) {
            return null;
        } else {
            return getAnswer().get(0).get(2);
        }
    }
}

package kvv.education.khasang.java2.project_manager.model.queryes;

/**
 * Запрос на получение количества открытых подзадач по id задаче родителя
 */
public class CountOpenTasksByParentAskor extends OpenTasksByParentAscor {
    public CountOpenTasksByParentAskor(int idParent) {
        super(idParent);
    }

    @Override
    public String sqlForPreparedStatement() {
        return "select " +
                "count(*) " +
                "from tasks " +
                "where (tasks.id_parent = ?) and (tasks.status = 'OPEN')";
    }
}

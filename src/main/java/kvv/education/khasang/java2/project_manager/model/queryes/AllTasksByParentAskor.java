package kvv.education.khasang.java2.project_manager.model.queryes;

/**
 * Запрос на получение подзадач по id задачи родителя (idTaskParent)
 */
public class AllTasksByParentAskor implements Askor {

    private int idParent;

    public AllTasksByParentAskor(int idParent) {
        this.idParent = idParent;
    }

    @Override
    public String sqlForPreparedStatement() {
        return "select " +
                "tasks.id, " +
                "tasks.project_flag, " +
                "tasks.name, " +
                "tasks.started, " +
                "tasks.during, " +
                "tasks.status," +
                "tasks.id_curator " +
                "from tasks " +
                "where (tasks.id_parent = ?)";
    }

    @Override
    public String[] getArgs() {
        return new String[]{String.valueOf(idParent)};
    }

    @Override
    public String[] getTypeArgs() {
        return new String[]{Askor.ARG_TYPE_INT};
    }
}

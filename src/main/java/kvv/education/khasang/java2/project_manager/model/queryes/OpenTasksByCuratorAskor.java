package kvv.education.khasang.java2.project_manager.model.queryes;

/**
 * Запрос на получение открытых задач за определенным куратором
 */
public class OpenTasksByCuratorAskor implements Askor {

    private String curatorName;

    public OpenTasksByCuratorAskor(String curatorName) {
        this.curatorName = curatorName;
    }

    @Override
    public String sqlForPreparedStatement() {
        return "select " +
                "tasks.id, " +
                "tasks.project_flag, " +
                "tasks.name, " +
                "tasks.started, " +
                "tasks.during," +
                "tasks.status," +
                "tasks.id_curator " +
                "from tasks " +
                "join curators on curators.name = ? " +
                "where (id_curator = curators.id) and (tasks.status = 'OPEN');";
    }

    @Override
    public String[] getArgs() {
        return new String[]{curatorName};
    }

    @Override
    public String[] getTypeArgs() {
        return new String[]{Askor.ARG_TYPE_STRING};
    }
}

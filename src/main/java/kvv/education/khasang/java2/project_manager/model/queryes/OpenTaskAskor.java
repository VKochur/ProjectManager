package kvv.education.khasang.java2.project_manager.model.queryes;

/**
 * Запрос на получение открытых задач (Singlton)
 */
public class OpenTaskAskor implements Askor {
    private static final Object KEY = new Object();
    private static OpenTaskAskor openTaskAskor;

    public static OpenTaskAskor getInstance() {
        if (openTaskAskor == null) {
            synchronized (KEY) {
                if (openTaskAskor == null) {
                    openTaskAskor = new OpenTaskAskor();
                } else {
                    return openTaskAskor;
                }
            }
        }
        return openTaskAskor;
    }

    private OpenTaskAskor() {
    }

    @Override
    public String sqlForPreparedStatement() {
        return "select " +
                "tasks.id, " +
                "tasks.project_flag, " +
                "tasks.name, " +
                "tasks.started, " +
                "tasks.during, " +
                "tasks.status, " +
                "tasks.id_curator, " +
                "curators.name, " +
                "curators.contact " +
                "from tasks " +
                "left join curators on curators.id = tasks.id_curator " +
                "where tasks.status = 'OPEN';";
    }

    @Override
    public String[] getArgs() {
        return new String[0];
    }

    @Override
    public String[] getTypeArgs() {
        return new String[0];
    }
}

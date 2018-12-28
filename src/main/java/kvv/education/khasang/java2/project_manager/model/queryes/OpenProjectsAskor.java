package kvv.education.khasang.java2.project_manager.model.queryes;

/**
 * Запрос на получение открытых проектов (Singlton)
 */
public class OpenProjectsAskor implements Askor {

    private static final Object KEY = new Object();
    private static OpenProjectsAskor openProjectsAskor;

    public static OpenProjectsAskor getInstance() {
        if (openProjectsAskor == null) {
            synchronized (KEY) {
                if (openProjectsAskor == null) {
                    openProjectsAskor = new OpenProjectsAskor();
                } else {
                    return openProjectsAskor;
                }
            }
        }
        return openProjectsAskor;
    }

    private OpenProjectsAskor() {
    }

    @Override
    public String sqlForPreparedStatement() {
        return "select " +
                "id, " +
                "project_flag, " +
                "name, " +
                "started, " +
                "during, " +
                "status, " +
                "id_curator " +
                "from tasks " +
                "where (tasks.status = 'OPEN') and (tasks.project_flag = 'true')";
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

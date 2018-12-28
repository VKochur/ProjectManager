package kvv.education.khasang.java2.project_manager.model.queryes;

/**
 * Запрос на получение всех проектов (Singlton)
 */
public class AllProjectsAskor implements Askor {
    private static final Object KEY = new Object();
    private static AllProjectsAskor allProjectsAskor;

    public static AllProjectsAskor getInstance() {
        if (allProjectsAskor == null) {
            synchronized (KEY) {
                if (allProjectsAskor == null) {
                    allProjectsAskor = new AllProjectsAskor();
                } else {
                    return allProjectsAskor;
                }
            }
        }
        return allProjectsAskor;
    }

    private AllProjectsAskor() {

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
                "where tasks.project_flag = 'true'";
    }


    @Override
    public String[] getArgs() {
        //в запросе отсутствуют уточняющие критерии
        return new String[0];
    }

    @Override
    public String[] getTypeArgs() {
        return new String[0];
    }

}

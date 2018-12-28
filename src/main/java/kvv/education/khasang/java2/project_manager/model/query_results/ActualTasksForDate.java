package kvv.education.khasang.java2.project_manager.model.query_results;

import kvv.education.khasang.java2.project_manager.version1.TasksProducer;
import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.queryes.Askor;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.queryes.OpenTaskAskor;

import java.sql.SQLException;
import java.util.*;

/**
 * Ответчик на запрос актуальных задач на определенную дату
 */
public class ActualTasksForDate extends Answerer implements TasksProducer {
    private static final String NAME = "Список актуальных задач";
    private static final String[] NAME_COLUMNS = {
            Answerer.TITLE_ID_TASK,
            Answerer.TITLE_IS_PROJECT,
            Answerer.TITLE_NAME,
            Answerer.TITLE_STARTED,
            Answerer.TITLE_DURING,
            Answerer.TITLE_STATUS_TASK,
            Answerer.TITLE_ID_CURATOR,
            Answerer.TITLE_CURATOR,
            Answerer.TITLE_CONTACT_CURATOR};
    private static Askor askor = OpenTaskAskor.getInstance();
    private final Date forDate;

    /**
     * Ответчик к определенной базе с указанной интересуемой датой
     * @param operatorDB
     * @param forDate
     */
    public ActualTasksForDate(OperatorDB operatorDB, Date forDate) {
        super(operatorDB);
        this.name = ActualTasksForDate.NAME;
        this.forDate = forDate;
    }

    @Override
    public List<String> getNameColumns() {
        return Arrays.asList(NAME_COLUMNS);
    }

    @Override
    public List<List<String>> getAnswer() throws SQLException {
        Calendar dateForInterest = new GregorianCalendar();
        dateForInterest.setTime(forDate);
        //выберем открытые задачи
        List<List<String>> openTasks = operatorDB.executeQuery(askor);
        //отберем среди откртытых задач актуальные
        Iterator<List<String>> iterator = openTasks.iterator();
        while (iterator.hasNext()) {
            List<String> openTask = iterator.next();
            // 3 - started 4 - during
            Calendar startedTask = Util.getDateByString(openTask.get(3));
            if (startedTask.compareTo(dateForInterest) > 0) {
                //если время старта задачи еще не настало
                iterator.remove();
            }
        }
        return openTasks;
    }
}

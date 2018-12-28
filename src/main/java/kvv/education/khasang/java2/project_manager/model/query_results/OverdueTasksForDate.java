package kvv.education.khasang.java2.project_manager.model.query_results;

import kvv.education.khasang.java2.project_manager.version1.TasksProducer;
import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.queryes.Askor;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.queryes.OpenTaskAskor;

import java.sql.SQLException;
import java.util.*;

/**
 * Ответчик на получение просроченных задач, расчитываемых по определенной дате
 */
public class OverdueTasksForDate extends Answerer implements TasksProducer {
    private static final String NAME = "Просроченные задачи";
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
     * Ответчик по просроченным задачам к определенной бд, расчитываемых к определенной дате
     * @param operatorDB
     * @param forDate
     */
    public OverdueTasksForDate(OperatorDB operatorDB, Date forDate) {
        super(operatorDB);
        this.name = OverdueTasksForDate.NAME;
        this.forDate =  forDate;
    }

    @Override
    public List<String> getNameColumns() {
        return Arrays.asList(NAME_COLUMNS);
    }

    @Override
    public List<List<String>> getAnswer() throws SQLException {
        Calendar dateForInterest = new GregorianCalendar();
        dateForInterest.setTime(forDate);
        //отбираем все открытые задачи
        List<List<String>> openTasks = operatorDB.executeQuery(askor);
        //оставляем только проcроченные среди открытых
        Iterator<List<String>> iterator = openTasks.iterator();
        while (iterator.hasNext()) {
            List<String> openTask = iterator.next();
            // 3 - started 4 - during
            Calendar expectedEndTask = Util.getDateByString(openTask.get(3));
            int countDays = Integer.parseInt(openTask.get(4));
            expectedEndTask.add(Calendar.DAY_OF_MONTH, countDays);
            if (expectedEndTask.compareTo(dateForInterest) > 0) {
                //если задача не просрочена
                iterator.remove();
            }
        }
        return openTasks;
    }
}

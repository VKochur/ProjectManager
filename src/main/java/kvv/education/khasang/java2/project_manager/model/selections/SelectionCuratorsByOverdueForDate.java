package kvv.education.khasang.java2.project_manager.model.selections;


import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;

import java.sql.SQLException;
import java.util.*;

/**
 * Выборка ответственных, за которыми есть просроченные задания расчитываемые к определенной дате
 */
public class SelectionCuratorsByOverdueForDate extends Selection {

    private static final String NAME = "Выборка кураторов с просроченными заданиями";
    private static String[] TITLES = {"id", "Ответственный", "Контакт"};
    private final Date forDate;

    public SelectionCuratorsByOverdueForDate(OperatorDB operatorDB, Date forDate) {
        super(operatorDB);
        this.forDate = forDate;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Map<String, String> getArgs() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public void setArgs(Map<String, String> args) {

    }

    @Override
    public List<String> getTitles() {
        return Arrays.asList(SelectionCuratorsByOverdueForDate.TITLES);
    }

    @Override
    public List<List<String>> getResults() throws SQLException {
        List<List<String>> overdueTasks = new SelectionOverdueTasksForDate(operatorDB, forDate).getResults();
        //выберем кураторов просроченных задач через вспомогательный Map
        Map<String, String[]> temp = new HashMap<>();
        for (List<String> strings : overdueTasks) {
            String idCurator = strings.get(6);
            String nameCurator = strings.get(7);
            String contactCurator = strings.get(8);
            String[] value = new String[]{nameCurator, contactCurator};
            temp.put(idCurator, value);
        }

        List<List<String>> out = new ArrayList<>();
        Set<String> key = temp.keySet();
        for (String s : key) {
            String[] value = temp.get(s);
            out.add(Arrays.asList(s, value[0], value[1]));
        }

        return out;
    }
}

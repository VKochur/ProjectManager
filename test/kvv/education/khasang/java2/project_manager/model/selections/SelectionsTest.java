package kvv.education.khasang.java2.project_manager.model.selections;

import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Задача "Менеджер проектов"
 *
 * Тестирование получения результатов выборок
 */
public class SelectionsTest {
    private static OperatorDB operatorDB;

    @BeforeClass
    public static void init() {
        operatorDB = new OperatorDB("test/test.db");
    }

    /**
     * Тестирование получения результатов выборки по актуальным задачам к определенному времени
     *
     * @throws Exception
     */
    @Test
    public void testSelectionActualTaskForDateGetResults() throws Exception {
        List<List<String>> actual = new SelectionActualTaskForDate(operatorDB, Util.getDateByString("20.06.2018").getTime()).getResults();
        String expected = "[[1, true, Проект1, 30.09.2017, 365, OPEN, 1, Куратор1, контакт1]," +
                " [9, false, Проект1 Задача23, 01.04.2018, 30, OPEN, 2, Куратор2, контакт2]," +
                " [10, false, Проект1 Задача221, 01.05.2018, 10, OPEN, null, null, null]," +
                " [11, false, Проект1 Задача24, 01.07.2017, 50, OPEN, 1, Куратор1, контакт1]]";
        assertEquals("результат выборки актуальных задач на 20.06.2018", expected, actual.toString());

        actual = new SelectionActualTaskForDate(operatorDB, Util.getDateByString("01.01.2020").getTime()).getResults();
        expected = "[[1, true, Проект1, 30.09.2017, 365, OPEN, 1, Куратор1, контакт1], " +
                "[2, true, Проект2, 02.09.2019, 200, OPEN, 1, Куратор1, контакт1], " +
                "[6, false, Проект1 Задача2, 20.08.2018, 30, OPEN, 1, Куратор1, контакт1], " +
                "[9, false, Проект1 Задача23, 01.04.2018, 30, OPEN, 2, Куратор2, контакт2], " +
                "[10, false, Проект1 Задача221, 01.05.2018, 10, OPEN, null, null, null], " +
                "[11, false, Проект1 Задача24, 01.07.2017, 50, OPEN, 1, Куратор1, контакт1], " +
                "[12, false, Проект2 Задача1, 01.01.2019, 1, OPEN, 1, Куратор1, контакт1], " +
                "[13, false, Проект2 Задача2, 01.08.2019, 20, OPEN, 1, Куратор1, контакт1]]";
        assertEquals("результат выборки актуальных задач на 01.01.2020", expected, actual.toString());
    }

    /**
     * Тестирование получения результатов по открытым проектам
     *
     * @throws Exception
     */
    @Test
    public void testSelectionOpenProjectGetResults() throws Exception {
        List<List<String>> actual = new SelectionOpenProject(operatorDB).getResults();
        String expected = "[[1, true, Проект1, 30.09.2017, 365, OPEN, 1]," +
                " [2, true, Проект2, 02.09.2019, 200, OPEN, 1]]";
        assertEquals("результат выборки открытых проектов задач", expected, actual.toString());
    }

    /**
     * Тестирование получения результатов выборки по количеству открытых подзадач в условии что не задан критерий выборки "id задача-родитель"
     * @throws Exception
     */
    @Test(expected = NullPointerException.class)
    public void testSelectionOpenSubtasksCountByTask() throws Exception {
        new SelectionOpenSubtasksCountByTask(operatorDB).getResults();
    }

    /**
     * Тестирование получения результатов выборки по количеству открытых подзадач в условии что критерий выборки "id задача-родитель" некорректен
     * @throws Exception
     */
    @Test(expected = NumberFormatException.class)
    public void test2SelectionOpenSubtasksCountByTask() throws Exception {
        SelectionOpenSubtasksCountByTask selection = new SelectionOpenSubtasksCountByTask(operatorDB);
        Map<String, String> args = new HashMap<>();
        args.put(SelectionOpenSubtasksCountByTask.KEY1, "sf");
        selection.setArgs(args);
        selection.getResults();
    }

    /**
     * Тестирование получения результатов выборки количества открытых подзадач
     * @throws Exception
     */
    @Test
    public void test3SelectionOpenSubtasksCountByTask() throws Exception {
        SelectionOpenSubtasksCountByTask selection = new SelectionOpenSubtasksCountByTask(operatorDB);
        Map<String, String> args = new HashMap<>();
        args.put(SelectionOpenSubtasksCountByTask.KEY1, "1");
        selection.setArgs(args);
        String actual = selection.getResults().toString();
        assertEquals("колво открытых задач по существующему проекту", "[[1]]", actual);
        args.put(SelectionOpenSubtasksCountByTask.KEY1, "2");
        actual = selection.getResults().toString();
        assertEquals("колво открытых задач по существующему проекту", "[[2]]", actual);
        args.put(SelectionOpenSubtasksCountByTask.KEY1, "3");
        actual = selection.getResults().toString();
        assertEquals("колво открытых задач по существующему проекту", "[[0]]", actual);
        args.put(SelectionOpenSubtasksCountByTask.KEY1, "233");
        actual = selection.getResults().toString();
        assertEquals("колво открытых задач по несуществующему проекту", "[[0]]", actual);
        args.put(SelectionOpenSubtasksCountByTask.KEY1, "6");
        actual = selection.getResults().toString();
        assertEquals("колво открытых подзадач по существующей задаче", "[[2]]", actual);
    }

    /**
     * Тестирование получения кураторов с просроченными задачами к определенной дате
     */
    @Test
    public void testSelectionCuratorsByOverdue() throws SQLException {
        SelectionCuratorsByOverdueForDate selectionCuratorsByOverdue = new SelectionCuratorsByOverdueForDate(operatorDB, Util.getDateByString("20.06.2018").getTime());

        String actual = selectionCuratorsByOverdue.getResults().toString();

        assertEquals("ответственные с просроченными заданиями к 20.06.2018", "[[null, null, null], [1, Куратор1, контакт1], [2, Куратор2, контакт2]]", actual);

        selectionCuratorsByOverdue = new SelectionCuratorsByOverdueForDate(operatorDB, Util.getDateByString("01.09.2017").getTime());
        actual = selectionCuratorsByOverdue.getResults().toString();
        assertEquals("ответственные с просроченными заданиями к 01.09.2017", "[[1, Куратор1, контакт1]]", actual);
    }

    /**
     * Тестирование получения результатов выборки открытых задач за ответственным
     */
    @Test
    public void testSelectionOpenTasksByCurator() throws SQLException {
        SelectionOpenTasksByCurator selectionOpenTasksByCurator = new SelectionOpenTasksByCurator(operatorDB);
        String actual = selectionOpenTasksByCurator.getResults().toString();
        assertEquals("выборка по неуказанному ответственному", "[]", actual);
        Map<String, String> args = new HashMap<>();
        args.put(SelectionOpenTasksByCurator.KEY1, "такого нет");
        selectionOpenTasksByCurator.setArgs(args);
        actual = selectionOpenTasksByCurator.getResults().toString();
        assertEquals("выборка по несуществующему ответственному", "[]", actual);
        args.put(SelectionOpenTasksByCurator.KEY1, "Куратор1");
        actual = selectionOpenTasksByCurator.getResults().toString();
        String expected = "[[1, true, Проект1, 30.09.2017, 365, OPEN, 1], " +
                "[2, true, Проект2, 02.09.2019, 200, OPEN, 1], " +
                "[6, false, Проект1 Задача2, 20.08.2018, 30, OPEN, 1], " +
                "[11, false, Проект1 Задача24, 01.07.2017, 50, OPEN, 1], " +
                "[12, false, Проект2 Задача1, 01.01.2019, 1, OPEN, 1], " +
                "[13, false, Проект2 Задача2, 01.08.2019, 20, OPEN, 1]]";
        assertEquals("выборка по существующему ответственному", expected, actual);
    }

    /**
     * Тестирование получения результатов выборки по просроченным задачам
     */
    @Test
    public void testSelectionOverdueTasks() throws SQLException {
        SelectionOverdueTasksForDate selectionOverdueTasks = new SelectionOverdueTasksForDate(operatorDB, Util.getDateByString("20.06.2018").getTime());
        String actual = selectionOverdueTasks.getResults().toString();
        String expected = "[[9, false, Проект1 Задача23, 01.04.2018, 30, OPEN, 2, Куратор2, контакт2]," +
                " [10, false, Проект1 Задача221, 01.05.2018, 10, OPEN, null, null, null]," +
                " [11, false, Проект1 Задача24, 01.07.2017, 50, OPEN, 1, Куратор1, контакт1]]";
        assertEquals("просроченные задачи к 20.06.2018", actual, expected);

        selectionOverdueTasks = new SelectionOverdueTasksForDate(operatorDB, Util.getDateByString("11.09.2017").getTime());
        actual = selectionOverdueTasks.getResults().toString();
        expected = "[[11, false, Проект1 Задача24, 01.07.2017, 50, OPEN, 1, Куратор1, контакт1]]";
        assertEquals("просроченные задачи к 11.09.2017", actual, expected);
    }
}
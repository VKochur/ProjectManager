package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDAO;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDaoJDBC;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionOverdueTasksForDate;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Задача "Менеджер проектов"
 *
 * Тестирование получения обертки выборки просроченных задач
 */
public class OverdueTasksTest {
    private static OperatorDB operatorDB;
    private static CrudDAO crudDAO;

    @BeforeClass
    public static void init() {
        operatorDB = new OperatorDB("test/test.db");
        crudDAO =  new CrudDaoJDBC("test/test.db");
    }

    /**
     * Тестирование получения результатов выборки
     * @throws Exception
     */
    @Test
    public void getResults() throws Exception {
        OverdueTasks overdueTasks = new OverdueTasks(new SelectionOverdueTasksForDate(operatorDB, Util.getDateByString("20.06.2018").getTime()));
        List<Task> actual = overdueTasks.getResults();
        assertEquals(3, actual.size());
        assertTrue(actual.containsAll(Arrays.asList(new Task[] {crudDAO.getTaskById(9), crudDAO.getTaskById(9), crudDAO.getTaskById(11)})));
    }

}
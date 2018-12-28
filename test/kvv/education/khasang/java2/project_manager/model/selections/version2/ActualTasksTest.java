package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDaoJDBC;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionActualTaskForDate;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Задача "Менеджер проектов"
 * <p>
 * Тестирование получения результата обертки выборки актуальных задач
 */
public class ActualTasksTest {

    private static OperatorDB operatorDB;
    private static CrudDaoJDBC crudDAO;

    @BeforeClass
    public static void init() {
        operatorDB = new OperatorDB("test/test.db");
        crudDAO = new CrudDaoJDBC("test/test.db");
    }

    /**
     * Тестирование получения результатов выборки
     * @throws SQLException
     */
    @Test
    public void testGetResults() throws SQLException {
        ActualTasks actualTasks = new ActualTasks(new SelectionActualTaskForDate(operatorDB, Util.getDateByString("20.06.2018").getTime()));
        List<Task> actual = actualTasks.getResults();
        assertEquals(4, actual.size());
        assertTrue(actual.contains(crudDAO.getTaskById(1)));
        assertTrue(actual.contains(crudDAO.getTaskById(9)));
        assertTrue(actual.contains(crudDAO.getTaskById(1)));
        assertTrue(actual.contains(crudDAO.getTaskById(11)));
    }



}
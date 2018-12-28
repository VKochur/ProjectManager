package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDaoJDBC;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionOpenSubtasksCountByTask;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Задача "Менеджер проектов"
 *
 * Тестирование методов обертки выборки количества откртытых подзадач
 */
public class OpenSubtasksCountTest {
    private static OperatorDB operatorDB;

    @BeforeClass
    public static void init() {
        operatorDB = new OperatorDB("test/test.db");
    }

    /**
     * Тестирование получения результатов
     * @throws Exception
     */
    @Test
    public void test1GetResults() throws Exception {
        //подготовка
        OpenSubtasksCount openSubtasksCount = new OpenSubtasksCount(new SelectionOpenSubtasksCountByTask(operatorDB));
        CrudDaoJDBC crudDaoJDBC = new CrudDaoJDBC(operatorDB.getPathDB());
        List<Integer> actual;
        //тестирование получения значений

        //получение значений по существующим задачам с подзадачами
        Task task = crudDaoJDBC.getTaskById(1);
        openSubtasksCount.setValueInCondition(OpenSubtasksCount.NAME_CONDITION, task);
        actual = openSubtasksCount.getResults();
        assertEquals(1, actual.size());
        assertEquals(Integer.valueOf(1), actual.get(0));

        task = crudDaoJDBC.getTaskById(2);
        openSubtasksCount.setValueInCondition(OpenSubtasksCount.NAME_CONDITION, task);
        actual = openSubtasksCount.getResults();
        assertEquals(1, actual.size());
        assertEquals(Integer.valueOf(2), actual.get(0));

        task = crudDaoJDBC.getTaskById(3);
        openSubtasksCount.setValueInCondition(OpenSubtasksCount.NAME_CONDITION, task);
        actual = openSubtasksCount.getResults();
        assertEquals(1, actual.size());
        assertEquals(Integer.valueOf(0), actual.get(0));

        //получение значений по существующей задаче без подзадач
        task = crudDaoJDBC.getTaskById(9);
        openSubtasksCount.setValueInCondition(OpenSubtasksCount.NAME_CONDITION, task);
        actual = openSubtasksCount.getResults();
        assertEquals(1, actual.size());
        assertEquals(Integer.valueOf(0), actual.get(0));
    }

    /**
     * Тестирование получения результатов при не указании предварительном критерия выборки
     */
    @Test (expected = NullPointerException.class)
    public void test2GetResults() throws Exception {
        OpenSubtasksCount openSubtasksCount = new OpenSubtasksCount(new SelectionOpenSubtasksCountByTask(operatorDB));
        openSubtasksCount.getResults();
    }

}
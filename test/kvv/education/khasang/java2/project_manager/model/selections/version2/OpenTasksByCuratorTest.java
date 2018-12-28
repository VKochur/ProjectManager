package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDaoJDBC;
import kvv.education.khasang.java2.project_manager.model.entity.Curator;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionOpenTasksByCurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Задача "Менеджер проектов"
 * <p>
 * Тестирование методов обертки выборки открытых задач за куратором
 */
public class OpenTasksByCuratorTest {
    private static OperatorDB operatorDB;
    private static CrudDaoJDBC crudDAO;

    @BeforeClass
    public static void init() {
        operatorDB = new OperatorDB("test/test.db");
        crudDAO = new CrudDaoJDBC("test/test.db");
    }

    /**
     * Тестирование получение результатов выборки
     *
     * - тестирование получения результатов без указания критерия выборки
     * - тестирование получения результатов с указанием критерия выборки
     * - тестирование получения результатов после обнуления значения критерия выборки
     *
     * @throws SQLException
     */
    @Test
    public void testGetResults() throws SQLException {
        // тестирование получения результатов без указания критерия выборки
        OpenTasksByCurator openTasksByCurator = new OpenTasksByCurator(new SelectionOpenTasksByCurator(operatorDB));
        List<Task> actual = openTasksByCurator.getResults();
        assertEquals(0, actual.size());

        // тестирование получения результатов с указанием критерия выборки
        Curator curator = new CrudDaoJDBC(operatorDB.getPathDB()).getCuratorById(1);
        openTasksByCurator.setValueInCondition(OpenTasksByCurator.NAME_CONDITION, curator);
        actual = openTasksByCurator.getResults();
        assertEquals(6, actual.size());
        assertTrue(actual.containsAll(Arrays.asList(new Task[] {crudDAO.getTaskById(1), crudDAO.getTaskById(2), crudDAO.getTaskById(6), crudDAO.getTaskById(11),
                crudDAO.getTaskById(12), crudDAO.getTaskById(13)})));

        curator = new CrudDaoJDBC(operatorDB.getPathDB()).getCuratorById(2);
        openTasksByCurator.setValueInCondition(OpenTasksByCurator.NAME_CONDITION, curator);
        actual = openTasksByCurator.getResults();
        assertEquals(1, actual.size());
        assertTrue(actual.contains(crudDAO.getTaskById(9)));

        curator = new CrudDaoJDBC(operatorDB.getPathDB()).getCuratorById(3);
        openTasksByCurator.setValueInCondition(OpenTasksByCurator.NAME_CONDITION, curator);
        actual = openTasksByCurator.getResults();
        assertEquals(0, actual.size());

        //тестирование получения результатов после обнуления значения критерия выборки
        openTasksByCurator.setValueInCondition(OpenTasksByCurator.NAME_CONDITION, null);
        actual = openTasksByCurator.getResults();
        assertEquals(0, actual.size());

    }

    /**
     * Тестирование установки значения в критерий выборки
     *  -1.проверка что без установки значение критерия null
     *  -2.изменение значения критерия
     *  -3.обнуления критерия
     */
    @Test
    public void testSetValueCondition() throws SQLException {
        OpenTasksByCurator openTasksByCurator = new OpenTasksByCurator(new SelectionOpenTasksByCurator(operatorDB));
        String nameCondition = OpenTasksByCurator.NAME_CONDITION;
        //1
        assertNull(openTasksByCurator.getConditions().get(nameCondition));
        //2
        Curator curator = new CrudDaoJDBC(operatorDB.getPathDB()).getCuratorById(1);
        openTasksByCurator.setValueInCondition(nameCondition, curator);
        assertEquals(curator, openTasksByCurator.getConditions().get(nameCondition));
        //3
        openTasksByCurator.setValueInCondition(nameCondition, null);
        assertNull(openTasksByCurator.getConditions().get(nameCondition));
    }

    /**
     * Тестирование установки значения в критерий выборки
     *  Установка значения критерия с несуществующим названием
     */
    @Test (expected = IllegalArgumentException.class)
    public void test2SetValueCondition() throws SQLException {
        OpenTasksByCurator openTasksByCurator = new OpenTasksByCurator(new SelectionOpenTasksByCurator(operatorDB));
        Curator curator = new CrudDaoJDBC(operatorDB.getPathDB()).getCuratorById(1);
        openTasksByCurator.setValueInCondition("Несуществующий критерий", curator);
    }

    /**
     * Тестирование установки значения в критерий выборки
     *  Установка значения критерия с существующим названием, но значение не является объектом нужного класса
     */
    @Test (expected = IllegalArgumentException.class)
    public void test3SetValueCondition() throws SQLException {
        OpenTasksByCurator openTasksByCurator = new OpenTasksByCurator(new SelectionOpenTasksByCurator(operatorDB));
        Curator curator = new CrudDaoJDBC(operatorDB.getPathDB()).getCuratorById(1);
        openTasksByCurator.setValueInCondition(OpenTasksByCurator.NAME_CONDITION, "значение критерия");
    }
}
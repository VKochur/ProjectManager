package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDAO;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDaoJDBC;
import kvv.education.khasang.java2.project_manager.model.entity.Curator;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionCuratorsByOverdueForDate;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Задача "Менеджер проектов"
 *
 * Тестирование получения результатов обертки выборки кураторов
 */
public class CuratorsByOverdueTest {
    private static OperatorDB operatorDB;
    private static CrudDAO crudDAO;

    @BeforeClass
    public static void init() {
        operatorDB = new OperatorDB("test/test.db");
        crudDAO = new CrudDaoJDBC("test/test.db");
    }

    /**
     * Тестирование получения результата выборки
     * @throws Exception
     */
    @Test
    public void testGetResults() throws Exception {
        CuratorsByOverdue curatorsByOverdue= new CuratorsByOverdue(new SelectionCuratorsByOverdueForDate(operatorDB, Util.getDateByString("20.06.2018").getTime()));
        List<Curator> actual = curatorsByOverdue.getResults();
        assertEquals(3, actual.size());
        assertTrue(actual.contains(crudDAO.getCuratorById(1)));
        assertTrue(actual.contains(crudDAO.getCuratorById(2)));
        assertTrue(actual.contains(null));
    }

    /**
     * Тестирование установки значений в критерии выборки
     * @throws SQLException
     */
    @Test (expected = IllegalArgumentException.class)
    public void testSetValueCondition() throws SQLException {
        CuratorsByOverdue curatorsByOverdue= new CuratorsByOverdue(new SelectionCuratorsByOverdueForDate(operatorDB, Util.getDateByString("20.06.2018").getTime()));
        curatorsByOverdue.setValueInCondition("nameCondition", "value");
    }

}
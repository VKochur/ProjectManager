package kvv.education.khasang.java2.project_manager.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

/**
 * Задача "Менеджер проектов"
 *
 * Тестирование метода получения даты из строки
 *
 */
public class UtilTest {
    /**
     * Тестирование получения даты по валидной строке
     */
    @Test
    public void testGetDateByValidString() {
        Calendar value = Util.getDateByString("01.01.2017");
        Assert.assertEquals(1, value.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(0, value.get(Calendar.MONTH));
        Assert.assertEquals(2017, value.get(Calendar.YEAR));

        value = Util.getDateByString("31.10.1956");
        Assert.assertEquals(31, value.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(9, value.get(Calendar.MONTH));
        Assert.assertEquals(1956, value.get(Calendar.YEAR));

        value = Util.getDateByString("28.12.1756");
        Assert.assertEquals(28, value.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(11, value.get(Calendar.MONTH));
        Assert.assertEquals(1756, value.get(Calendar.YEAR));

        value = Util.getDateByString("29.02.2016");
        Assert.assertEquals(29, value.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(1, value.get(Calendar.MONTH));
        Assert.assertEquals(2016, value.get(Calendar.YEAR));

        Assert.assertNull(Util.getDateByString(null));
        Assert.assertNull(Util.getDateByString("null.null.null"));
    }

    //тестирование получения исключений при невалидной входной строке

    @Test(expected = IllegalArgumentException.class)
    public void testGetDateByNotValidString() {
        Util.getDateByString("31.11.2024");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDateByNotValidString2() {
        Util.getDateByString("29.02.2018");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDateByNotValidString3() {
        Util.getDateByString("29.14.2018");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDateByNotValidString4() {
        Util.getDateByString("01-02-2013");
    }

}
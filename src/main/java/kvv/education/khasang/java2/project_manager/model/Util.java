package kvv.education.khasang.java2.project_manager.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Util {

    /**
     *
     * @param date
     * @return null, при date ==  null || date =="null.null.null"
     */
    public static Calendar getDateByString(String date) {

        if ((date == null) || (date == "null.null.null")){
            return null;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        //чтобы проверялась корректность даты
        format.setLenient(false);
        Calendar date1 = new GregorianCalendar();
        try {
            date1.setTime(format.parse(date));
        } catch (ParseException e) {
            throw new IllegalArgumentException("Некорректный формат: \"" + date + "\" Для даты  д.б: дд.мм.гггг");
        }
        date1 = format.getCalendar();
        return date1;
    }

    public static String getStringFromDate(Date date){
        return String.format("%td.%tm.%tY", date, date, date);
    }

}

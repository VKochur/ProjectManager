package kvv.education.khasang.java2.project_manager.model.query_results;

import kvv.education.khasang.java2.project_manager.model.db.OperatorDB;

import java.sql.SQLException;
import java.util.List;

/**
 * Ответчик на запросы Askor к бд
 */
public abstract class Answerer {

    //возможные заголовки столбцов в ответе на запрос
    public static String TITLE_ID_TASK = "id задачи";
    public static String TITLE_IS_PROJECT = "Признак проекта";
    public static String TITLE_NAME = "Задача";
    public static String TITLE_STARTED = "Дата начала";
    public static String TITLE_DURING = "Длительность";
    public static String TITLE_ID_CURATOR = "id ответственного";
    public static String TITLE_CURATOR = "Ответственный";
    public static String TITLE_STATUS_TASK = "Статус задачи";
    public static String TITLE_CONTACT_CURATOR = "Контакт";
    public static String TITLE_COUNT = "Количество";

    protected String name;

    //оператор ДБ, в нем содержится также и информация к какой базе идет подключение
    protected OperatorDB operatorDB;

    public Answerer(OperatorDB operatorDB) {
        this.operatorDB = operatorDB;
    }

    public String getName() {
        return name;
    }

    /**
     * Список заголовков в "таблице" ответе
     *
     * @return
     */
    public abstract List<String> getNameColumns();

    /**
     * Ответ в виде "таблицы"
     *
     * @return
     */
    public abstract List<List<String>> getAnswer() throws SQLException;

    public int getCountRows() {
        try {
            return getAnswer().size();
        } catch (SQLException e) {
            return 0;
        }
    }

    public int getCountColumns() {
        return getNameColumns().size();
    }

    @Override
    public String toString() {
        return getName();
    }
}

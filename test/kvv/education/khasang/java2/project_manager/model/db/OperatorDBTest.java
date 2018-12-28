package kvv.education.khasang.java2.project_manager.model.db;

import kvv.education.khasang.java2.project_manager.model.queryes.Askor;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Задача "Менеджер проектов"
 * Тестирование методов OperatorDB - класса, методы которого инкапсулируют создание соединений с бд,
 * выполнение запросов к ней и получение результатов
 */
public class OperatorDBTest {

    private static final String pathTestDB = "test/test.db";
    private static OperatorDB operatorDB;

    //первоначальное наполнение таблиц
    private static final String[][] TASKS_CONTENT = {
            {"1", "0", "true", "Проект1", "30.09.2017", "365", "OPEN", "1"},
            {"2", "0", "true", "Проект2", "02.09.2019", "200", "OPEN", "1"},
            {"3", "1", "false", "Проект1 Задача1", "01.10.2017", "5", "CLOSED", "1"},
            {"4", "3", "false", "Проект1 Задача11", "01.10.2017", "2", "CLOSED", "1"},
            {"5", "3", "false", "Проект1 Задача12", "03.10.2017", "1", "CLOSED", "3"},
            {"6", "1", "false", "Проект1 Задача2", "20.08.2018", "30", "OPEN", "1"},
            {"7", "6", "false", "Проект1 Задача21", "05.10.2017", "7", "CLOSED", "2"},
            {"8", "6", "false", "Проект1 Задача22", "10.10.2017", "300", "CLOSED", "1"},
            {"9", "6", "false", "Проект1 Задача23", "01.04.2018", "30", "OPEN", "2"},
            {"10", "8", "false", "Проект1 Задача221", "01.05.2018", "10", "OPEN", "2"},
            {"11", "6", "false", "Проект1 Задача24", "01.07.2017", "50", "OPEN", "1"},
            {"12", "2", "false", "Проект2 Задача1", "01.01.2019", "1", "OPEN", "1"},
            {"13", "2", "false", "Проект2 Задача2", "01.08.2019", "20", "OPEN", "1"},
            {"14", "0", "true", "Проект3", "01.08.2019", "20", "CLOSED", "1"}
    };

    private static final String[][] CURATORS_CONTENT = {
            {"1", "Куратор1", "контакт1"},
            {"2", "Куратор2", "контакт2"},
            {"3", "Куратор3", "контакт3"},
    };

    @BeforeClass
    public static void initDB() {
        try {
            operatorDB = new OperatorDB(pathTestDB);
        } catch (IllegalArgumentException e) {
            //нет по указанному пути файла бд
            operatorDB = new OperatorDB(pathTestDB, TASKS_CONTENT, CURATORS_CONTENT);
        }
    }

    /**
     * Тестирование инстанцирования оператора к бд, которой не существует по указанному адресу
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor1() {
        String pathNotExistsDB = "test/notexists.db";
        new OperatorDB(pathNotExistsDB);
    }

    /**
     * Тестирование инстанцирование оператора к бд, предусматривающего создание новой бд, при условии
     * что по указанному пути базу уже существует
     */
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor2() {
        String pathExistsDB = "test/test.db";
        new OperatorDB(pathExistsDB, new String[0][0], new String[0][0]);
    }

    /**
     * Тестирование получения пути к бд, с которой взаимодействует операторБД
     */
    @Test
    public void testGetPathToDB() {
        assertEquals(pathTestDB, operatorDB.getPathDB());
        assertEquals("jdbc:sqlite:" + pathTestDB, operatorDB.getUrl());
    }

    /**
     * Тестирование метода оператора создающего новые таблицы
     * Проверка создания новых таблиц, контента таблиц
     */
    @Test
    public void testCreateNewTables() {
        //для тестирования создания новыъх таблиц создадим новую бд
        String pathDB = "test/forCreateTable";
        String url = "jdbc:sqlite:" + pathDB;
        //создадим новую базу с указанными таблицами и определенным контентом
        new OperatorDB(pathDB, TASKS_CONTENT, CURATORS_CONTENT);
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
            //----------------------
            String sql = "SELECT " +
                    "name " +
                    "FROM sqlite_master " +
                    "WHERE type = 'table' " +
                    "order by name";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            StringBuilder actual = new StringBuilder("");
            String expected = "curators;sqlite_sequence;tasks;";
            while (resultSet.next()) {
                actual.append(resultSet.getString(1)).append(";");
            }
            //проверка перечня созданных таблиц
            assertEquals("созданные таблицы", expected, actual.toString());
            //-----------------------
            sql = "SELECT " +
                    "* " +
                    "from " +
                    "tasks " +
                    "order by id";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            actual = new StringBuilder("");

            while (resultSet.next()) {
                StringBuilder row = new StringBuilder("");
                row.append(resultSet.getString(1)).append(";");
                row.append(resultSet.getString(2)).append(";");
                row.append(resultSet.getString(3)).append(";");
                row.append(resultSet.getString(4)).append(";");
                row.append(resultSet.getString(5)).append(";");
                row.append(resultSet.getString(6)).append(";");
                row.append(resultSet.getString(7)).append(";");
                row.append(resultSet.getString(8)).append(";");
                actual.append(row).append("|");
            }

            StringBuilder expectedContent = new StringBuilder();
            for (int i = 0; i < TASKS_CONTENT.length; i++) {
                for (int j = 0; j < TASKS_CONTENT[0].length; j++) {
                    expectedContent.append(TASKS_CONTENT[i][j]).append(";");
                }
                expectedContent.append("|");
            }
            //проверка содержания таблицы
            assertEquals("контент таблицы заданий", expectedContent.toString(), actual.toString());
            //------------------------
            sql = "SELECT " +
                    "* " +
                    "from " +
                    "curators " +
                    "order by id";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            actual = new StringBuilder("");
            while (resultSet.next()) {
                StringBuilder row = new StringBuilder("");
                row.append(resultSet.getString(1)).append(";");
                row.append(resultSet.getString(2)).append(";");
                row.append(resultSet.getString(3)).append(";");
                actual.append(row).append("|");
            }

            expectedContent = new StringBuilder();
            for (int i = 0; i < CURATORS_CONTENT.length; i++) {
                for (int j = 0; j < CURATORS_CONTENT[0].length; j++) {
                    expectedContent.append(CURATORS_CONTENT[i][j]).append(";");
                }
                expectedContent.append("|");
            }
            //проверка содержания таблицы
            assertEquals("контент таблицы ответственных", expectedContent.toString(), actual.toString());


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //физическое удаление файла
            new File(pathDB).delete();
        }
    }

    /**
     * Тестирование метода выполняющего запрос (запрос получения информации) к бд на основе объекта Ascor
     *
     * @throws SQLException
     */
    @Test
    public void testExecuteQuery() throws SQLException {
        OperatorDB operatorDB = new OperatorDB(pathTestDB);
        List<List<String>> actual = operatorDB.executeQuery(new Askor() {
            @Override
            public String sqlForPreparedStatement() {
                return "select tasks.name from tasks where (id_parent = ?) and (status = ?) order by tasks.id";
            }

            @Override
            public String[] getArgs() {
                return new String[]{"0", "OPEN"};
            }

            @Override
            public String[] getTypeArgs() {
                return new String[]{Askor.ARG_TYPE_INT, Askor.ARG_TYPE_STRING};
            }
        });

        assertEquals("количество возвращенных строк", 2, actual.size());
        assertEquals("количество возвращенных столбцов", 1, actual.get(0).size());
        assertEquals("название открытые проекты", "[[Проект1], [Проект2]]", actual.toString());
    }

    /**
     * Тестирование метода выполняющего запрос (запрос меняющий данные) к бд на основе Ascor
     *
     * @throws SQLException
     */
    @Test
    public void testUpdateQuery() throws SQLException {
        String path = "test/forUpdate";
        //создадим пустую базу
        OperatorDB operatorDB = new OperatorDB(path, null, null);
        try {
            Askor askor = new Askor() {
                @Override
                public String sqlForPreparedStatement() {
                    return "insert into curators (name, contact) values ('name', 'contact')";
                }

                @Override
                public String[] getArgs() {
                    return new String[]{};
                }

                @Override
                public String[] getTypeArgs() {
                    return new String[]{};
                }
            };

            assertEquals("колво измененных строчек", 1, operatorDB.updateQuery(askor));
            operatorDB.updateQuery(askor);

            List<List<String>> actual = operatorDB.executeQuery(new Askor() {
                @Override
                public String sqlForPreparedStatement() {
                    return "select * from curators";
                }

                @Override
                public String[] getArgs() {
                    return new String[]{};
                }

                @Override
                public String[] getTypeArgs() {
                    return new String[]{};
                }
            });

            assertEquals("содержание таблицы ответственных", "[[1, name, contact], [2, name, contact]]", actual.toString());

        } finally {
            //физическое удаление созданной базы
            new File(path).delete();
        }
    }
}
package kvv.education.khasang.java2.project_manager.model.db;

import kvv.education.khasang.java2.project_manager.model.queryes.Askor;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

import static kvv.education.khasang.java2.project_manager.model.db.ProjectDBStructura.*;

/**
 * Основной класс, реализующий взаимодействие модели с БД проектов
 */
public class OperatorDB {

    private static final String DRIVER = "org.sqlite.JDBC";

    //пусть к файлу БД
    private String pathDB;

    //параметры соединения с БД
    private String url;

    static {
        //загрузка драйвера JDBC
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Драйвер не найден: " + DRIVER);
            System.exit(1);
        }
    }

    /**
     * Инстанцирует оператораБД к бд, находящейся по указанному пути,
     * Если по указанному пути нет БД, выбрасывает IllegalArgumentException
     *
     * @param pathDB путь к файлу БД
     */
    public OperatorDB(String pathDB) {
        File fileDB = new File(pathDB);
        if (!fileDB.exists()) {
            throw new IllegalArgumentException("Нет файла БД по пути: " + pathDB);
        }
        if (!fileDB.isFile()) {
            throw new IllegalArgumentException("Нет файла БД по пути: " + pathDB);
        }

        this.pathDB = pathDB;
        url = "jdbc:sqlite:" + this.pathDB;
    }

    /**
     * Инстанцирует оператораБД к НОВОЙ бд, создаваемой по указанному пути.
     * Если по указанному пути уже существует файл, выбрасывает IllegalArgumentException
     * Чтобы бд создалась пустая, только структура таблиц: OperatorDB(pathDB, null, null)
     *
     * @param pathDB
     * @param tasksContent
     * @param curatorsContent
     */
    public OperatorDB(String pathDB, String[][] tasksContent, String[][] curatorsContent) {
        File fileDB = new File(pathDB);
        if (fileDB.isFile() && (fileDB.exists())) {
            throw new IllegalArgumentException("По указанному пути уже есть файл, невозможно создать новую БД управления проектами: " + pathDB);
        }

        this.pathDB = pathDB;
        url = "jdbc:sqlite:" + this.pathDB;

        createNewTables();
        if ((tasksContent != null) && (curatorsContent != null)) {
            fillTables(curatorsContent, tasksContent);
        }
    }

    public String getPathDB() {
        return pathDB;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Создает sql запрос на создание таблицы в БД
     *
     * @param nameTable        имя создаваемой таблицы
     * @param tableNameColumns название столбцов
     * @param tableTypeColumns типы столбцов
     * @return sql запрос на создание таблицы в БД
     */
    private static String getSqlQueryCreateTable(String nameTable, String[] tableNameColumns, String[] tableTypeColumns) {
        String sql = "CREATE TABLE IF NOT EXISTS " + nameTable + " (" + tableNameColumns[0] + " " + tableTypeColumns[0] + " PRIMARY KEY AUTOINCREMENT NOT NULL";
        StringBuilder sqlBuilder = new StringBuilder().append(sql);
        for (int i = 1; i < tableNameColumns.length; i++) {
            sqlBuilder.append(", " + tableNameColumns[i] + " " + tableTypeColumns[i]);
        }
        sql = sqlBuilder.toString() + ")";
        return sql;
    }

    /**
     * Создает новые таблицы для задач и кураторов
     */
    public void createNewTables() {
        try (Connection connection = DriverManager.getConnection(url)) {
            Statement statement = connection.createStatement();
            String sql;
            sql = OperatorDB.getSqlQueryCreateTable(NAME_TABLE_TASKS, TSK_COLUMNS, TSK_COLUMNS_TYPE);
            statement.executeUpdate(sql);
            sql = OperatorDB.getSqlQueryCreateTable(NAME_TABLE_CURATORS, CURATORS_COLUMNS, CURATORS_COLUMNS_TYPE);
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Заполняет контекстом таблицы по задачам и кураторам
     */
    private void fillTables(String[][] tableCuratorsContent, String[][] tableTasksContent) {
        fillTasksContent(tableTasksContent);
        fillCuratorsContent(tableCuratorsContent);
    }

    private void fillCuratorsContent(String[][] tableCuratorsContent) {
        fillTable(NAME_TABLE_CURATORS, CURATORS_COLUMNS, CURATORS_COLUMNS_TYPE, tableCuratorsContent);
    }

    private void fillTasksContent(String[][] tableTasksContent) {
        fillTable(NAME_TABLE_TASKS, TSK_COLUMNS, TSK_COLUMNS_TYPE, tableTasksContent);
    }

    /**
     * Заполняет таблицу контекстом
     *
     * @param nameTable   имя таблицы
     * @param nameColumns названия столбцов
     * @param typeColumns типы столбцов
     * @param content     содержание таблицы
     */
    //сюда нужно передавать не только контекст, но и инфо в какую таблицу
    private void fillTable(String nameTable, String[] nameColumns, String[] typeColumns, String[][] content) {
        for (int i = 0; i < content.length; i++) {
            String[] row = content[i];
            addRow(nameTable, nameColumns, typeColumns, row);
        }
    }

    /**
     * Добавляет строку в таблицу
     *
     * @param nameTable   название таблицы
     * @param nameColumns название столбцов в таблице
     * @param typeColumns типы столбцов
     * @param values      значения
     */
    private void addRow(String nameTable, String[] nameColumns, String[] typeColumns, String[] values) {
        try (Connection connection = DriverManager.getConnection(url)) {

            Statement statement = connection.createStatement();

            String arg1 = nameTable + " (";
            StringBuilder stringBuilder = new StringBuilder().append(arg1);
            for (int i = 0; i < nameColumns.length; i++) {
                stringBuilder.append(nameColumns[i]).append(", ");
            }
            arg1 = stringBuilder.toString();
            arg1 = arg1.substring(0, arg1.length() - 2);
            arg1 += ")";

            String arg2 = " (";
            stringBuilder = new StringBuilder().append(arg2);
            for (int i = 0; i < typeColumns.length; i++) {
                if (typeColumns[i].equals("TEXT")) {
                    stringBuilder.append(String.format("'%s'", values[i]));
                } else {
                    stringBuilder.append(values[i]);
                }
                stringBuilder.append(", ");
            }
            arg2 = stringBuilder.toString();
            arg2 = arg2.substring(0, arg2.length() - 2);
            arg2 += ");";

            String sql = "INSERT INTO " + arg1 + " VALUES " + arg2;
            System.out.println(sql);
            statement.executeUpdate(sql);
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает результат выполнения запроса вида SELECT
     *
     * @param askor запрос
     * @return "таблица" результатов
     */
    public List<List<String>> executeQuery(Askor askor) throws SQLException {
        List<List<String>> out = new LinkedList<>();
        PreparedStatement preparedStatement;
        try (Connection connection = DriverManager.getConnection(url)) {
            preparedStatement = solveStatement(connection, askor);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                List<String> row = new LinkedList<>();
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 0; i < columnCount; i++) {
                    row.add(resultSet.getString(i + 1));
                }
                out.add(row);
            }
        }
        return out;
    }

    /**
     * Выполняет запрос на обновление информации в БД
     *
     * @param askor запрос
     * @return колво обновленных строк
     */
    public int updateQuery(Askor askor) {
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement preparedStatement = solveStatement(connection, askor);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * Получает PrepareStatement для указанного соединения на основе запроса askor
     *
     * @param connection соединение в котором создается выражение
     * @param askor      запрос к БД, на основе которого создается выражение
     * @return
     * @throws SQLException
     */
    private static PreparedStatement solveStatement(Connection connection, Askor askor) throws SQLException {
        String sql = askor.sqlForPreparedStatement();
        String[] args = askor.getArgs();
        String[] typeArgs = askor.getTypeArgs();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            String type = typeArgs[i];
            if (type == Askor.ARG_TYPE_INT) {
                preparedStatement.setInt(i + 1, Integer.parseInt(args[i]));
            } else if (type == Askor.ARG_TYPE_STRING) {
                preparedStatement.setString(i + 1, args[i]);
            } else {
                throw new IllegalArgumentException("Неизвестный тип параметра");
            }
        }
        return preparedStatement;
    }
}

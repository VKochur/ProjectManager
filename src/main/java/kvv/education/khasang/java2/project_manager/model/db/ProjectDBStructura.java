package kvv.education.khasang.java2.project_manager.model.db;

public class ProjectDBStructura {

    //пакетный доступ у переменных

    //название таблицы информации о задачах
    static final String NAME_TABLE_TASKS = "tasks";
    //структура таблицы
    static final String[] TSK_COLUMNS = {"id", "id_parent", "project_flag", "name", "started", "during", "status", "id_curator"};
    static final String[] TSK_COLUMNS_TYPE = {"INTEGER", "INTEGER", "TEXT", "TEXT", "TEXT", "TEXT", "TEXT", "INTEGER"};

    //название таблицы информации о кураторах
    static final String NAME_TABLE_CURATORS = "curators";
    //структура таблицы
    static final String[] CURATORS_COLUMNS = {"id", "name", "contact"};
    static final String[] CURATORS_COLUMNS_TYPE = {"INTEGER", "TEXT", "TEXT"};

    //первоначальное наполнение таблиц
    static final String[][] DEFAULT_TASKS_CONTENT = {
            {"1", "0", "true", "Пройти обучение java", "30.09.2017", "365", "OPEN", "1"},
            {"2", "0", "true", "Построить дом", "02.09.2019", "200", "OPEN", "1"},
            {"3", "1", "false", "Провести платеж", "01.10.2017", "5", "CLOSED", "1"},
            {"4", "3", "false", "Отнести деньги", "01.10.2017", "2", "CLOSED", "1"},
            {"5", "3", "false", "Произвести перевод", "03.10.2017", "1", "CLOSED", "3"},
            {"6", "1", "false", "Пройти курс javaTry - javaIntern", "20.08.2018", "30", "OPEN", "1"},
            {"7", "6", "false", "Выдать материалы на самостоятельное обучение", "05.10.2017", "7", "CLOSED", "2"},
            {"8", "6", "false", "Изучать выданные материалы, сделать ДЗ, отправить на проверку", "10.10.2017", "300", "CLOSED", "1"},
            {"9", "6", "false", "Проверить ДЗ", "01.04.2018", "30", "OPEN", "2"},
            {"10", "6", "false", "Сообщить о старте курса интернДжава", "01.05.2018", "10", "OPEN", "2"},
            {"11", "6", "false", "Пройти курс интернДжава", "01.07.2017", "50", "OPEN", "1"},
            {"12", "2", "false", "Достать денег на строительство", "01.01.2019", "1", "OPEN", "1"},
            {"13", "2", "false", "Запустить процесс строительства", "01.08.2019", "20", "OPEN", "1"}
    };

    static final String[][] DEFAULT_CURATORS_CONTENT = {
            {"1", "victor", "v.kochurov@rambler.ru"},
            {"2", "khasang", "khasang@support.ru"},
            {"3", "vtb", "vtb@support.ru"}
    };
}

package kvv.education.khasang.java2.project_manager.model.queryes;

/**
 * Запрос к базе
 */
public interface Askor {

    /**
     * Возможные типы аргументов
     */
    String ARG_TYPE_STRING = "argTypeString";
    String ARG_TYPE_INT = "argTypeInt";

    /**
     * sql запрос для preparedStatement
     *
     * @return
     */
    String sqlForPreparedStatement();

    /**
     * Значения подстановочных аргументов
     *
     * @return
     */
    default String[] getArgs(){
        return new String[0];
    }

    /**
     * Типы подставляемых аргументов
     *
     * @return
     */
    default String[] getTypeArgs(){
        return new String[0];
    }

}

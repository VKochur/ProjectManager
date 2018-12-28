package kvv.education.khasang.java2.project_manager.model.selections.version2;


import kvv.education.khasang.java2.project_manager.model.selections.Selection;

import java.sql.SQLException;
import java.util.*;

/**
 * Выборка данных соответствующих определенным критериям. Обертка для Selection
 * T - найденые элементы
 */
public abstract class SelectionWrapple<T> {

    protected final Selection selection;

    protected Map<String, Object> conditions;

    public SelectionWrapple(Selection selection) {
        this.selection = selection;
        //инициализируем сразу условия в null
        conditions = new LinkedHashMap<>();
        //названия ключей возьмем из getMetaInfoAboutConditions(), сохраняя порядок
        Set<String> keys = getMetaInfoAboutConditions().keySet();
        for (String key : keys) {
            conditions.put(key, null);
        }
    }

    public String getName() {
        return selection.getName();
    }

    public List<String> getTitles() {
        return selection.getTitles();
    }

    /**
     * Установить значения для критерия (условия) выборки
     * <p>
     * throws IllegalArgumentException в случае если нет критерия выборки с указанном названием, или если в качестве значения критерия
     * указан объект несоответствующего класса
     * <p>
     * Для обнуления значения критерия следует передать object = null
     *
     * @param conditionName название критерия
     * @param object        значение критерия
     */
    public final void setValueInCondition(String conditionName, Object object) {
        if (conditions.containsKey(conditionName)) {
            if (object != null) {
                Class clazz = getMetaInfoAboutConditions().get(conditionName);
                if (clazz.isInstance(object)) {
                    conditions.put(conditionName, object);
                } else {
                    throw new IllegalArgumentException(object + " должен быть экзепляром " + clazz + ". Данные о критериях выборки: " + getMetaInfoAboutConditions().toString());
                }

            } else {
                conditions.put(conditionName, null);
            }
        } else {
            throw new IllegalArgumentException("У выборки нет условия с названием " + conditionName + ".  Данные о критериях выборки: " + getMetaInfoAboutConditions().toString());
        }
    }

    /**
     * Получить условия критерии выборки
     *
     * @return
     */
    public Map<String, Object> getConditions() {
        return Collections.unmodifiableMap(conditions);
    }

    /**
     * Список объектов, результат выборки. Использует List<List<String>>> selection.getResults()
     * и this.getAffectedObject(List<String> row)
     *
     * @return
     * @throws SQLException
     */
    public List<T> getResults() throws SQLException {
        selection.setArgs(getAffectedArgsForSelection(conditions));
        List<List<String>> tempResults = selection.getResults();
        List<T> results = new ArrayList<>();
        for (List<String> row : tempResults) {
            T current = getAffectedObject(row);
            results.add(current);

        }
        return results;
    }

    /**
     * Результаты выборки, представленные в виде таблицы
     * @return
     * @throws SQLException
     */
    public List<List<String>> getResultsAsTable() throws SQLException {
        selection.setArgs(getAffectedArgsForSelection(conditions));
        return selection.getResults();
    }

    /**
     * Получение объекта на основе данных в определенной строке таблицы результатов. Как если бы таблица результатов была результатом указанной выборки
     * @param numberRow номер строки (строки считаются с 0, т.е. 0, 1, 2, ...)
     * @param results таблица результатов
     * @param selectionWrapple выборка, результатом которой является таблица результатов results
     * @return объект, который являлся бы результатом выполнения выборки selectionWrapple, отраженный в numberRow строке результатов results,
     *
     * т.е. метод выдает соответствие строки результатов выборки объекту.
     */
    public static Object getObjectAffectedRowInTable(int numberRow, List<List<String>> results, SelectionWrapple selectionWrapple) throws SQLException {
        if ((numberRow < 0) || (numberRow > results.size() - 1)) {
            throw new IllegalArgumentException("Не корректный номер строки, выход за границы. numberRow = " + numberRow);
        }
        List<String> rowAffectedToObj = results.get(numberRow);
        Object object = selectionWrapple.getAffectedObject(rowAffectedToObj);
        return object;
    }

    /**
     * Получение объекта из строки таблицы результатов выборки selection
     *
     * @param row
     * @return
     */
    protected abstract T getAffectedObject(List<String> row) throws SQLException;

    /**
     * Перевод критериев обертки выборки SelectionWrapple в критерии выборки Selection
     *
     * @param conditions
     * @return
     */
    protected abstract Map<String, String> getAffectedArgsForSelection(Map<String, Object> conditions);

    /**
     * Соответствие - какое условие объектом какого класса представлено. Сохранен порядок занесения соответствий
     *
     * @return
     */
    public abstract LinkedHashMap<String, Class> getMetaInfoAboutConditions();

    @Override
    public String toString() {
        return getName();
    }
}

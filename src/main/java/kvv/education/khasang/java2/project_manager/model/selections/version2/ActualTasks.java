package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.entity.CrudDAO;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDaoJDBC;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionActualTaskForDate;

import java.sql.SQLException;
import java.util.*;

/**
 * Обертка для выборки актуальных задач.
 * В качестве объектов, которые выдаются как результат выборки определены Task
 */
public class ActualTasks extends SelectionWrapple<Task> {
    public ActualTasks(SelectionActualTaskForDate selection) {
        super(selection);
    }

    /**
     * Возвращает Task, соотв-ий строке таблицы результатов выборки SelectionActualTask
     * @param row
     * @return
     */
    @Override
    protected Task getAffectedObject(List<String> row) throws SQLException {
        CrudDAO crudDAO = new CrudDaoJDBC(selection.getOperatorDB().getPathDB());
        return crudDAO.getTaskById( Integer.valueOf(row.get(0)) );
    }

    /**
     * Выборка актуальных задач не имеет никаких критериев для уточнения
     * @param conditions
     * @return
     */
    @Override
    public Map<String, String> getAffectedArgsForSelection(Map<String, Object> conditions) {
        return Collections.EMPTY_MAP;
    }

    /**
     * У выборки нет критериев для уточнения
     * @return
     */
    @Override
    public LinkedHashMap<String, Class> getMetaInfoAboutConditions() {
        return new LinkedHashMap<>();
    }
}

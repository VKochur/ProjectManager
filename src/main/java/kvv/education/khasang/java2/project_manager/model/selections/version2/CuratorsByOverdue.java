package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.entity.CrudDAO;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDaoJDBC;
import kvv.education.khasang.java2.project_manager.model.entity.Curator;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionCuratorsByOverdueForDate;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Обертка для выборки кураторов с просроченными заданиями
 *  В качестве объектов, которые выдаются как результат выборки определены id кураторов
 */
public class CuratorsByOverdue extends SelectionWrapple<Curator> {
    public CuratorsByOverdue(SelectionCuratorsByOverdueForDate selection) {
        super(selection);
    }

    /**
     * Возвращает id куратора, соотв-го строке таблицы результатов выборки SelectionCuratorsByOverdue
     * @param row
     * @return
     */
    @Override
    protected Curator getAffectedObject(List<String> row) throws SQLException {
        if (row.get(0) == null) {
          return null;
        } else {
            CrudDAO crudDAO = new CrudDaoJDBC(selection.getOperatorDB().getPathDB());
            return crudDAO.getCuratorById( Integer.valueOf(row.get(0)) );
        }
    }

    /**
     * Выборка кураторов не имеет никаких критериев для уточнения
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

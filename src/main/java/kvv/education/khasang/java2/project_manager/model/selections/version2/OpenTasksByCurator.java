package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.entity.CrudDAO;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDaoJDBC;
import kvv.education.khasang.java2.project_manager.model.entity.Curator;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionOpenTasksByCurator;

import java.sql.SQLException;
import java.util.*;

/**
 * Обертка для выборки открытых задач за конкретным куратором
 *  В качестве объектов, которые выдаются как результат выборки определены Task
 */
public class OpenTasksByCurator extends SelectionWrapple<Task> {
    public static final String NAME_CONDITION = "Куратор";

    public OpenTasksByCurator(SelectionOpenTasksByCurator selection) {
        super(selection);
    }

    @Override
    protected Task getAffectedObject(List<String> row) throws SQLException {
        CrudDAO crudDAO = new CrudDaoJDBC(selection.getOperatorDB().getPathDB());
        return crudDAO.getTaskById( Integer.valueOf(row.get(0)) );
    }

    @Override
   protected Map<String, String> getAffectedArgsForSelection(Map<String, Object> conditions) {
        Map<String, String> result = new HashMap<>();
        Curator curator = (Curator) conditions.get(NAME_CONDITION);
            if (curator != null) {
                result.put(SelectionOpenTasksByCurator.KEY1, curator.getName());
            } else {
                result.put(SelectionOpenTasksByCurator.KEY1, null);
            }
        return result;
    }

    @Override
    public LinkedHashMap<String, Class> getMetaInfoAboutConditions() {
        LinkedHashMap<String, Class> metaInfo = new LinkedHashMap<>();
        metaInfo.put(OpenTasksByCurator.NAME_CONDITION, Curator.class);
        return metaInfo;
    }
}

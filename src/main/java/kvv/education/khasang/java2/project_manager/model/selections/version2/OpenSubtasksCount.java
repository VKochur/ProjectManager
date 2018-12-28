package kvv.education.khasang.java2.project_manager.model.selections.version2;

import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.model.selections.SelectionOpenSubtasksCountByTask;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Обертка для выборки колва открытых подзадач
 * <p>
 * В качестве результата объект класса Integer
 */
public class OpenSubtasksCount extends SelectionWrapple<Integer> {
    public static final String NAME_CONDITION = "Задача";

    public OpenSubtasksCount(SelectionOpenSubtasksCountByTask selection) {
        super(selection);
    }

    @Override
    protected Integer getAffectedObject(List<String> row) {
        return Integer.valueOf(row.get(0));
    }

    @Override
    protected Map<String, String> getAffectedArgsForSelection(Map<String, Object> conditions) {
        Object obj = conditions.get(OpenSubtasksCount.NAME_CONDITION);
        Map<String, String> tmp = new HashMap<>();
        String val;
        //если не проставлено значение задачи выкинется NullPointerException
        if (obj == null) {
            throw new NullPointerException("Не указано условие выборки: критерий выборки " + OpenSubtasksCount.NAME_CONDITION + " =  null");
        }
        Integer idTask = ((Task) obj).getId();
        val = String.valueOf(idTask);
        tmp.put(SelectionOpenSubtasksCountByTask.KEY1, val);
        return tmp;
    }

    @Override
    public LinkedHashMap<String, Class> getMetaInfoAboutConditions() {
        LinkedHashMap<String, Class> tmp = new LinkedHashMap<>();
        tmp.put(OpenSubtasksCount.NAME_CONDITION, Task.class);
        return tmp;
    }
}

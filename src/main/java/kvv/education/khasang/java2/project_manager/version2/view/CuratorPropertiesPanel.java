package kvv.education.khasang.java2.project_manager.version2.view;

import kvv.education.khasang.java2.project_manager.model.entity.Curator;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.PropertiesPanel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Панель свойств для отображения куратора
 */
public class CuratorPropertiesPanel extends PropertiesPanel<Curator> {


    public static CuratorPropertiesPanel getInstance() {
        //Linked - для сохранения порядка
        Map<String, Class> fields = new LinkedHashMap<>();
        fields.put(Decor.ID_CURATOR, Integer.class);
        fields.put(Decor.NAME_CURATOR, String.class);
        fields.put(Decor.CONTACT_CURATOR, String.class);

        return new CuratorPropertiesPanel(fields);
    }

    private CuratorPropertiesPanel(Map<String, Class> fields) {
        super(fields);
    }

    @Override
    public Curator getObjectInstance() {
        Curator curator = new Curator();
        //поочередно забираем из линкеров (нашедшихся по названию поля) значения объектов, и заполняем поля
        curator.setId((Integer) linkers.get(Decor.ID_CURATOR).giveObject());
        curator.setName((String) linkers.get(Decor.NAME_CURATOR).giveObject());
        curator.setContact((String) linkers.get(Decor.CONTACT_CURATOR).giveObject());
        return curator;
    }

    @Override
    public void showOnPanel(Curator obj) {
        //последовательно перебираем линкеры по названию полей и передаем из интересуемы значения
        linkers.get(Decor.ID_CURATOR).takeObject(obj.getId());
        linkers.get(Decor.NAME_CURATOR).takeObject(obj.getName());
        linkers.get(Decor.CONTACT_CURATOR).takeObject(obj.getContact());
    }
}

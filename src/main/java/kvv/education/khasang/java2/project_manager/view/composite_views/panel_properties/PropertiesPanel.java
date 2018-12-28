package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties;

import javax.swing.*;
import java.util.*;

/**
 * Панель, визуально отображающая свойства объекта.
 * Может отображать свойства instanseof Byte, Short, Integer, Long, Float, Double, Boolean, String, Date а также Enum
 * (то, что поддерживает new LinkerFactory().getLinker())
 * <p>
 * Можно задать свой алгоритм сопоставления объектов различных классов визуальным объектам, для этого необходимо реализовать свой LinkerFactory
 * и инстанцировать панель свойств через конструктор  PropertiesPanel(Map<String, Class> fields, LinkerFactory linkerFactory)
 *
 * @param <T> - класс, объекты которого отображаются панелью
 */
public abstract class PropertiesPanel<T> extends JPanel {

    //сопоставление Названия поля и Класса, объект которого хранится в поле
    protected Map<String, Class> fields;

    //сопоставление Названий полей и Линкеров
    protected Map<String, Linker> linkers;

    //фабрика линкеров (сопоставителей визуальных компонентов и объектов различных классов)
    private LinkerFactory linkerFactory;

    /**
     * Панель инстанцируется и настраивается в зависимости от того объекты каких Классов будут отображаться
     *
     * @param fields - карта соответствий "Название представляемого объекта (Название поля)" - "Класс представляемого объекта"
     */
    public PropertiesPanel(Map<String, Class> fields) {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.fields = fields;
        linkers = new HashMap<>();
        //здесь начинаем размещение компонентов
        Set<Map.Entry<String, Class>> entry = fields.entrySet();
        for (Map.Entry<String, Class> stringClassEntry : entry) {
            //получим и добавим на панель компонент
            JComponent component = getLinkedComponent(stringClassEntry.getKey(), stringClassEntry.getValue());
            add(component);
        }
    }

    public PropertiesPanel(Map<String, Class> fields, LinkerFactory linkerFactory) {
        this.linkerFactory = linkerFactory;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.fields = fields;
        linkers = new HashMap<>();
        //здесь начинаем размещение компонентов
        Set<Map.Entry<String, Class>> entry = fields.entrySet();
        for (Map.Entry<String, Class> stringClassEntry : entry) {
            //получим и добавим на панель компонент
            JComponent component = getLinkedComponent(stringClassEntry.getKey(), stringClassEntry.getValue());
            add(component);
        }
    }

    /**
     * Сопоставление компонента классу
     * Компонент, в котором будет отображаться информация об объектах указанного класса
     *
     * @param key
     * @param clazz
     * @return
     */
    public JComponent getLinkedComponent(String key, Class clazz) {
        Linker linker;
        LinkerFactory linkerFactory = getLinkerFactory();
        linker = linkerFactory.getLinker(clazz);
        linker.setTitle(key);
        linkers.put(key, linker);
        return linker.giveComponent();
    }

    /**
     * Устанавливает возможность изменений значений поля в визуальном компоненте
     */
    public void setUnabled(String key, boolean value) {
        Linker linker = linkers.get(key);
        if (linker != null) {
            linker.setEditableForComponent(value);
        }
    }

    /**
     * Получение экземпляра класса, представленного информацией на панели
     */
    public abstract T getObjectInstance();

    /**
     * Отобразить информацию об объекте на панеле
     *
     * @param obj
     */
    public abstract void showOnPanel(T obj);

    public LinkerFactory getLinkerFactory() {
        return (this.linkerFactory != null) ? this.linkerFactory : new LinkerFactory();
    }
}

package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties;

import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers.*;

import java.util.Date;

/**
 * Фабрика линкеров, связывающих визуальные компоненты и объекты
 */
public class LinkerFactory {

    /**
     * Получить линкер в зависимости от класса, к которому принадлежит отображаемый объект
     *
     * @param clazz
     * @return null, если для указанного класса нет соответствия
     */
    public Linker getLinker(Class clazz) {

        if (clazz == Byte.class) {
            return new LinkerForByte();
        }
        if (clazz == Short.class) {
            return new LinkerForShort();
        }
        if (clazz == Integer.class) {
            return new LinkerForInteger();
        }
        if (clazz == Long.class) {
            return new LinkerForLong();
        }
        if ((clazz == Float.class)) {
            return new LinkerForFloat();
        }
        if ((clazz == Double.class)) {
            return new LinkerForDouble();
        }
        if (clazz == String.class) {
            return new LinkerForString();
        }
        if (clazz == Boolean.class) {
            return new LinkerForBoolean();
        }
        if (clazz == Date.class) {
            return new LinkerForDate();
        }
        if (clazz.isEnum()) {
            return new LinkerForEnum(clazz);
        }

        return null;
    }
}

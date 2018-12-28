package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties;

import javax.swing.*;
import java.awt.*;

/**
 * Интерфейс, посредством которого связывается компонент и объект
 */
public interface Linker<T> {

    //заголовок для компонента
    void setTitle(String text);

    //компонент, в котором отображается объект, определяется только типом класса
    JComponent giveComponent();

    //объект определяется содержанием в компоненте
    T giveObject();

    //компонент принимает себе объект и как-то его отображает
    void takeObject(T object);

    //устанавливает для компонента возможность изменения значения
    void setEditableForComponent(Boolean value);

}

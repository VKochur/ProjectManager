package kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel;

import java.awt.*;
import java.util.List;

/**
 * Объект который может быть отображен в дереве TreePanel
 */
public interface TreeNodeable {

    //получение потомков объекта для отображения в дереве
    List<TreeNodeable> getChilds();

    //получение представления как объект должен отображаться в дереве если не является текущим выбраным узлом
    Component getDefaultViewForTree();

    //получение представления как объект должен отображаться в дереве если является текущим выбраным узлом
    default Component getSelectedViewForTree() {
        return getDefaultViewForTree();
    }
}

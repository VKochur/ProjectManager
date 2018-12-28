package kvv.education.khasang.java2.project_manager.version2;

import kvv.education.khasang.java2.project_manager.model.entity.CrudDAO;
import kvv.education.khasang.java2.project_manager.model.entity.Curator;
import kvv.education.khasang.java2.project_manager.model.entity.Project;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.LinkerFactory;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Фабрика линкеров, используемая для построения панелей отображающий выборки.
 * При построении панелей выборок необходимо отображать критерии выборки. Для этих целей вводится данный класс
 * то как представляются объекты типа Task, Curator зависит от данных в бд, т.е. нужен CrudDAO
 */
public class LinkerFactoryForSelection extends LinkerFactory {

    private final CrudDAO crudDAO;

    public LinkerFactoryForSelection(CrudDAO crudDAO) {
        this.crudDAO = crudDAO;
    }


    public Linker getLinker(Class clazz) {
        Linker linker = super.getLinker(clazz);
        //если не нашли фабрике родителе соответствий класса графическому элементу
        if (linker == null) {

            if (clazz == Project.class || clazz == Task.class) {
                return new LinkerForTask(crudDAO);
            }

            if (clazz == Curator.class) {
                return new LinkerForCurator(crudDAO);
            }

        }
        return null;
    }
}


class LinkerForCurator implements Linker<Curator> {

    private static final String ERROR_SQL = "ошибка получения даных";
    private final CrudDAO crudDAO;
    JPanel panel;
    JComboBox<Curator> comboBox;
    JLabel jLabel;

    public LinkerForCurator(CrudDAO crudDAO) {
        this.crudDAO = crudDAO;
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        comboBox = new JComboBox<>();
        jLabel = new JLabel();
        panel.add(jLabel);
        panel.add(comboBox);
        //заполним комбо
        try {
            //добавим пустую срочку
            comboBox.addItem(null);
            //добавим все задачи из бд
            List<Curator> curatorsList = crudDAO.getAllCurators();
            //осортируем
            Collections.sort(curatorsList, new Comparator<Curator>() {
                @Override
                public int compare(Curator o1, Curator o2) {
                    return o1.getName().compareTo(o2.toString());
                }
            });


            for (Curator curator : curatorsList) {
                comboBox.addItem(curator);
            }


        } catch (SQLException e) {
            //при ошибке получения списка задач, ничего не делаем
            panel.setBorder(BorderFactory.createTitledBorder(ERROR_SQL));
        }
    }


    @Override
    public void setTitle(String text) {
        jLabel.setText(text);
    }

    @Override
    public JComponent giveComponent() {
        return panel;
    }

    @Override
    public Curator giveObject() {
        return (Curator) comboBox.getSelectedItem();
    }

    @Override
    public void takeObject(Curator object) {
        comboBox.setSelectedItem(object);
    }

    @Override
    public void setEditableForComponent(Boolean value) {
        comboBox.setEditable(value);
    }
}

class LinkerForTask implements Linker<Task> {

    private static final String ERROR_SQL = "ошибка получения даных";
    private final CrudDAO crudDAO;
    JPanel panel;
    JComboBox<Task> comboBox;
    JLabel jLabel;

    public LinkerForTask(CrudDAO crudDAO) {
        this.crudDAO = crudDAO;
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        comboBox = new JComboBox<>();
        jLabel = new JLabel();
        panel.add(jLabel);
        panel.add(comboBox);
        //заполним комбо
        try {
            //добавим пустую срочку
            comboBox.addItem(null);
            //добавим все задачи из бд
            List<Task> taskList = crudDAO.getAllTask();
            //осортируем
            Collections.sort(taskList, new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    return o1.getName().compareTo(o2.toString());
                }
            });


            for (Task task : taskList) {
                comboBox.addItem(task);
            }

        } catch (SQLException e) {
            //при ошибке получения списка задач, ничего не делаем
            panel.setBorder(BorderFactory.createTitledBorder(ERROR_SQL));
        }
    }

    @Override
    public void setTitle(String text) {
        jLabel.setText(text);
    }

    @Override
    public JComponent giveComponent() {
        return panel;
    }

    @Override
    public Task giveObject() {
        return (Task) comboBox.getSelectedItem();
    }

    @Override
    public void takeObject(Task object) {
        comboBox.setSelectedItem(object);
    }

    @Override
    public void setEditableForComponent(Boolean value) {
        comboBox.setEditable(value);
    }
}

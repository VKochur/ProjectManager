package kvv.education.khasang.java2.project_manager.version2.view;

import kvv.education.khasang.java2.project_manager.model.Status;
import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.model.entity.CrudDAO;
import kvv.education.khasang.java2.project_manager.model.entity.Task;
import kvv.education.khasang.java2.project_manager.view.composite_views.tree_panel.TreeNodeable;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Узел дерева, для отображения на панеле с деревом
 */
public class TaskForTreePanel implements TreeNodeable {

    private Task task;
    private CrudDAO crudDAO;

    public TaskForTreePanel(Task task, CrudDAO crudDAO) {
        this.task = task;
        this.crudDAO = crudDAO;
    }

    @Override
    public List<TreeNodeable> getChilds() {
        //из чего состоит узел
        List<TreeNodeable> subtasksTreeNodeable = new ArrayList<>();
        List<Task> subtasks;
        try {
            subtasks = crudDAO.getSubTasks(task);
        } catch (SQLException e) {
            //при ошибке получения данных с скл
            return forSQLException();
        }

        //перебираем задачи из бд и оборачиваем их для отображения на панеле с деревом
        for (Task subtask : subtasks) {
            subtasksTreeNodeable.add(new TaskForTreePanel(subtask, crudDAO));
        }
        return subtasksTreeNodeable;
    }

    /**
     * Что выдается в качестве состава листа в случае исключительной ситуации
     *
     * @return
     */
    public static List<TreeNodeable> forSQLException() {
        TreeNodeable error = new TreeNodeable() {
            @Override
            public List<TreeNodeable> getChilds() {
                return new ArrayList<>();
            }

            @Override
            public Component getDefaultViewForTree() {
                JLabel err = new JLabel(Decor.TREE_ERROR_MSG);
                err.setForeground(Decor.TREE_ERR_COLOR);
                return new JLabel(Decor.TREE_ERROR_MSG);
            }
        };

        return Arrays.asList(error);
    }


    public JLabel getLabel() {
        //в отображении задачи нужно учесть что может куратор быть и не указан
        JLabel jLabel = new JLabel(Decor.getShowForTree(task));
        if (task.getStatus() == Status.OPEN) {
            if (task.overdure()) {
                jLabel.setForeground(Decor.OVERDUE_TASK_COLOR);
            } else {
                jLabel.setForeground(Decor.OPEN_TASK_COLOR);
            }
        } else {
            jLabel.setForeground(Decor.CLOSED_TASK_COLOR);
        }
        return jLabel;
    }

    @Override
    public Component getDefaultViewForTree() {
        JPanel panel = new JPanel();
        panel.setBackground(Decor.COLOR_NOT_SELECT);
        panel.add(getLabel());
        return panel;
    }

    @Override
    public Component getSelectedViewForTree() {
        JPanel panel = new JPanel();
        panel.setBackground(Decor.COLOR_SELECT);
        panel.setBorder(BorderFactory.createLineBorder(Decor.BORDER_COLOR));
        panel.add(getLabel());
        return panel;
    }


    public Task getTask() {
        return task;
    }
}


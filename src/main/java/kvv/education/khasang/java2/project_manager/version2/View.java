package kvv.education.khasang.java2.project_manager.version2;

import kvv.education.khasang.java2.project_manager.model.selections.version2.SelectionWrapple;
import kvv.education.khasang.java2.project_manager.version2.view.Decor;
import kvv.education.khasang.java2.project_manager.version2.view.ProjectsTreePanel;
import kvv.education.khasang.java2.project_manager.version2.view.SelectionWrapplePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;


/**
 * View в MVC по задаче "Менеджер проектов"
 */
public class View extends JFrame {


    private final Controller controller;

    //панель дерева проектов
    private ProjectsTreePanel projectsTreePanel;
    private JSplitPane splitPane;
    //панель для выборок
    private JPanel forSelectionsPanel;
    private JComboBox<SelectionWrapple> selectionWrappleCombo;
    private SelectionWrapplePanel selectionWrapplePanel;
    //сообщения об исключениях
    private JLabel exceptStatusLabel;


    public View(Controller controller) {
        setTitle(Decor.MAIN_TITLE);
        setSize(900, 700);
        //свяжем контроллер с вью
        this.controller = controller;
        controller.setView(this);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        try {
            projectsTreePanel = new ProjectsTreePanel(controller);
        } catch (SQLException e) {
            processingExcpn(e);
        }
        splitPane.setLeftComponent(projectsTreePanel);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(4);
        forSelectionsPanel = new JPanel();
        splitPane.setRightComponent(forSelectionsPanel);
        //создать панель для выбора и отображения выборок
        configSelectionPanel();
        exceptStatusLabel = new JLabel();
        exceptStatusLabel.setForeground(Decor.TREE_ERR_COLOR);
        add(splitPane, BorderLayout.CENTER);
        add(exceptStatusLabel, BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public ProjectsTreePanel getProjectsTreePanel() {
        return projectsTreePanel;
    }

    private void processingExcpn(SQLException e) {
        getMsgExcp(e.getMessage());
    }

    /**
     * Создание панели для выбора и отображения выборки
     */
    private void configSelectionPanel() {
        forSelectionsPanel.setLayout(new BorderLayout());
        JPanel cap = new JPanel();
        cap.setLayout(new FlowLayout(FlowLayout.LEFT));
        selectionWrappleCombo = new JComboBox<>();
        selectionWrappleCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.setCurrentSelection((SelectionWrapple) selectionWrappleCombo.getSelectedItem());
            }
        });
        fullSelectionCombo();
        JLabel jLabel = new JLabel(Decor.TITLE_COMBO_SELECTION);
        cap.add(jLabel);
        cap.add(selectionWrappleCombo);
        forSelectionsPanel.add(cap, BorderLayout.NORTH);
    }

    /**
     * Заполнение комбо доступных выборок
     */
    private void fullSelectionCombo() {
        java.util.List<SelectionWrapple> wrapples = controller.getSelectionWrapples();
        for (SelectionWrapple wrapple : wrapples) {
            selectionWrappleCombo.addItem(wrapple);
        }
    }

    /**
     * Изменение панели выборки в соответствии с текущей в контроллере выборкой
     */
    public void changeSelectionPanel() {
        //удалим предыдущую панель выборки
        if (selectionWrapplePanel != null) {
            forSelectionsPanel.remove(selectionWrapplePanel);
        }
        SelectionWrapple currentSelectionWrapple = controller.getCurrentSelection();
        View view = this;
        selectionWrapplePanel = new SelectionWrapplePanel(currentSelectionWrapple, controller.getLinkerFactory()) {
            @Override
            public void sendViewAboutException(SQLException e) {
                view.getMsgExcp(e.getMessage());
            }
        };
        forSelectionsPanel.add(selectionWrapplePanel, BorderLayout.CENTER);
        forSelectionsPanel.updateUI();
    }

    /**
     * Принимает для отображения инфо об исключительной ситуации
     */
    public void getMsgExcp(String message) {
        exceptStatusLabel.setText(message);
    }
}

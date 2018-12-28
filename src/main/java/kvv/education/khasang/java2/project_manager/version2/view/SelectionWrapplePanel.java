package kvv.education.khasang.java2.project_manager.version2.view;

import kvv.education.khasang.java2.project_manager.model.selections.version2.*;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.LinkerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Панель для представления выборки и ее результатов
 */
public class SelectionWrapplePanel extends JPanel {

    private static final int MIN_ROWS_RESULTS = 4;
    private static final int MAX_ROWS_RESULTS = 50;


    //кнопка получения результатов выборки
    private JButton runButton;

    private JPanel panelForResults;
    //Таблица результатов
    private JScrollPane scrollPaneForTableResults;
    private JTable tableResults;

    //фабрика определяющая какие графические компоненты соответствуют различным классам
    private final LinkerFactory linkerFactory;

    //выборка
    SelectionWrapple selectionWrapple;

    //теекущие результаты выборки, отображаемые в таблице
    List<List<String>> resultsAsTable;

    //соответствие условий выборки линкера, которые связывают граф.компанент с значением условия выборки
    Map<String, Linker> linkersForConditions;

    /**
     * При инстанцировании панели учитывается сама выборка и фабрика LinkerFactory, поставляющая линкеры,
     * которые связывают экземпляры объектов и их представления через JComponent
     * <p>
     * Фабрика используется при создании панели уточняющих критериев для выборки
     *
     * @param selectionWrapple
     * @param linkerFactory
     */
    public SelectionWrapplePanel(SelectionWrapple selectionWrapple, LinkerFactory linkerFactory) {
        this.selectionWrapple = selectionWrapple;
        this.linkerFactory = linkerFactory;
        resultsAsTable = new ArrayList<>();
        linkersForConditions = new HashMap<>();
        setBorder(BorderFactory.createTitledBorder(selectionWrapple.getName()));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //если у выборки есть критерии, нужно их отобразить
        if (selectionWrapple.getConditions().size() > 0) {
            configPanelConditions();
        }

        runButton = new JButton(Decor.TITLE_BUTTON);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runSelection();
            }
        });
        runButton.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        add(runButton);
        configPanelResults();

    }

    private void configPanelResults() {
        panelForResults = new JPanel();
        panelForResults.setLayout(new BoxLayout(panelForResults, BoxLayout.Y_AXIS));
        panelForResults.setBorder(BorderFactory.createTitledBorder(Decor.TITLE_PANEL_RESULTS));
        add(panelForResults);
        //поместим изначально пустую таблицу
        updateTableResults(selectionWrapple.getTitles(), new ArrayList());
    }

    /**
     * Получить результат выборки
     */
    private void runSelection() {

        //определить критерии выборки
        definitionConditions();
        //получим результат выборки
        try {
            //получим результат выборки
             resultsAsTable = selectionWrapple.getResultsAsTable();
            //обновим таблицу с результатами

            updateTableResults(selectionWrapple.getTitles(), resultsAsTable);
        } catch (SQLException e) {
            sendViewAboutException(e);
        }
    }

    private void updateTableResults(List titles, List resultsAsTable) {
        //удалим существующую инфо о результатах, если она есть
        if (scrollPaneForTableResults != null) {
            panelForResults.remove(scrollPaneForTableResults);
        }
        //показываем новую инфо о результатах
        tableResults = createTableResults(titles, resultsAsTable);
        //добавляем слушателя
        SelectionWrapplePanel selectionWrapplePanel = this;
        tableResults.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
               selectionWrapplePanel.onClickTable();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        //приводим к нормальному визуальному  виду
        resizeViewTable(tableResults, MIN_ROWS_RESULTS, MAX_ROWS_RESULTS);
        scrollPaneForTableResults = new JScrollPane(tableResults);
        panelForResults.add(scrollPaneForTableResults);
        panelForResults.updateUI();
    }

    /**
     * Для переопределения. Панель используется внутри вью, а как вью отображает исключение - дело вью
     * @param e
     */
    public void sendViewAboutException(SQLException e) {
        System.err.println(e.getMessage());
    }

    private void definitionConditions() {
        //пробежимся по карте соответствий линкеров и названий условий
        Set<Map.Entry<String, Linker>> linkers = linkersForConditions.entrySet();
        for (Map.Entry<String, Linker> linker : linkers) {
            Object valueForCondition = linker.getValue().giveObject();
            selectionWrapple.setValueInCondition(linker.getKey(), valueForCondition);
        }
    }

    private void configPanelConditions() {
        JPanel conditionsPanel = new JPanel();
        conditionsPanel.setLayout(new BorderLayout());
        JPanel inScroolPane = new JPanel();
        JScrollPane jScrollPane = new JScrollPane(inScroolPane);
        conditionsPanel.add(jScrollPane);
        final int MAX_HEIGHT = 400;
        conditionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, MAX_HEIGHT));
        inScroolPane.setLayout(new BoxLayout(inScroolPane, BoxLayout.Y_AXIS));
        inScroolPane.setMinimumSize(new Dimension(500,500));
        conditionsPanel.setBorder(BorderFactory.createTitledBorder(Decor.TITLE_PANEL_CONDITIONS));
        add(conditionsPanel);

        //перебираем критерии выборки
        Map<String, Class> conditionsMetaInfo = selectionWrapple.getMetaInfoAboutConditions();
        Set<Map.Entry<String, Class>> setConditions = conditionsMetaInfo.entrySet();
        for (Map.Entry<String, Class> setCondition : setConditions) {
            Linker linker = linkerFactory.getLinker(setCondition.getValue());
            //если фабрика линкеров не знает линкер для класса, которым представлено одно из условий, то throws IAE, нужно использовать другую фабрику
            if (linker == null) {
                throw new IllegalArgumentException("Панель для выборки не построена. Фабрика " + linkerFactory + " для " + setCondition.getValue() + " не выдала линкер, определяющий графический элемент для" +
                        " критерия выборки: " + setCondition.getKey() + ". Дайте в конструктор панели фабрику знающую линкер для указанного класса");
            }
            //установим заголовок у компонента, с которым связан линкер
            linker.setTitle(setCondition.getKey());
            linkersForConditions.put(setCondition.getKey(), linker);
            //добавим на панель отображающих критерии графич. компонент соответствующий текущий критерий
            JComponent component = linker.giveComponent();
            inScroolPane.add(component);
        }


    }

    /**
     * Получает объект связанный с выделенной в таблице строкой
     * Если нет выделеной в таблице строки, выдает null
     */
    public Object getObjectAffectedCurrentRow() {
        int number = tableResults.getSelectedRow();
        if (number > -1) {
            try {
                 return SelectionWrapple.getObjectAffectedRowInTable(number, resultsAsTable, selectionWrapple);
            } catch (SQLException e) {
                sendViewAboutException(e);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Действия, выполняемые при выделении ячеек на таблице
     */
    public void onClickTable(){
        System.out.println(getObjectAffectedCurrentRow());
    }

    /**
     * Создает таблицу результатов выборки
     *
     * @param titles  название столбцов таблицы
     * @param context содержание таблицы
     * @return
     */
    private JTable createTableResults(List<String> titles, List<List<String>> context) {
        Object[] titlesColumn;
        titlesColumn = titles.toArray();
        Object[][] data = new Object[context.size()][titles.size()];
        int numberRow = 0;
        for (List<String> strings : context) {
            data[numberRow] = strings.toArray();
            numberRow++;
        }
        JTable tableResults = new JTable(data, titlesColumn);
        return tableResults;
    }

    /**
     * Конфигурирование отображения таблицы для JScroolPane
     *
     * @param table
     * @param minRows минимальное количество строк таблицы для отображения
     * @param maxRows максимальное количество строк таблицы для отображения
     */
    private void resizeViewTable(JTable table, int minRows, int maxRows) {
        int needRows = (table.getRowCount() > maxRows) ? maxRows : (table.getRowCount() < minRows) ? minRows : table.getRowCount();
        table.setPreferredScrollableViewportSize(new Dimension(table.getPreferredScrollableViewportSize().width, table.getRowHeight() * needRows));
    }

}

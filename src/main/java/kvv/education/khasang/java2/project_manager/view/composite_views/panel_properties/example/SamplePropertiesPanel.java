package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.example;

import kvv.education.khasang.java2.project_manager.model.Util;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;
import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.PropertiesPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Пример использования панели отображения свойств объектов
 *
 * Демонстрирует отображение информации о 2х instanceof Human, а также создание instanceof Human в соответ. с введеной на панель инфомацией
 */
public class SamplePropertiesPanel extends JFrame {

    JButton button1;
    JButton button2;
    JButton button3;
    JCheckBox checkBox;
    Human human1;
    Human human2;
    PropertiesPanel<Human> propertiesPanel;
    boolean editableField;

    public SamplePropertiesPanel() throws HeadlessException{
        setTitle("Пример использования панели свойств");
        //сохраняется порядок занесения
        setMinimumSize(new Dimension(300, 300));

        //на панеле будет отображаться указанные поля, и их тип также указан
        Map<String, Class> fields = new LinkedHashMap<>();
        fields.put("Фамилия", String.class);
        fields.put("Имя", String.class);
        fields.put("Пол", Human.SEX.class);
        fields.put("Дата рождения", Date.class);
        fields.put("Вес", Float.class);
        fields.put("Состоит в браке", Boolean.class);

        //инициализируем панель на основе карты соответствий имя поля - класс в нем содержащийся
        propertiesPanel = initPanel(fields);
        initHumansForExample();
        JScrollPane scrollPane = new JScrollPane(propertiesPanel);
        add(scrollPane, BorderLayout.CENTER);
        JPanel temp = new JPanel();
        add(temp, BorderLayout.NORTH);

        button1 = new JButton("1й");
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                propertiesPanel.showOnPanel(human1);
            }
        });
        temp.add(button1);


        button2 = new JButton("2й");
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                propertiesPanel.showOnPanel(human2);
            }
        });
        temp.add(button2);

        button3 = new JButton("Сгенерировать");
        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Human human = propertiesPanel.getObjectInstance();
                human.print();
            }
        });
        temp.add(button3);

        checkBox = new JCheckBox("Запретить изменение полей");
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                propertiesPanel.setUnabled("Фамилия", !(checkBox.isSelected()));
                propertiesPanel.setUnabled("Имя", !(checkBox.isSelected()));
                propertiesPanel.setUnabled("Пол", !(checkBox.isSelected()));
                propertiesPanel.setUnabled("Дата рождения", !(checkBox.isSelected()));
                propertiesPanel.setUnabled("Вес", !(checkBox.isSelected()));
                propertiesPanel.setUnabled("Состоит в браке", !(checkBox.isSelected()));
                propertiesPanel.setUnabled("такоко поля нет, должно просто проигнорить", !(checkBox.isSelected()));
            }
        });
        temp.add(checkBox);


        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initHumansForExample() {
        human1 = new BuilderHuman("Иванов").setName("Иван").setDateOfBorn(Util.getDateByString("07.07.2007").getTime()).setGender(Human.SEX.MAN).
                setMarried(true).setWeight(0f).getHuman();
        human2 = new BuilderHuman("Петров").getHuman();
    }

    private PropertiesPanel<Human> initPanel(Map<String, Class> fields) {
        PropertiesPanel<Human> propertiesPanel =  new PropertiesPanel<Human>(fields) {

            /**
             * Получение объекта по введенной информации
             * @return
             */
            @Override
            public Human getObjectInstance() {
                String surname = (String) linkers.get("Фамилия").giveObject();
                String name = (String)linkers.get("Имя").giveObject();
                Human.SEX gender = (Human.SEX) linkers.get("Пол").giveObject();
                Date dateOfBorn = (Date) linkers.get("Дата рождения").giveObject();
                Float weight = (Float) linkers.get("Вес").giveObject();
                Boolean married = (Boolean) linkers.get("Состоит в браке").giveObject();

                Human human = new BuilderHuman(surname).setName(name).setGender(gender).setDateOfBorn(dateOfBorn).setWeight(weight).setMarried(married).getHuman();
                return human;
            }

            /**
             * Отображение объекта на панеле
             * @param obj
             */
            @Override
            public void showOnPanel(Human obj) {
                Linker linker = linkers.get("Фамилия");
                linker.takeObject(obj.surname);
                linker = linkers.get("Имя");
                linker.takeObject(obj.name);
                linker = linkers.get("Пол");
                linker.takeObject(obj.gender);
                linker = linkers.get("Дата рождения");
                linker.takeObject(obj.dateOfBorn);
                linker = linkers.get("Вес");
                linker.takeObject(obj.weigth);
                linker = linkers.get("Состоит в браке");
                linker.takeObject(obj.married);
                updateUI();
            }
        };
        return propertiesPanel;
    }

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {

        final SamplePropertiesPanel[] samplePropertiesPanel = new SamplePropertiesPanel[1];

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                    samplePropertiesPanel[0] = new SamplePropertiesPanel();
            }
        });
    }
}

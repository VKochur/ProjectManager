package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.linkers;

import kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.Linker;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class LinkerForDate implements Linker<Date> {
    JDatePickerImpl datePicker;
    JPanel panel;
    JLabel label;
    JButton buttonFromPicker;

    public LinkerForDate() {
        UtilDateModel utilDateModel = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(utilDateModel);
        datePicker = new JDatePickerImpl(datePanel);
        datePicker.getJFormattedTextField().setBackground(new Color(255, 255, 255));
        //определим кнопку датаПикера
        int count = datePicker.getComponentCount();
        for (int i = 0; i < count; i++) {
            Component component = datePicker.getComponent(i);
            if (component instanceof JButton) {
                buttonFromPicker = (JButton) component;
            }
        }

        panel = new JPanel();
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(0x6E697C)));
        label = new JLabel();
        panel.add(label, BorderLayout.WEST);
        panel.add(datePicker, BorderLayout.CENTER);
    }

    @Override
    public void setTitle(String text) {
        label.setText(text + ":  ");
    }

    @Override
    public JComponent giveComponent() {
        return panel;
    }

    @Override
    public Date giveObject() {
        return (Date) datePicker.getModel().getValue();
    }

    @Override
    public void takeObject(Date object) {
        Calendar calendar = new GregorianCalendar();
        if (object == null) {
            datePicker.getModel().setSelected(false);
        } else {
            calendar.setTime(object);
            datePicker.getModel().setDay(calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.getModel().setMonth(calendar.get(Calendar.MONTH));
            datePicker.getModel().setYear(calendar.get(Calendar.YEAR));
            datePicker.getModel().setSelected(true);
        }
    }

    @Override
    public void setEditableForComponent(Boolean value) {
        //прячем кнопку выбора даты при необходимости
        buttonFromPicker.setVisible(value);
        if (value) {
            datePicker.getJFormattedTextField().setBackground(new Color(255, 255, 255));
        } else {
            datePicker.getJFormattedTextField().setBackground(new Color(235, 235, 235));
        }
    }
}

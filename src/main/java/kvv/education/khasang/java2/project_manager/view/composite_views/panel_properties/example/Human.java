package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.example;

import java.util.Date;

/**
 * Человек
 */
public class Human {
    String name;
    String surname;
    Date dateOfBorn;

    SEX gender;
    Boolean married;

    Float weigth;

    enum SEX {
        MAN,
        WOMAN
    }


    public void print() {
        System.out.println("----------------------");
        System.out.println("фамилия: " + surname);
        System.out.println("имя: " + name);
        System.out.println("рождение " + dateOfBorn);
        System.out.println("пол " + gender);
        System.out.println("состоит в браке: " + married);
        System.out.println("вес " + weigth);
        System.out.println("----------------------");
    }
}


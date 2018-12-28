package kvv.education.khasang.java2.project_manager.view.composite_views.panel_properties.example;

import java.util.Date;

/**
 * Строитель человека
 */
public class BuilderHuman {

    String name;
    String surname;
    Date dateOfBorn;

    Human.SEX gender;
    Boolean married;

    Float weight;

    public BuilderHuman(String surname) {
        this.surname = surname;
    }

    public BuilderHuman setName(String name) {
        this.name = name;
        return this;
    }

    public BuilderHuman setDateOfBorn(Date dateOfBorn) {
        this.dateOfBorn = dateOfBorn;
        return this;
    }

    public BuilderHuman setGender(Human.SEX gender) {
        this.gender = gender;
        return this;
    }

    public BuilderHuman setMarried(Boolean married) {
        this.married = married;
        return this;
    }

    public BuilderHuman setWeight(Float weight) {
        this.weight = weight;
        return this;
    }

    public Human getHuman() {
        Human human = new Human();
        human.surname = surname;
        human.name = name;
        human.dateOfBorn = dateOfBorn;
        human.gender = gender;
        human.married = married;
        human.weigth = weight;
        return human;
    }


}

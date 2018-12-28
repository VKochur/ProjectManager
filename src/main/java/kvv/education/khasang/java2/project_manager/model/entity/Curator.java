package kvv.education.khasang.java2.project_manager.model.entity;

/**
 * Куратор задачи (POJO)
 */
public class Curator {

    private int id;
    private String name;
    private String contact;

    public Curator() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Curator)) return false;

        Curator curator = (Curator) o;

        if (id != curator.id) return false;
        if (getName() != null ? !getName().equals(curator.getName()) : curator.getName() != null) return false;
        return getContact() != null ? getContact().equals(curator.getContact()) : curator.getContact() == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getContact() != null ? getContact().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name + " ( " + contact +" )";
    }
}

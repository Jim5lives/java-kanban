public class Task {
    String name;
    String description;
    int id;
    Progress status;

    public Task(String name, String description) { // конструктор без id
        //int id;
        this.description = description;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Task {" +
                "name= '" + name + '\'' +
                ", description= '" + description + '\'' +
                ", ID = " + id +
                ", STATUS= " + status +
                '}';
    }
}

public class SubTask extends Task {

    Integer epicId;

    public SubTask(String name, String description,Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }
}

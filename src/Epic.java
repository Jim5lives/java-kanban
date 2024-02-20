import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> subTasksArray = new ArrayList<>();

    public Epic(String name, String description) { // конструктор без id
        super(name, description);
    }

    // метод для привязки сабтаска к эпику (добавляем id сабтаска в массив эпика)
    public void linkSubTaskToEpic(int epicId) {
        subTasksArray.add(epicId);
    }
    // метод для отвязки сабтаска от эпика (убираем id сабтаска из массива эпика)
    public void removeSubTaskFromEpic(int epicId) {
        subTasksArray.remove(epicId);
    }
    // удаляем все сабтаски из эпика
    public void clearAllSubTaskFromEpic() {
        subTasksArray.clear();
    }

    public ArrayList<Integer> getSubtasksList(int epicId) {
        return subTasksArray;
    }

}

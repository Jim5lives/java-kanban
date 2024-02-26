package com.yandex.app.model;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTasksArray = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    // getter
    public ArrayList<Integer> getSubTasksArray() {
        return subTasksArray;
    }

    // setter
    public void setSubTasksArray(ArrayList<Integer> subTasksArray) {
        this.subTasksArray = subTasksArray;
    }

    // метод для привязки сабтаска к эпику (добавляем id сабтаска в массив эпика)
    public void linkSubTaskToEpic(int epicId) {
        subTasksArray.add(epicId);
    }
    // метод для отвязки сабтаска из массива эпика
    public void removeSubTaskFromEpic(Integer epicId) {
        subTasksArray.remove(epicId);
    }
    // удаляем все сабтаски из эпика
    public void clearAllSubTaskFromEpic() {
        subTasksArray.clear();
    }
    public ArrayList<Integer> getSubtasksList() {
        return subTasksArray;
    }

}

package com.yandex.app.model;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasksArray = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    // getter
    public List<Integer> getSubTasksArray() {
        return subTasksArray;
    }

    // setter
    public void setSubTasksArray(List<Integer> subTasksArray) {
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

    public List<Integer> getSubtasksList() {
        return subTasksArray;
    }

}

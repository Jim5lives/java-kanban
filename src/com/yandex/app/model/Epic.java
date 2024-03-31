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

    // метод для привязки сабтаска к эпику (добавляем id сабтаска в массив эпика)
    public void linkSubTaskToEpic(int epicId) {
        subTasksArray.add(epicId);
    }

    // метод для отвязки сабтаска из массива эпика
    public void removeSubTaskFromEpic(Integer subTaskId) {
        subTasksArray.remove(subTaskId);
    }

    // удаляем все сабтаски из эпика
    public void clearAllSubTaskFromEpic() {
        subTasksArray.clear();
    }

}

package com.yandex.app.model;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTasksArray = new ArrayList<>();
    private Instant endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, Progress status) {
        super(name, description, id, status);
    }

    // getter subTasksArray
    public List<Integer> getSubTasksArray() {
        return subTasksArray;
    }

    //setter для endTime
    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    //getter для endTime
    @Override
    public Instant getEndTime() {
        return endTime;
    }

    // метод для привязки сабтаска к эпику
    public void linkSubTaskToEpic(int epicId) {
        subTasksArray.add(epicId);
    }

    // метод для отвязки сабтаска от эпика
    public void removeSubTaskFromEpic(Integer subTaskId) {
        subTasksArray.remove(subTaskId);
    }

    // удаляем все сабтаски из эпика
    public void clearAllSubTaskFromEpic() {
        subTasksArray.clear();
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        if (getDuration() == null || getStartTime() == null) {
             return  getId() + "," + getType() + ","
                    + getName() + "," + getStatus() + "," + getDescription();
        } else {
            return getId() + "," + getType() + ","
                    + getName() + "," + getStatus() + "," + getDescription() + "," + timeToString(getStartTime()) + ","
                    + getDuration().toMinutes();
        }
    }

}

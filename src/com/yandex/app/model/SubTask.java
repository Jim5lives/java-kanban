package com.yandex.app.model;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description,Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int id, Progress status, Integer epicId) {
        super(name, description, id,status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return  getId() + "," + getType() + ","
                + getName() + "," + getStatus() + "," + getDescription() + "," + getEpicId();
    }
}

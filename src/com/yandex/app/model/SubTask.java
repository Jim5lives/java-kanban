package com.yandex.app.model;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description,Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}

package com.yandex.app.service;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager(getDefaultHistoryManager());
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();

    }

    public static TaskManager getFileBackedTaskManager() {
        return new FileBackedTaskManager(getDefaultHistoryManager());
    }
}

package com.yandex.app.service;

public class Managers {

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager(getDefaultHistoryManager());
    }
    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();

    }
}

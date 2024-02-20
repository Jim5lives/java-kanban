import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HashMap<Integer, Task> epics = new HashMap<>();
    static private int id = 0;

    public static int generateId() { // генератор id
        return id++;
    }

    //Методы для каждого из типа задач(Задача/Эпик/Подзадача):
    // TASK
    //a. Получение списка всех задач.
    public void printAllTasks() {
        for (Integer id : tasks.keySet()) {
            System.out.println(tasks.get(id).toString());
        }
    }

    //b. Удаление всех задач.
    public void deleteAllTasks() {
        tasks.clear();
    }

    //c. Получение по идентификатору.
    public Task getTask(int id) {
        return tasks.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public Task addTask(Task newTask) {
        newTask.id = generateId();
        newTask.status = Progress.NEW;
        tasks.put(newTask.id, newTask);
        return newTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Task updateTask(Task updatedTask) {
        Task currentTask = tasks.get(updatedTask.id);
        tasks.put(updatedTask.id, currentTask);
        return updatedTask;
    }

    //f. Удаление по идентификатору.
    public Task deleteTask(int id) {
        Task task = tasks.get(id);
        tasks.remove(id);
        return task;
    }

    // Обновление статуса Task.
    public Task setTaskStatus(Task task, Progress status) {
        switch (status) {
            case NEW:
                task.status = Progress.NEW;
                break;
            case IN_PROGRESS:
                task.status = Progress.IN_PROGRESS;
                break;
            case DONE:
                task.status = Progress.DONE;
                break;
        }
        return task;
    }
//----------------------------------------------------------------------------------------------------------------------

    // EPIC
    //a. Получение списка всех задач.
    public ArrayList<Task> printAllEpics() {
        ArrayList<Task> allEpicsList = new ArrayList<>();
        for (Integer id : epics.keySet()) {
            allEpicsList.add(epics.get(id));
        }
        return allEpicsList;
    }

    //b. Удаление всех задач.
    public void deleteAllEpics() {
        epics.clear();
        // Epic.subTasksArray.clear();
    }

    //c. Получение по идентификатору.
    public Task getEpic(int id) {
        return epics.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public Task addEpic(Task newEpic) {
        newEpic.id = generateId();
        epics.put(newEpic.id, newEpic);
        newEpic.status = Progress.NEW;
        return newEpic;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public Task updateEpic(Task updatedEpic) {
        Task currentTask = tasks.get(updatedEpic.id);
        tasks.put(updatedEpic.id, currentTask);
        return updatedEpic;
    }

    //f. Удаление по идентификатору.
    public Task deleteEpic(int id) {
        Task epic = epics.get(id);
        epics.remove(id);
        return epic;
    }

    // Обновление статуса epic.
    public Epic setEpicStatus(Epic epic) {
        int subTasksDone = 0;
        int subTasksInProgress = 0;
        int subtasksNew = 0;
        for (SubTask subTask : getSubtasksFromEpic(epic.id)) {
            if (subTask.status == Progress.DONE) {
                subTasksDone++;
            } else if (subTask.status == Progress.IN_PROGRESS) {
                subTasksInProgress++;
            } else {
                subtasksNew++;
            }
            if (epic.subTasksArray == null || subtasksNew == epic.subTasksArray.size()) {
                epic.status = Progress.NEW;
            } else if (subTasksDone == epic.subTasksArray.size()) {
                epic.status = Progress.DONE;
            } else {
                epic.status = Progress.IN_PROGRESS;
            }
        }
        return epic;
    }


//----------------------------------------------------------------------------------------------------------------------

    // SUBTASKS
    //a. Получение списка всех задач.
    public void printAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            System.out.println(subTasks.get(id).toString());
        }
    }

    //b. Удаление всех задач.
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Integer id : epics.keySet()) {
            Epic currentTask = (Epic) epics.get(id);
            currentTask.clearAllSubTaskFromEpic();
        }

    }

    //c. Получение по идентификатору.
    public SubTask getSubtask(int id) {
        return subTasks.get(id);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public SubTask addSubTask(SubTask newSubTask) {
        newSubTask.id = generateId();
        Epic linkedEpic = (Epic) epics.get(newSubTask.epicId);
        linkedEpic.linkSubTaskToEpic(newSubTask.id);
        newSubTask.status = Progress.NEW;
        subTasks.put(newSubTask.id, newSubTask);
        return newSubTask;
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public SubTask updateSubTask(SubTask updatedSubTask) {
        Task currentTask = tasks.get(updatedSubTask.id);
        tasks.put(updatedSubTask.id, currentTask);
        return updatedSubTask;
    }

    //f. Удаление по идентификатору.
    public SubTask deleteSubtask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic linkedEpic = (Epic) epics.get(subTask.epicId);
        linkedEpic.removeSubTaskFromEpic(subTask.id);
        subTasks.remove(id);
        return subTask;
    }

    // Обновление статуса subTask.
    public Task setSubTaskStatus(SubTask subTask, Progress status) {
        switch (status) {
            case NEW:
                subTask.status = Progress.NEW;
                break;
            case IN_PROGRESS:
                subTask.status = Progress.IN_PROGRESS;
                break;
            case DONE:
                subTask.status = Progress.DONE;
                break;
        }
        return subTask;
    }

//----------------------------------------------------------------------------------------------------------------------
//    Дополнительные методы:
//    a. Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getSubtasksFromEpic(int id) {
        Epic selectedEpic = (Epic) epics.get(id);
        ArrayList<Integer> subTasksIdsArray = selectedEpic.getSubtasksList(selectedEpic.id);
        ArrayList<SubTask> subTasksInSelectedEpic = new ArrayList<>();
        for (Integer subTaskNumber : subTasksIdsArray) {
            subTasksInSelectedEpic.add(subTasks.get(subTaskNumber));
        }
        return subTasksInSelectedEpic;
    }
}
package ru.kartsev.dmitry.todolist;

public class TaskItem {

    private String title;
    private String description;
    private boolean active = false;
    private int idInTable;
    private int inCharge;
    private int taskType = 0;

    public TaskItem(String title, String description, boolean active, int idInTable, int inCharge, int type) {
        this.title = title;
        this.description = description;
        this.active = active;
        this.idInTable = idInTable;
        this.inCharge = inCharge;
        this.taskType = type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getIdInTable() {
        return idInTable;
    }

    public void setIdInTable(int i) {
        this.idInTable = i;
    }

    public int getInCharge() {
        return inCharge;
    }

    public void setInCharge(int inCharge) {
        this.inCharge = inCharge;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }
}

package ru.kartsev.dmitry.todolist;

/**
 * Created by Jag on 16.12.2016.
 */

public class TaskItem {

    private String title;
    private String description;
    boolean active = false;
    int idInTable;
    int inCharge;

    public TaskItem(String title, String description, boolean active, int idInTable, int inCharge) {
        this.title = title;
        this.description = description;
        this.active = active;
        this.idInTable = idInTable;
        this.inCharge = inCharge;
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
}

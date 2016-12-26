package ru.kartsev.dmitry.todolist;

/**
 * Created by Jag on 24.12.2016.
 */

public class UsersCards {
    private String name;
    private boolean active = false;
    private int idInTable;

    public UsersCards(String name, boolean active, int idInTable) {
        this.name = name;
        this.active = active;
        this.idInTable = idInTable;
    }

    public String getName() {
        return name;
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
}

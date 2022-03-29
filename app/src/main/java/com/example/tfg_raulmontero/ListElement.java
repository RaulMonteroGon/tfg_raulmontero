package com.example.tfg_raulmontero;

import java.io.Serializable;

public class ListElement implements Serializable {
    public String color;
    public String name;
    public String description;
    public String number;
    public String idgroup;


    public ListElement(String color, String name, String description, String number, String idgroup) {
        this.color = color;
        this.name = name;
        this.description = description;
        this.number = number;
        this.idgroup = idgroup;
    }

    public String getIdgroup() {
        return idgroup;
    }

    public void setIdgroup(String idgroup) {
        this.idgroup = idgroup;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}

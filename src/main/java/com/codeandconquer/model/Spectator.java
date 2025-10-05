package com.codeandconquer.model;

public class Spectator {
    private String name;

    public Spectator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Spectator{name='" + name + "'}";
    }

}

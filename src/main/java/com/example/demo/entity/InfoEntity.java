package com.example.demo.entity;


public class InfoEntity {
    private String name;

    private String password;

    private String description;

    public String getName() {
        return name;
    }

    public InfoEntity withName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public InfoEntity withPassword(String password) {
        this.password = password;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public InfoEntity withDescription(String description) {
        this.description = description;
        return this;
    }
}

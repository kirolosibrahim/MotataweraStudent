package com.kmk.motatawera.student.model;

public class SubjectModel {

    private int branch;
    private int department;
    private int grad;
    private String id;
    private String name;

    public SubjectModel() {
    }

    public SubjectModel(int branch, int department, int grad, String id, String name) {
        this.branch = branch;
        this.department = department;
        this.grad = grad;
        this.id = id;
        this.name = name;
    }

    public int getBranch() {
        return branch;
    }

    public int getDepartment() {
        return department;
    }

    public int getGrad() {
        return grad;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

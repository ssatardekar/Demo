package org.example;

public class Employee implements AutoCloseable {

    private String name;

    private long id;

    public Employee(String name, long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void close(){
        this.name = null;
        this.id = -1;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + "'" +
                ", id=" + id +
                '}';
    }
}

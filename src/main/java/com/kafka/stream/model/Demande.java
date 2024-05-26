package com.kafka.stream.model;

public class Demande {
    private Long id;
    private String name;
    private String firstName;
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }    
}

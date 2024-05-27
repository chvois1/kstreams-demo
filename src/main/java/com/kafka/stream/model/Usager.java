package com.kafka.stream.model;

public class Usager {

    private Long id;
    private String email;

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String name) {
        this.email = name;
    }
}

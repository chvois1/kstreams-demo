package com.kafka.stream.model;

public class Usager {

    private Long id;
    private String eMail;

    public String getEMail() {
        return eMail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEMail(String name) {
        this.eMail = name;
    }
}

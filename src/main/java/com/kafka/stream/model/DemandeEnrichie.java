package com.kafka.stream.model;

public class DemandeEnrichie {

    private Long id;
    private String name;
    private String firstName;
    private String email;
 
    public DemandeEnrichie(Long id, String name, String firstName, String email) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

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
    public void setEmail(String email) {
        this.email = email;
    }
}

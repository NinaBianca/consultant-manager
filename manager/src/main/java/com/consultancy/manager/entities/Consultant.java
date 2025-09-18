package com.consultancy.manager.entities;

import com.consultancy.manager.enums.ConsultantType;
import jakarta.persistence.*;

@Entity
@Table(name="CONSULTANTS")
public class Consultant {

    @Id
    @GeneratedValue()
    private Integer id;

    @Column(name="NAME")
    private String name;

    @Column(name="EMAIL")
    private String email;

    @Column(name="AVAILABLE")
    private Boolean available;

    @Column(name="CONSULTANT_TYPE")
    @Enumerated(EnumType.STRING)
    private ConsultantType consultantType;

    @Column(name="LOCATION")
    private String location;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAvailable() {
        return this.available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public ConsultantType getConsultantType() {
        return this.consultantType;
    }

    public void setConsultantType(ConsultantType consultantType) {
        this.consultantType = consultantType;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

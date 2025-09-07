package com.cbfacademy.horoscopeapi;

import jakarta.persistence.*;

@Entity
@Table(name = "zodiac_sign")
public class ZodiacSign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    private String element;
    private String modality;

    @Column(name = "ruling_planet")
    private String rulingPlanet;

    private String traits;

    public ZodiacSign() {
    }

    public ZodiacSign(String name, String element, String modality, String rulingPlanet, String traits) {
        this.name = name;
        this.element = element;
        this.modality = modality;
        this.rulingPlanet = rulingPlanet;
        this.traits = traits;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getRulingPlanet() {
        return rulingPlanet;
    }

    public void setRulingPlanet(String rulingPlanet) {
        this.rulingPlanet = rulingPlanet;
    }

    public String getTraits() {
        return traits;
    }

    public void setTraits(String traits) {
        this.traits = traits;
    }
}
package br.com.aonsistemas.appvet.model;

import java.io.Serializable;

public class Product implements Serializable {

    private int id;
    private String description;
    private String note;
    private String unity;
    private Double value;

    public Product() {
        this.id = 0;
        this.description = "";
        this.note = "";
        this.unity = "";
        this.value = 0.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUnity() {
        return unity;
    }

    public void setUnity(String unity) {
        this.unity = unity;
    }

    public Double getValor() {
        return value;
    }

    public void setValor(Double value) {
        this.value = value;
    }

}

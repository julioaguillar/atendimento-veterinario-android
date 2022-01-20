package br.com.aonsistemas.appvet.model;

import java.io.Serializable;

public class Client implements Serializable {

    private int id;
    private String name;
    private String address;
    private String telephone;
    private String email;

    public Client() {
        this.id = 0;
        this.name = "";
        this.address = "";
        this.telephone = "";
        this.email = "";
    }

    public Client(int id, String name, String address, String telephone, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}

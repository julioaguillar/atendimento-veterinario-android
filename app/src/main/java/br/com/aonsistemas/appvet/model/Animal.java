package br.com.aonsistemas.appvet.model;

import java.io.Serializable;

public class Animal implements Serializable {

    private int id;
    private int client_id;
    private String name;
    private String birth;
    private Client client;

    public Animal() {
        this.id = 0;
        this.client_id = 0;
        this.name = "";
        this.birth = "";
        this.client = new Client();
    }

    public Animal(int id, int client_id, String name, String birth, Client client) {
        this.id = id;
        this.client_id = client_id;
        this.name = name;
        this.birth = birth;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}

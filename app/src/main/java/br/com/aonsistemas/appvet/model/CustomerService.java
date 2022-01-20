package br.com.aonsistemas.appvet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomerService implements Serializable {

    private int id;
    private int id_client;
    private int id_animal;
    private String data;
    private String note;
    private Double total;
    private Client client;
    private Animal animal;
    private List<CustomerServiceItem> items;

    public CustomerService() {
        this.id = 0;
        this.id_client = 0;
        this.id_animal = 0;
        this.data = "";
        this.note = "";
        this.total = 0.0;
        this.client = new Client();
        this.animal = new Animal();
        this.items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public int getId_animal() {
        return id_animal;
    }

    public void setId_animal(int id_animal) {
        this.id_animal = id_animal;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public List<CustomerServiceItem> getItems() {
        return items;
    }

    public void setItems(List<CustomerServiceItem> items) {
        this.items = items;
    }
}

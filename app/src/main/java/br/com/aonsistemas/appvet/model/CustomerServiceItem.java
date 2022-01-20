package br.com.aonsistemas.appvet.model;

import java.io.Serializable;

public class CustomerServiceItem implements Serializable {

    private int id;
    private int id_customer_service;
    private int id_product;
    private Double amount;
    private Double value;
    private Double total_value;
    private Product product;

    public CustomerServiceItem() {
        this.id = 0;
        this.id_customer_service = 0;
        this.id_product = 0;
        this.amount = 0.0;
        this.value = 0.0;
        this.total_value = 0.0;
        this.product = new Product();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdCustomerService(int id_customer_service) {
        this.id_customer_service = id_customer_service;
    }

    public int getIdProduct() {
        return id_product;
    }

    public void setIdProduct(int id_product) {
        this.id_product = id_product;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getTotalValue() {
        return total_value;
    }

    public void setTotalValue(Double total_value) {
        this.total_value = total_value;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}

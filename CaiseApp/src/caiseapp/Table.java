/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caiseapp;

/**
 *
 * @author macbookpro
 */
public class Table {
    String itemName,createdAt;
    int quantity;
    double price,amount;

    public Table(String itemName,String createdAt, int quantity, double price, double amount) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
        this.createdAt=createdAt;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    
}

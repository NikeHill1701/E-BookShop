package mypkg;

import java.util.*;

public class CartItem {
    private int id;
    private String title;
    private String author;
    private Double price;
    private int qtyOrdered;

    public void CartItem() {
        this.id = 0;
        this.title = "Default Title";
        this.author = "Default Author";
        this.price = 0.0;
        this.qtyOrdered = 0;
    }

    public CartItem(int id, String title, String author, Double price, int qtyOrdered) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.qtyOrdered = qtyOrdered;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public Double getPrice() {
        return this.price;
    }

    public int getQtyOrdered() {
        return this.qtyOrdered;
    }

    public void setQtyOrdered(int qtyOrdered) {
        this.qtyOrdered = qtyOrdered;
    }

}
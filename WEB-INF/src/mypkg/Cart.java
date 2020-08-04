package mypkg;

import java.util.*;

public class Cart { 
    private CartItem cItem;
    private HashMap<Integer,CartItem> cart;
    private int totalNumberOfBooks;
    private Double totalPrice;

    public Cart() {
        cart = new HashMap<Integer,CartItem>();
        totalPrice = 0.0;
        totalNumberOfBooks = 0;
    }

    public void add(CartItem cartItem) {
        int cartItemId = cartItem.getId();
        int qtyOrderedNew = cartItem.getQtyOrdered();
        CartItem cartItemOld = cart.get(cartItemId);

        if (cartItemOld != null) {
            cartItem.setQtyOrdered(cartItemOld.getQtyOrdered() + qtyOrderedNew);
        }
        
        cart.put(cartItemId, cartItem);
        totalPrice += qtyOrderedNew * cartItem.getPrice();
        totalNumberOfBooks += qtyOrderedNew;
    }

    public void update(int id, int newQty) {
        CartItem cartItem = cart.get(id);
        totalNumberOfBooks += newQty - cartItem.getQtyOrdered();
        totalPrice += (newQty - cartItem.getQtyOrdered()) * cartItem.getPrice();
        cartItem.setQtyOrdered(newQty);
        cart.put(id,cartItem);
    }

    public void remove(int id) {
        CartItem cartItem = cart.get(id);
        if (cartItem != null) {
            totalNumberOfBooks -= cartItem.getQtyOrdered();
            totalPrice -= cartItem.getQtyOrdered() * cartItem.getPrice();
            cart.remove(id);
        }
    }

    public boolean isEmpty() {
        if (cart.size() == 0)
            return true;
        else    
            return false;
    }

    public int size() {
        return cart.size();
    }

    public ArrayList<CartItem> getItems() {
        ArrayList<CartItem> itemList = new ArrayList<CartItem>();
        Set<Integer> cartKeySet = cart.keySet();
        for(int id : cartKeySet) {
            itemList.add(cart.get(id));
        }
        return itemList;
    }

    public Double getTotalPrice() {
        return this.totalPrice;
    }

    public int getTotalNumberOfBooks() {
        return totalNumberOfBooks;
    }

    public void clear() {
        cart.clear();
    }
}
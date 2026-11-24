package com.example.examen03_dyvr;

import java.io.Serializable;

/** Un producto de https://dummyjson.com/products (solo los campos que usa la app). */
public class Product implements Serializable {
    public int id;
    public String title;
    public String description;
    public double price;
    public String category;
    public String brand; // algunos productos no traen marca
    public double rating;
    public String thumbnail;

    /** "home-decoration" -> "Home decoration" */
    public static String formatCategoria(String categoria) {
        if (categoria == null || categoria.isEmpty()) return "";
        String s = categoria.replace('-', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}

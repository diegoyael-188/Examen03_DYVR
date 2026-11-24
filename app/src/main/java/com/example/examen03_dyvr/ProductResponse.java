package com.example.examen03_dyvr;

import java.util.List;

/** Respuesta raíz del endpoint: { "products": [...], "total", "skip", "limit" } */
public class ProductResponse {
    public List<Product> products;
    public int total;
    public int skip;
    public int limit;
}

package com.example.examen03_dyvr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private final List<Product> products = new ArrayList<>();

    public void setProducts(List<Product> nuevos) {
        products.clear();
        products.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Product p = products.get(position);
        Context ctx = h.itemView.getContext();
        h.nombre.setText(p.title);
        h.descripcion.setText(p.description);
        h.precio.setText(ctx.getString(R.string.precio, p.price));
        h.categoria.setText(Product.formatCategoria(p.category));
        h.rating.setText(ctx.getString(R.string.rating, p.rating));
        Glide.with(h.imagen).load(p.thumbnail).into(h.imagen);
        h.itemView.setOnClickListener(v -> ctx.startActivity(
                new Intent(ctx, DetalleActivity.class).putExtra(DetalleActivity.EXTRA_PRODUCT, p)));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imagen;
        final TextView nombre, descripcion, precio, categoria, rating;

        ViewHolder(View v) {
            super(v);
            imagen = v.findViewById(R.id.imgProducto);
            nombre = v.findViewById(R.id.tvNombre);
            descripcion = v.findViewById(R.id.tvDescripcion);
            precio = v.findViewById(R.id.tvPrecio);
            categoria = v.findViewById(R.id.tvCategoria);
            rating = v.findViewById(R.id.tvRating);
        }
    }
}

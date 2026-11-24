package com.example.examen03_dyvr;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class DetalleActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT = "product";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Product p = IntentCompat.getSerializableExtra(getIntent(), EXTRA_PRODUCT, Product.class);
        if (p == null) {
            finish();
            return;
        }

        Glide.with(this).load(p.thumbnail).into((ImageView) findViewById(R.id.imgProducto));
        ((TextView) findViewById(R.id.tvNombre)).setText(p.title);
        ((TextView) findViewById(R.id.tvDescripcion)).setText(p.description);
        ((TextView) findViewById(R.id.tvPrecio)).setText(getString(R.string.precio, p.price));
        ((TextView) findViewById(R.id.tvCategoria)).setText(getString(R.string.categoria, p.category));
        ((TextView) findViewById(R.id.tvMarca)).setText(getString(R.string.marca,
                p.brand != null ? p.brand : getString(R.string.sin_marca)));
        ((TextView) findViewById(R.id.tvCalificacion)).setText(getString(R.string.calificacion, p.rating));
    }
}

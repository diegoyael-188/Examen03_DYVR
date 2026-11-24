package com.example.examen03_dyvr;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

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

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Glide.with(this).load(p.thumbnail).into((ImageView) findViewById(R.id.imgProducto));
        ((TextView) findViewById(R.id.tvNombre)).setText(p.title);
        ((TextView) findViewById(R.id.tvDescripcion)).setText(p.description);
        ((TextView) findViewById(R.id.tvPrecio)).setText(getString(R.string.precio, p.price));
        ((TextView) findViewById(R.id.tvCategoria)).setText(Product.formatCategoria(p.category));
        ((TextView) findViewById(R.id.tvCalificacion)).setText(getString(R.string.calificacion, p.rating));

        // Algunos productos no traen marca: se oculta la etiqueta
        TextView tvMarca = findViewById(R.id.tvMarca);
        if (p.brand != null && !p.brand.isEmpty()) tvMarca.setText(p.brand);
        else tvMarca.setVisibility(View.GONE);
    }
}

package com.example.examen03_dyvr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final ProductAdapter adapter = new ProductAdapter();
    private Button btnConsultar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnConsultar = findViewById(R.id.btnConsultar);
        progressBar = findViewById(R.id.progressBar);
        RecyclerView rv = findViewById(R.id.rvProductos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnConsultar.setOnClickListener(v -> consultarProductos());
    }

    private void consultarProductos() {
        setCargando(true);
        RetrofitClient.SERVICE.getProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                setCargando(false);
                ProductResponse body = response.body();
                if (response.isSuccessful() && body != null && body.products != null) {
                    adapter.setProducts(body.products);
                    btnConsultar.setText(R.string.actualizar); // el mismo botón ahora vuelve a consultar la API
                } else {
                    mostrarError(getString(R.string.error_servidor, response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                setCargando(false);
                mostrarError(getString(R.string.error_conexion, t.getLocalizedMessage()));
            }
        });
    }

    private void setCargando(boolean cargando) {
        progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnConsultar.setEnabled(!cargando);
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}

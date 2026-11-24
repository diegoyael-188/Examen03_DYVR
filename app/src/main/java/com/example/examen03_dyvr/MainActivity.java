package com.example.examen03_dyvr;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final ProductAdapter adapter = new ProductAdapter();
    private List<Product> todos = new ArrayList<>();
    private String categoriaSel = null; // null = todas
    private String busqueda = "";

    private Button btnConsultar;
    private ImageButton btnActualizar;
    private ProgressBar progressBar;
    private ChipGroup chipGroup;
    private TextView tvSinResultados;

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
        btnActualizar = findViewById(R.id.btnActualizar);
        progressBar = findViewById(R.id.progressBar);
        chipGroup = findViewById(R.id.chipGroup);
        tvSinResultados = findViewById(R.id.tvSinResultados);
        RecyclerView rv = findViewById(R.id.rvProductos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btnConsultar.setOnClickListener(v -> consultarProductos());
        btnActualizar.setOnClickListener(v -> consultarProductos());

        chipGroup.setOnCheckedStateChangeListener((group, ids) -> {
            if (ids.isEmpty()) return;
            categoriaSel = (String) group.findViewById(ids.get(0)).getTag();
            aplicarFiltro();
        });

        ((TextView) findViewById(R.id.etBuscar)).addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                busqueda = s.toString().trim().toLowerCase(Locale.ROOT);
                aplicarFiltro();
            }
        });
    }

    private void consultarProductos() {
        setCargando(true);
        RetrofitClient.SERVICE.getProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                ProductResponse body = response.body();
                if (response.isSuccessful() && body != null && body.products != null) {
                    todos = body.products;
                    construirChips();
                    aplicarFiltro();
                } else {
                    mostrarError(getString(R.string.error_servidor, response.code()));
                }
                setCargando(false);
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                setCargando(false);
                mostrarError(getString(R.string.error_conexion, t.getLocalizedMessage()));
            }
        });
    }

    /** Un chip "Todos" + uno por cada categoría recibida; conserva la selección si sigue existiendo. */
    private void construirChips() {
        Set<String> categorias = new LinkedHashSet<>();
        for (Product p : todos) categorias.add(p.category);
        if (!categorias.contains(categoriaSel)) categoriaSel = null;

        chipGroup.removeAllViews();
        agregarChip(getString(R.string.todos), null);
        for (String c : categorias) agregarChip(Product.formatCategoria(c), c);
    }

    private void agregarChip(String texto, String categoria) {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.chip_categoria, chipGroup, false);
        chip.setId(View.generateViewId());
        chip.setText(texto);
        chip.setTag(categoria);
        chipGroup.addView(chip);
        if (Objects.equals(categoria, categoriaSel)) chipGroup.check(chip.getId());
    }

    private void aplicarFiltro() {
        List<Product> filtrados = new ArrayList<>();
        for (Product p : todos) {
            boolean coincideCategoria = categoriaSel == null || categoriaSel.equals(p.category);
            boolean coincideTexto = busqueda.isEmpty()
                    || (p.title != null && p.title.toLowerCase(Locale.ROOT).contains(busqueda));
            if (coincideCategoria && coincideTexto) filtrados.add(p);
        }
        adapter.setProducts(filtrados);
        tvSinResultados.setVisibility(filtrados.isEmpty() && !todos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setCargando(boolean cargando) {
        progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnActualizar.setEnabled(!cargando);
        // El botón grande solo aparece mientras aún no hay productos cargados
        btnConsultar.setVisibility(!cargando && todos.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}

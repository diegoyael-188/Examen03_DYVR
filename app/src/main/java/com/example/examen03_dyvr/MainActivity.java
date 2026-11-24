package com.example.examen03_dyvr;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

    private ImageButton btnActualizar;
    private RecyclerView rv;
    private View skeleton;
    private ProgressBar progressBar;
    private ObjectAnimator pulso;
    private ChipGroup chipGroup;
    private TextView tvMensaje;

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

        btnActualizar = findViewById(R.id.btnActualizar);
        skeleton = findViewById(R.id.skeleton);
        progressBar = findViewById(R.id.progressBar);
        chipGroup = findViewById(R.id.chipGroup);
        tvMensaje = findViewById(R.id.tvMensaje);
        rv = findViewById(R.id.rvProductos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Efecto "pulso" del skeleton mientras carga
        pulso = ObjectAnimator.ofFloat(skeleton, View.ALPHA, 1f, 0.4f);
        pulso.setDuration(800);
        pulso.setRepeatMode(ValueAnimator.REVERSE);
        pulso.setRepeatCount(ValueAnimator.INFINITE);

        btnActualizar.setOnClickListener(v -> consultarProductos());

        chipGroup.setOnCheckedStateChangeListener((group, ids) -> {
            if (ids.isEmpty()) return;
            categoriaSel = (String) group.findViewById(ids.get(0)).getTag();
            aplicarFiltro();
        });

        // Filtra en tiempo real; el botón "buscar" del teclado solo lo cierra
        EditText etBuscar = findViewById(R.id.etBuscar);
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                busqueda = s.toString().trim().toLowerCase(Locale.ROOT);
                aplicarFiltro();
            }
        });
        etBuscar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_SEARCH) return false;
            getSystemService(InputMethodManager.class).hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
            return true;
        });

        consultarProductos(); // carga automática al entrar
    }

    private void consultarProductos() {
        setCargando(true);
        RetrofitClient.SERVICE.getProducts().enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                setCargando(false);
                ProductResponse body = response.body();
                if (response.isSuccessful() && body != null && body.products != null) {
                    todos = body.products;
                    construirChips();
                } else {
                    mostrarError(getString(R.string.error_servidor, response.code()));
                }
                aplicarFiltro();
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                setCargando(false);
                mostrarError(getString(R.string.error_conexion, t.getLocalizedMessage()));
                aplicarFiltro();
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

        if (filtrados.isEmpty()) {
            tvMensaje.setText(todos.isEmpty() ? R.string.sin_productos : R.string.sin_resultados);
            tvMensaje.setVisibility(skeleton.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        } else {
            tvMensaje.setVisibility(View.GONE);
        }
    }

    private void setCargando(boolean cargando) {
        btnActualizar.setEnabled(!cargando);
        progressBar.setVisibility(cargando ? View.VISIBLE : View.INVISIBLE);
        skeleton.setVisibility(cargando ? View.VISIBLE : View.GONE);
        rv.setVisibility(cargando ? View.GONE : View.VISIBLE);
        if (cargando) {
            tvMensaje.setVisibility(View.GONE);
            pulso.start();
        } else {
            pulso.cancel();
            skeleton.setAlpha(1f);
        }
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}

package com.example.financialcontroller;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialcontroller.data.CategoryEntity;
import com.example.financialcontroller.data.DatabaseClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class Categories extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        recyclerView = findViewById(R.id.rv_categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fabAdd = findViewById(R.id.fab_add_category);
        fabAdd.setOnClickListener(v -> showAddCategoryDialog());

        loadCategories();
    }

    private void loadCategories() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CategoryEntity> categories = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .categoryDao()
                    .getAllCategories();

            new Handler(Looper.getMainLooper()).post(() -> {
                adapter = new CategoryAdapter(categories);
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        // Make background transparent so your CardView corners show nicely
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Initialize Views from your new dialog XML
        EditText etName = view.findViewById(R.id.et_category_name);
        Button btnSave = view.findViewById(R.id.btn_save_cat);
        Button btnCancel = view.findViewById(R.id.btn_cancel_cat);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                saveCategory(name);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveCategory(String name) {
        Executors.newSingleThreadExecutor().execute(() -> {
            CategoryEntity newCategory = new CategoryEntity(name, "#4169E1", "default");
            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .categoryDao()
                    .insert(newCategory);

            loadCategories(); // Refresh list
        });
    }
}
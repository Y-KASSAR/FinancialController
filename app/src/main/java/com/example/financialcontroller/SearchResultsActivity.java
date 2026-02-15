package com.example.financialcontroller;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financialcontroller.data.DatabaseClient;
import com.example.financialcontroller.data.TransactionEntity;

import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvCount, tvNoResults;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        recyclerView = findViewById(R.id.rv_search_results);
        tvCount = findViewById(R.id.tv_result_count);
        tvNoResults = findViewById(R.id.tv_no_results);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get data passed from SearchFilter
        String keyword = getIntent().getStringExtra("KEYWORD");
        String type = getIntent().getStringExtra("TYPE");
        String category = getIntent().getStringExtra("CATEGORY");
        long fromDate = getIntent().getLongExtra("FROM", 0);
        long toDate = getIntent().getLongExtra("TO", 0);

        performSearch(keyword, type, category, fromDate, toDate);
    }

    private void performSearch(String keyword, String type, String category, long from, long to) {
        new Thread(() -> {
            List<TransactionEntity> results = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .transactionDao()
                    .searchTransactions(type, category, from, to, keyword);

            runOnUiThread(() -> {
                if (results.isEmpty()) {
                    tvNoResults.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    tvCount.setText("Found 0 transactions");
                } else {
                    tvNoResults.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvCount.setText("Found " + results.size() + " transactions");

                    // FIX: Context first, then List
                    adapter = new TransactionAdapter(SearchResultsActivity.this, results);
                    recyclerView.setAdapter(adapter);
                }
            });
        }).start();
    }
}
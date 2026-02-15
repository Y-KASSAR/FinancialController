package com.example.financialcontroller;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.financialcontroller.data.CategoryEntity;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryEntity> categoryList;

    public CategoryAdapter(List<CategoryEntity> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your new item_category.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryEntity category = categoryList.get(position);

        // Set Name
        holder.tvName.setText(category.name);

        // Optional: Handle icon color or image if you have logic for it
        // For now, we keep the default tint from XML or set it dynamically
        // holder.ivIcon.setColorFilter(Color.parseColor("#4169E1"));
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivIcon, ivEdit;
        View bgIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Updated IDs based on your new XML
            tvName = itemView.findViewById(R.id.tv_cat_name);
            ivIcon = itemView.findViewById(R.id.iv_cat_icon);
            bgIcon = itemView.findViewById(R.id.view_cat_icon_bg);
            ivEdit = itemView.findViewById(R.id.iv_edit);
        }
    }
}
package com.example.smarttourism.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourism.R;
import com.example.smarttourism.entity.SaleItem;

import java.util.List;

public class SaleItemAdapter extends RecyclerView.Adapter<SaleItemAdapter.ViewHolder> {
    private final List<SaleItem> items;
    private final LayoutInflater inflater;

    public SaleItemAdapter(Context ctx, List<SaleItem> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(ctx);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.sale_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        SaleItem it = items.get(pos);
        holder.name.setText(it.getSight_name());
        holder.sale.setText(it.getSight_sale());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, sale;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sightName);
            sale = itemView.findViewById(R.id.sightSale);
        }
    }
}

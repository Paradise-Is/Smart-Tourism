package com.example.smarttourism.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarttourism.R;
import com.example.smarttourism.entity.AddressBean;

import java.util.List;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {
    protected OnItemClickListener mItemClickListener;
    private Activity mActivity;
    private List<AddressBean> list;

    public MapAdapter(Activity activity) {
        this.mActivity = activity;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.map_item, viewGroup, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        //先用立即绑定显示
        AddressBean item = list.get(i);
        viewHolder.mMapName.setText(item.getTitle());
        viewHolder.mMapContent.setText(item.getText());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && mItemClickListener != null) {
                    mItemClickListener.onItemClick(list.get(pos));
                }
            }
        });
    }

    //设置数据集
    public void setData(List<AddressBean> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(AddressBean item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mMapName, mMapContent;
        private final RelativeLayout relay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relay = itemView.findViewById(R.id.relay);
            mMapName = itemView.findViewById(R.id.map_title);
            mMapContent = itemView.findViewById(R.id.map_content);
        }
    }
}



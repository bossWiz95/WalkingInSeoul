package com.example.walkinginseoul;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ParkVO> list_park;

    public OnItemClickListener mOnItemClickListener = null;

    public interface OnItemClickListener {
        void onItemClick(View view, ParkVO parkVO);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ParkAdapter(Context mContext, ArrayList<ParkVO> list_park) {
        this.mContext = mContext;
        this.list_park = list_park;
    }

    @NonNull
    @Override
    public ParkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.park_item, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkAdapter.ViewHolder holder, int position) {
        final ParkVO parkVO = list_park.get(position);


        Glide.with(mContext)
                .load(parkVO.getImg())
                .thumbnail(0.5f)
                .into(holder.img_title);


        holder.txt_address.setText(parkVO.getAddress());
        holder.txt_title.setText(parkVO.getTitle());

        holder.layout_parkitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, parkVO);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_park.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout layout_parkitem;
        private ImageView img_title;
        private TextView txt_title;
        private TextView txt_address;

        public ViewHolder(View convertView) {
            super(convertView);

            layout_parkitem = (LinearLayout) convertView.findViewById(R.id.layout_parkitem);
            img_title = (ImageView) convertView.findViewById(R.id.img_title);
            txt_title = (TextView) convertView.findViewById(R.id.txt_title);
            txt_address = (TextView) convertView.findViewById(R.id.txt_address);
        }
    }
}

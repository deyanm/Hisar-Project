package com.deyanm.hisar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deyanm.hisar.databinding.ListItemBinding;
import com.deyanm.hisar.model.Hotel;

import java.util.ArrayList;

public class HotelsAdapter extends RecyclerView.Adapter<HotelsAdapter.PokemonViewHolder> {
    private Context mContext;
    private ArrayList<Hotel> mList;
    private ListItemBinding binding;

    public HotelsAdapter(Context mContext, ArrayList<Hotel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        binding = ListItemBinding.inflate(inflater,parent,false);
        return new PokemonViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        holder.itemBinding.hotelName.setText(mList.get(position).getName());
        Glide.with(mContext).load(mList.get(position).getUrl())
                .into(holder.itemBinding.hotelImage);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class PokemonViewHolder extends RecyclerView.ViewHolder{
        private ListItemBinding itemBinding;

        public PokemonViewHolder(ListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }

    public  void updateList(ArrayList<Hotel> updatedList){
        mList = updatedList;
        notifyDataSetChanged();
    }

    public Hotel getHotelAt(int position){
        return mList.get(position);
    }
}
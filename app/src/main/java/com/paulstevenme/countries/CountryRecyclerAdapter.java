package com.paulstevenme.countries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CountryRecyclerAdapter extends RecyclerView.Adapter<CountryRecyclerAdapter.ViewHolder> {
    public List<CountryItem> rv_list;

    public CountryRecyclerAdapter(List<CountryItem> list) {
        this.rv_list = list;
    }

    @NonNull
    @Override
    public CountryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rc_country_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CountryRecyclerAdapter.ViewHolder holder, int position) {
        holder.image.setImageResource(rv_list.get(position).getImg());
        holder.title.setText(rv_list.get(position).getTitle());
        holder.desc.setText(rv_list.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return rv_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, desc;
        private ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cd_rv_img);
            title = itemView.findViewById(R.id.cd_rv_tv_name);
            desc = itemView.findViewById(R.id.cd_rv_tv_desc);

        }
    }

}

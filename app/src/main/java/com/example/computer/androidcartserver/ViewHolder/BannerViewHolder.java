package com.example.computer.androidcartserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.computer.androidcartserver.Common.Common;
import com.example.computer.androidcartserver.Interface.ItemClickListener;
import com.example.computer.androidcartserver.R;

public class BannerViewHolder  extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener
{
    public TextView banner_name;
    public ImageView banner_image;
    public BannerViewHolder(@NonNull View itemView) {
        super(itemView);

        banner_name = (TextView)itemView.findViewById(R.id.banner_name);
        banner_image = (ImageView)itemView.findViewById(R.id.banner_image);
        itemView.setOnCreateContextMenuListener(this);
       // itemView.setOnClickListener(this);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select the Action");
        menu.add(0,0,getAdapterPosition(),Common.Update);
        menu.add(0,1,getAdapterPosition(),Common.Delete);
    }
}

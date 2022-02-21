package com.cempod.crch;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerIconAdapter extends RecyclerView.Adapter {
    UserIconsManager iconsManager = new UserIconsManager();


    class IconHolder extends RecyclerView.ViewHolder {

        ImageView iconItemImage;
        public IconHolder(@NonNull View itemView) {
            super(itemView);
            iconItemImage = itemView.findViewById(R.id.iconItemImage);

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_item, parent,false);
        return new IconHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TypedValue typedValue = new TypedValue();
        holder.itemView.getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color = typedValue.data;
        ((IconHolder)holder).iconItemImage.setImageResource(iconsManager.getIconIds()[position]);
        Drawable drawable = ((IconHolder)holder).iconItemImage.getBackground();
        if(drawable instanceof ShapeDrawable){
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(color);
            ((IconHolder)holder).iconItemImage.setBackground(drawable);

        }else if (drawable instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) drawable;
            colorDrawable.setColor(color);
            ((IconHolder)holder).iconItemImage.setBackground(drawable);

        }else if (drawable instanceof GradientDrawable) {
            // alpha value may need to be set again after this call
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(color);
            ((IconHolder)holder).iconItemImage.setBackground(drawable);

        }
    }

    @Override
    public int getItemCount() {
        return iconsManager.getIconIds().length;
    }
}

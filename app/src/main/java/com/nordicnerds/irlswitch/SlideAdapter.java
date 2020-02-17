package com.nordicnerds.irlswitch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideAdapter extends PagerAdapter
{
    private Context context;

    int[] lst_images;
    String[] lst_title;
    String[] lst_key;

    SlideAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return lst_title.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide,container,false);

        ImageView PC_img = view.findViewById(R.id.slideimg);
        TextView PC_title= view.findViewById(R.id.txttitle);

        PC_img.setImageResource(lst_images[position]);
        PC_title.setText(lst_title[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((LinearLayout)object);
    }
}
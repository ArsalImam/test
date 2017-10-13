package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bykea.pk.partner.R;

public class HowitworksAdapter extends PagerAdapter {
    private Context context;
    private int[] ImageGal = new int[]{
            R.drawable.one,
            R.drawable.two,
            R.drawable.three,
            R.drawable.four,
    };

    public HowitworksAdapter(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return ImageGal.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == ((ImageView) object);
    }

    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        //int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
        //imageView.setPadding(padding, padding, padding, padding);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(ImageGal[position]);
        ((ViewPager) container).addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }
}
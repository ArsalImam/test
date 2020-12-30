package com.bykea.pk.partner.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;

import com.bykea.pk.partner.R;

import cn.lightsky.infiniteindicator.ImageLoader;
import cn.lightsky.infiniteindicator.OnPageClickListener;
import cn.lightsky.infiniteindicator.Page;
import cn.lightsky.infiniteindicator.recycle.ViewBinder;

public class BannerViewBinder implements ViewBinder {
    @Override
    public View bindView(Context context,
                         final int position,
                         final Page page,
                         ImageLoader imageLoader,
                         final OnPageClickListener mOnPageClickListener,
                         View convertView,
                         ViewGroup container) {

        BannerViewBinder.ViewHolder holder;
        if (convertView != null) {
            holder = (BannerViewBinder.ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_banner_slide, null);
            holder = new BannerViewBinder.ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (holder.target != null) {
            if (mOnPageClickListener != null) {
                holder.target.setOnClickListener(v -> mOnPageClickListener.onPageClick(position, page));
            }

            if (imageLoader != null) {
                imageLoader.load(context, holder.target, page.res);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        final AppCompatImageView target;

        public ViewHolder(View view) {
            target = view.findViewById(R.id.slider_image);
        }
    }
}

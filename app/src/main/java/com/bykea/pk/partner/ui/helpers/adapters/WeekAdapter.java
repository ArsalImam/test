package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bykea.pk.partner.R;

import java.util.List;

public class WeekAdapter extends ArrayAdapter<String> {

    private List<String> list;

    public WeekAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.week_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvTitle.setText(list.get(position));

        return convertView;
    }

    public class ViewHolder {

        public TextView tvTitle;


        public ViewHolder(View view) {
            tvTitle = view.findViewById(R.id.tvTitle);

        }
    }
}

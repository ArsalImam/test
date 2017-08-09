package com.bykea.pk.partner.ui.helpers.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ModelVideoDemo;

import java.util.ArrayList;



public class HowItWorksVideoAdapter extends BaseAdapter {

    private ArrayList<ModelVideoDemo> data = new ArrayList<>();
    private static LayoutInflater inflater = null;
    int i = 0;

    /*************
     * CustomAdapter Constructor
     *****************/
    public HowItWorksVideoAdapter(Activity a, ArrayList<ModelVideoDemo> d) {

        /********** Take passed values **********/
        /**********
         Declare Used Variables
         */Activity activity = a;
        data = d;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /********
     * What is the size of Passed Arraylist Size
     ************/
    public int getCount() {

        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public static class ViewHolder {

        public TextView tvVideoNo,tvVideoName;
        public ImageView imgPlay,imgVideoName;

    }

    /******
     * Depends upon data size called for each row , Create each ListView row
     *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.how_it_works_videos_single_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.tvVideoName = (TextView) vi.findViewById(R.id.tvVideoName);
            holder.tvVideoNo=(TextView) vi.findViewById(R.id.tvVideoNo);
            holder.imgPlay = (ImageView) vi.findViewById(R.id.imgPlay);
            holder.imgVideoName = (ImageView) vi.findViewById(R.id.imgVideoName);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.tvVideoNo.setText("No Data");

        } else {
            /***** Get each Model object from Arraylist ********/

            /************  Set Model values in Holder elements ***********/

            holder.tvVideoNo.setText(data.get(position).getVideoNumber());
            holder.tvVideoName.setText(data.get(position).getVideoName());
            holder.imgVideoName.setImageResource(data.get(position).getImageName());


        }
        return vi;
    }


}

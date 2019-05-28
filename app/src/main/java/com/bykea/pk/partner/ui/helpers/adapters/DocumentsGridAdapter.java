package com.bykea.pk.partner.ui.helpers.adapters;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bykea.pk.partner.DriverApp;
import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.DocumentsData;
import com.bykea.pk.partner.ui.helpers.IntegerCallBack;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocumentsGridAdapter extends RecyclerView.Adapter<DocumentsGridAdapter.ItemHolder> {

    private static DocumentsGridAdapter mInstance;
    private ArrayList<DocumentsData> mList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private int prevPosition = 999, selectedItem = 0, iconCount = 0;
    private IntegerCallBack onCounterValueChange;
    private String imgPath;
    private  ItemHolder itemHolder;
    //    private LinearLayout.LayoutParams lastGridLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams itemGridLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private DocumentsGridAdapter() {

    }

    public synchronized void resetTheInstance() {
        itemHolder = null;
        mList.clear();
        onItemClickListener = null;
        imgPath = null;
        mInstance = null;
    }

    public int getSelectedItemIndex() {
        return selectedItem;
    }

    public void setSelectedItemIndex(int position) {
        selectedItem = position;
    }

    public synchronized static DocumentsGridAdapter getmInstanceForNullCheck() {
        return mInstance;
    }

    public synchronized static DocumentsGridAdapter getInstance() {
        if (mInstance == null) {
            mInstance = new DocumentsGridAdapter();
        }
        return mInstance;
    }

    public synchronized void init(ArrayList<DocumentsData> list, OnItemClickListener onItemClickListener, IntegerCallBack onCounterValueChange) {
        this.onItemClickListener = onItemClickListener;
        this.onCounterValueChange = onCounterValueChange;
        resetIconCount();
        int gridLine = (int) DriverApp.getContext().getResources().getDimension(R.dimen.grid_line);
        if (gridLine < 1) {
            gridLine = 1;
        }
//        lastGridLayoutParams.setMargins(gridLine, gridLine, gridLine, 0);
        itemGridLayoutParams.setMargins(gridLine, gridLine, gridLine, gridLine);
        setItemsList(list);
    }


    public DocumentsData getItem(int position) {
        return mList.get(position);
    }

    public ArrayList<DocumentsData> getItemsList() {
        return mList;
    }

    public void setItemsList(ArrayList<DocumentsData> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.documents_item,
                parent, false);
        itemHolder = new ItemHolder(view);
        return itemHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        DocumentsData data = mList.get(position);
        holder.ivUploaded.setVisibility(data.isUploaded() ? View.VISIBLE : View.GONE);
        holder.loader.setVisibility(data.isUploading() ? View.VISIBLE : View.GONE);
        holder.tvName.setText(data.getName());
        holder.tvUrduName.setText(data.getUrduName());
        holder.llMain.setLayoutParams(itemGridLayoutParams);
        if (data.getImageUri() != null) {
            loadImgURL(holder.ivDocument, holder.loaderImage, data.getImageUri());
        } else if (StringUtils.isNotBlank(data.getImage())) {
            loadImgURL(holder.ivDocument, holder.loaderImage, data.getImage());
        } else {
            holder.ivDocument.setImageDrawable(ContextCompat.getDrawable(DriverApp.getContext(), R.drawable.photo_camera_4_copy));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.llMain)
        LinearLayout llMain;

        @BindView(R.id.ivUploaded)
        ImageView ivUploaded;

        @BindView(R.id.loader)
        ProgressBar loader;

        @BindView(R.id.loaderImage)
        ProgressBar loaderImage;

        @BindView(R.id.ivDocument)
        ImageView ivDocument;

        @BindView(R.id.tvUrduName)
        FontTextView tvUrduName;

        @BindView(R.id.tvName)
        FontTextView tvName;

        ItemHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClickListener(getLayoutPosition(), prevPosition);
            prevPosition = getLayoutPosition();
        }
    }


    private void loadImgURL(ImageView imageView, final ProgressBar loaderImage, Uri link) {
        if (link != null) {
            loaderImage.setVisibility(View.VISIBLE);
            Picasso.get().load(link)
                    .fit().centerInside()
                    .error(R.drawable.photo_camera_4_copy)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            iconCount++;
                            loaderImage.setVisibility(View.GONE);
                            Utils.redLog("Image", "onSuccess " + iconCount);
                            onCounterValueChange.onCallBack(iconCount);
                        }

                        @Override
                        public void onError(Exception e) {
                            iconCount++;
                            loaderImage.setVisibility(View.GONE);
                            Utils.redLog("Image", "onError" + iconCount);
                            onCounterValueChange.onCallBack(iconCount);
                        }
                    });

        }
    }

    private void loadImgURL(ImageView imageView, final ProgressBar loaderImage, String link) {
        if (StringUtils.isNotBlank(link)) {
            loaderImage.setVisibility(View.VISIBLE);
            Utils.redLog("Doc Img", link);
            Picasso.get().load(link)
                    .fit().centerInside()
                    .error(R.drawable.photo_camera_4_copy)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            iconCount++;
                            loaderImage.setVisibility(View.GONE);
                            Utils.redLog("Image", "onSuccess " + iconCount);
                            onCounterValueChange.onCallBack(iconCount);
                        }

                        @Override
                        public void onError(Exception e) {
                            iconCount++;
                            loaderImage.setVisibility(View.GONE);
                            Utils.redLog("Image", "onError" + iconCount);
                            onCounterValueChange.onCallBack(iconCount);
                        }
                    });

        }
    }

    public void resetIconCount() {
        iconCount = 0;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, int prevPosition);
    }
}

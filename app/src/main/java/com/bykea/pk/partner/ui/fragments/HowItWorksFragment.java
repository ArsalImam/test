package com.bykea.pk.partner.ui.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.ModelVideoDemo;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.activities.MyPlayerActivity;
import com.bykea.pk.partner.ui.helpers.adapters.HowItWorksVideoAdapter;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Utils;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class HowItWorksFragment extends Fragment {
    private HomeActivity mCurrentActivity;


    ArrayList<ModelVideoDemo> arrayDemo = new ArrayList<>();
    RecyclerView mRecyclerVeiw;
    String[] videoURLs = new String[]{"https://www.youtube.com/watch?v=D8-PCyUx3ic", "https://www.youtube.com/watch?v=cEC6sPC-fwg", "https://www.youtube.com/watch?v=N7g9JL4oJyg", "https://www.youtube.com/watch?v=D8-PCyUx3ic", "https://www.youtube.com/watch?v=cEC6sPC-fwg", "https://www.youtube.com/watch?v=N7g9JL4oJyg", "https://www.youtube.com/watch?v=D8-PCyUx3ic"};
    String[] videoName = new String[]{"App Overview", "Best Practices", "Ride Process", "Ratings", "Wallet and Payments", "Parcel", "Registration"};
    int[] videoNameImgs = new int[]{R.drawable.app_ka_jaiza, R.drawable.tareeqa_kaar, R.drawable.amal, R.drawable.rating, R.drawable.raqam_aur_adaigi, R.drawable.parcel, R.drawable.registration};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.how_it_works_videos_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCurrentActivity = (HomeActivity) getActivity();
        mCurrentActivity.setToolbarTitle("How it works", "طریقہ کار");
        mCurrentActivity.hideToolbarLogo();
        mCurrentActivity.findViewById(R.id.toolbarLine).setVisibility(View.VISIBLE);
        mCurrentActivity.findViewById(R.id.statusLayout).setVisibility(View.VISIBLE);
        mCurrentActivity.hideStatusCompletely();

        mRecyclerVeiw = (RecyclerView) view.findViewById(R.id.lvVideoDemo);
        String[] videoLinks = AppPreferences.getSettings().getSettings().getVideos().split(",");

        for (int x = 0; x < videoLinks.length; x++) {
            int m = x + 1;

            ModelVideoDemo mDemo = new ModelVideoDemo();
            mDemo.setImageName(R.mipmap.ic_launcher);
            mDemo.setVideoName(x < videoName.length ? videoName[x] : videoName[1]);
            mDemo.setVideoNumber("Video " + m);
            mDemo.setImageName(x < videoNameImgs.length ? videoNameImgs[x] : videoNameImgs[1]);
            mDemo.setVideoURL(videoLinks[x]);
            arrayDemo.add(mDemo);
        }
        mRecyclerVeiw.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(mCurrentActivity, 2);
        mRecyclerVeiw.setLayoutManager(layoutManager);
        final HowItWorksVideoAdapter adapter = new HowItWorksVideoAdapter(mCurrentActivity, arrayDemo, new HowItWorksVideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(ModelVideoDemo data) {
                try {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        // to handle app crash caused by some bug in YouTube App. https://stackoverflow.com/questions/48674311/exception-java-lang-noclassdeffounderror-pim
                        //TODO: Remove it when crash is resolved in latest YouTube App
                        Utils.watchYoutubeVideo(mCurrentActivity, data.getVideoURL().split("v=")[1]);
                        return;
                    }
                    Intent intent = new Intent(mCurrentActivity, MyPlayerActivity.class);
                    intent.putExtra("VIDEO_ID", data.getVideoURL().split("v=")[1]);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        );

        mRecyclerVeiw.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        mCurrentActivity.showToolbar();
        mCurrentActivity.hideUrduTitle();
        super.onDestroyView();
    }
}

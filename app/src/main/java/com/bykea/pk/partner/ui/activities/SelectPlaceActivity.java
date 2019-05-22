package com.bykea.pk.partner.ui.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.models.data.PlacesResult;
import com.bykea.pk.partner.models.data.SavedPlaces;
import com.bykea.pk.partner.ui.fragments.PlacesAreaFragment;
import com.bykea.pk.partner.ui.fragments.PlacesRecentFragment;
import com.bykea.pk.partner.ui.fragments.PlacesSearchFragment;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.helpers.adapters.CustomPagerAdapter;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;
import com.bykea.pk.partner.widgets.NonSwipeableViewPager;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectPlaceActivity extends BaseActivity {
    @BindView(R.id.vpFragments)
    NonSwipeableViewPager mViewPager;

    @BindView(R.id.ivArea)
    ImageView ivArea;

    @BindView(R.id.ivRecent)
    ImageView ivRecent;


    @BindView(R.id.ivSearch)
    ImageView ivSearch;

    @BindView(R.id.ivSaved)
    ImageView ivSaved;

    @BindView(R.id.tvArea)
    FontTextView tvArea;

    @BindView(R.id.tvAreaUrdu)
    FontTextView tvAreaUrdu;

    @BindView(R.id.tvSearch)
    FontTextView tvSearch;

    @BindView(R.id.tvSearchUrdu)
    FontTextView tvSearchUrdu;

    @BindView(R.id.tvSaved)
    FontTextView tvSaved;

    @BindView(R.id.tvSavedUrdu)
    FontTextView tvSavedUrdu;

    @BindView(R.id.tvRecent)
    FontTextView tvRecent;

    @BindView(R.id.tvRecentUrdu)
    FontTextView tvRecentUrdu;

    @BindView(R.id.llTopBarRight)
    LinearLayout llTopBarRight;

    @BindView(R.id.tvDrop)
    FontTextView tvDrop;

    @BindView(R.id.tvPick)
    FontTextView tvPick;


    private CustomPagerAdapter mAdapter;
    private SelectPlaceActivity mCurrentActivity;

    private boolean isPickUp;
    private ArrayList<Fragment> list = new ArrayList<>();

    public CustomPagerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_place);
        ButterKnife.bind(this);
        mCurrentActivity = this;
        mCurrentActivity.setTitleCustomToolbarUrdu(getIntent() != null &&
                StringUtils.isNotBlank(getIntent().getStringExtra(Constants.PLACES_TITLE)) ? getIntent().getStringExtra(Constants.PLACES_TITLE) : "مقام مقرر کریں");
        initViewPager();
        if (showChangeButton()) {
            setTopBar(getIntent().getStringExtra("top_bar").equalsIgnoreCase("PICK_UP"));
        } else {
            llTopBarRight.setVisibility(View.GONE);
        }
    }

    public boolean showChangeButton() {
        return getIntent() != null &&
                StringUtils.isNotBlank(getIntent().getStringExtra("top_bar"));
    }

    public boolean isPickUp() {
        return isPickUp;
    }

    private void setTopBar(boolean pickup) {
        llTopBarRight.setVisibility(View.VISIBLE);
        isPickUp = pickup;
        mCurrentActivity.setTitleCustomToolbarUrdu(isPickUp ? "یہاں سے پِک کریں" : "مقام مقرر کریں");

        if (isPickUp) {
            tvPick.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.white));
            tvDrop.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorPrimary));
            tvPick.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.border_blue_right_round));
            tvDrop.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.border_grey_left_round));
        } else {
            tvDrop.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.white));
            tvPick.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorPrimary));
            tvDrop.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.border_blue_left_round));
            tvPick.setBackground(ContextCompat.getDrawable(mCurrentActivity, R.drawable.border_grey_right_round));
        }
    }

    private void initViewPager() {
        list = new ArrayList<>();
        list.add(new PlacesAreaFragment());
        list.add(getSearchFragment());
//        list.add(new PlacesSavedFragment());
        list.add(new PlacesRecentFragment());
        mAdapter = new CustomPagerAdapter(getSupportFragmentManager(), list);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(list.size());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabsBackground(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setTabsBackground(0);
    }

    @NonNull
    private Fragment getSearchFragment() {
        Fragment placesSearchFragment = new PlacesSearchFragment();
        Bundle bundle = new Bundle();
        if (getIntent() != null && getIntent().getParcelableExtra(Constants.Extras.SELECTED_ITEM) != null) {
            PlacesResult placesResult = getIntent().getParcelableExtra(Constants.Extras.SELECTED_ITEM);
            bundle.putParcelable(Constants.Extras.SELECTED_ITEM, placesResult);
        }
        bundle.putBoolean(Constants.Extras.IS_FROM_VIEW_PAGER, true);
        placesSearchFragment.setArguments(bundle);
        return placesSearchFragment;
    }


    private void setTabsBackground(int position) {
        if (position == 0) {
            mCurrentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            setSelected(ivArea, R.drawable.ic_location_grey, tvArea, tvAreaUrdu);
            setUnSelected(ivSearch, R.drawable.ic_search_grey, tvSearch, tvSearchUrdu);
//            setUnSelected(ivSaved, R.drawable.ic_star_grey, tvSaved, tvSavedUrdu);
            setUnSelected(ivRecent, R.drawable.ic_reload_grey, tvRecent, tvRecentUrdu);
        } else if (position == 1) {
            setUnSelected(ivArea, R.drawable.ic_location_grey, tvArea, tvAreaUrdu);
            setSelected(ivSearch, R.drawable.ic_search_grey, tvSearch, tvSearchUrdu);
//            setUnSelected(ivSaved, R.drawable.ic_star_grey, tvSaved, tvSavedUrdu);
            setUnSelected(ivRecent, R.drawable.ic_reload_grey, tvRecent, tvRecentUrdu);
            ((PlacesSearchFragment) mAdapter.getItem(position)).setInitMap();
        } /*else if (position == 2) {
            setUnSelected(ivArea, R.drawable.ic_location_grey, tvArea, tvAreaUrdu);
            setUnSelected(ivSearch, R.drawable.ic_search_grey, tvSearch, tvSearchUrdu);
//            setSelected(ivSaved, R.drawable.ic_star_grey, tvSaved, tvSavedUrdu);
            setUnSelected(ivRecent, R.drawable.ic_reload_grey, tvRecent, tvRecentUrdu);
            ((PlacesSavedFragment) mAdapter.getItem(position)).updateAdapter();
        }*/ else if (position == 2) {
            setUnSelected(ivArea, R.drawable.ic_location_grey, tvArea, tvAreaUrdu);
            setUnSelected(ivSearch, R.drawable.ic_search_grey, tvSearch, tvSearchUrdu);
//            setUnSelected(ivSaved, R.drawable.ic_star_grey, tvSaved, tvSavedUrdu);
            setSelected(ivRecent, R.drawable.ic_reload_grey, tvRecent, tvRecentUrdu);
        }

    }

    private void setSelected(ImageView imageView, int drawable, FontTextView textView, FontTextView textViewUrdu) {
        imageView.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, drawable, R.color.colorAccent));
        textView.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorPrimary));
        textViewUrdu.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.textColorPrimary));
    }

    private void setUnSelected(ImageView imageView, int drawable, FontTextView textView, FontTextView textViewUrdu) {
        imageView.setImageDrawable(Utils.changeDrawableColor(mCurrentActivity, drawable, R.color.secondaryColor4));
        textView.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.secondaryColor4));
        textViewUrdu.setTextColor(ContextCompat.getColor(mCurrentActivity, R.color.secondaryColor4));
    }

    @OnClick({R.id.llRecent, R.id.llArea, R.id.llSaved, R.id.llSearch, R.id.tvPick, R.id.tvDrop})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.llArea:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.llSearch:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.llSaved:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.llRecent:
                mViewPager.setCurrentItem(3);
                break;
            case R.id.tvPick:
                setTopBar(true);
                break;
            case R.id.tvDrop:
                setTopBar(false);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            Fragment fragment = list.get(0);
            if (fragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
                fragment.getChildFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }


    public String isPlaceSaved(String place, double lat, double lng) {
        String isSaved = StringUtils.EMPTY;
        ArrayList<SavedPlaces> savedPlaces = AppPreferences.getSavedPlaces();
        if (savedPlaces != null && savedPlaces.size() > 0) {
            for (SavedPlaces savedPlace : savedPlaces) {
                if (savedPlace.getAddress().equalsIgnoreCase(place)) {
                    float distance = Utils.calculateDistance(lat, lng, savedPlace.getLat(), savedPlace.getLng());
                    if (distance < Constants.SAVED_PLACES_RADIUS) {
                        isSaved = savedPlace.getPlaceId();
                        if (distance != 0) {
                            savedPlace.setLat(lat);
                            savedPlace.setLng(lng);
                            AppPreferences.updateSavedPlace(savedPlaces);
                        }
                        break;
                    }
                }
            }
        }
        return isSaved;
    }

    public void removeSavedPlace(String place, double lat, double lng) {
        ArrayList<SavedPlaces> savedPlaces = AppPreferences.getSavedPlaces();
        if (savedPlaces != null && savedPlaces.size() > 0) {
            for (int i = 0; i < savedPlaces.size(); i++) {
                if (savedPlaces.get(i).getAddress().equalsIgnoreCase(place)
                        && Utils.calculateDistance(lat, lng, savedPlaces.get(i).getLat(), savedPlaces.get(i).getLng()) < Constants.SAVED_PLACES_RADIUS) {
                    savedPlaces.remove(i);
                    AppPreferences.updateSavedPlace(savedPlaces);
                    break;
                }
            }
        }
    }

}

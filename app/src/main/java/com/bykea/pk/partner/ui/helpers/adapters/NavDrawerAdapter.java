package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.booking.BookingListingFragment;
import com.bykea.pk.partner.ui.fragments.ContactUsFragment;
import com.bykea.pk.partner.ui.fragments.HomeFragment;
import com.bykea.pk.partner.ui.fragments.ProfileFragment;
import com.bykea.pk.partner.ui.fragments.TripHistoryFragment;
import com.bykea.pk.partner.ui.fragments.WalletFragment;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.ui.offlinerides.OfflineRidesFragment;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Constants;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.TelloTalkManager;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bykea.pk.partner.utils.Constants.ScreenRedirections.HOME_SCREEN_S;

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {
    private String[] titles;
    private String[] icons;
    private String[] newLabel;
    private Context context;
    private HomeActivity mainActivity;
    private FragmentManager fragmentManager;

    // The default constructor to receive titles,icons and context from MainActivity.
    public NavDrawerAdapter(String[] titles, String[] icons, String[] newLabel, Context context) {
        this.titles = titles;
        this.icons = icons;
        this.newLabel = newLabel;
        this.context = context;
        mainActivity = (HomeActivity) context;
        fragmentManager = mainActivity.getSupportFragmentManager();
    }

    /**
     * Its a inner class to NavDrawerAdapter Class.
     * This ViewHolder class implements View.OnClickListener to handle click events.
     * If the itemType==1 ; it implies that the view is a single row_item with TextView and ImageView.
     * This ViewHolder describes an item view with respect to its place within the RecyclerView.
     * For every item there is a ViewHolder associated with it .
     */

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        FontTextView navTitle, navIcon;
        ImageView logoutIv;
        CircleImageView driverImage;
        FontTextView driverName;
        RatingBar driverRb;
        FontTextView tvRating;
        FontTextView newLabel;

        Context context;

        ViewHolder(View drawerItem, int itemType, Context context) {

            super(drawerItem);
            this.context = context;
            drawerItem.setOnClickListener(this);

            if (itemType == 1) {
                navTitle = (FontTextView) itemView.findViewById(R.id.tv_NavTitle);
                navIcon = (FontTextView) itemView.findViewById(R.id.iv_NavIcon);
                newLabel = (FontTextView) itemView.findViewById(R.id.newLabel);
            } else if (itemType == 2)//footer Logout grey_square_bg_img
            {
                logoutIv = (ImageView) itemView.findViewById(R.id.logoutIv);
            } else if (itemType == 0)//Header driver info
            {
                driverImage = (CircleImageView) itemView.findViewById(R.id.driverImage);
                driverName = (FontTextView) itemView.findViewById(R.id.driverNameTv);
                driverRb = (RatingBar) itemView.findViewById(R.id.driverRb);
                tvRating = (FontTextView) itemView.findViewById(R.id.tvRating);
            }
        }

        /**
         * This defines onClick for every item with respect to its position.
         */

        @Override
        public void onClick(View v) {
            mainActivity.drawerLayout.closeDrawers();
            int position = getLayoutPosition();

            if (getLayoutPosition() == 0) {
                updateCurrentFragment(new ProfileFragment(), Constants.ScreenRedirections.PROFILE_SCREEN_S);
            } else if (getLayoutPosition() > 0 && getLayoutPosition() < getItemCount() - 1) {
                switch (getItem(getLayoutPosition() - 1)) {
                    case Constants.ScreenRedirections.PROFILE_SCREEN_S:// This case is for driver header part click.
                        updateCurrentFragment(new ProfileFragment(), Constants.ScreenRedirections.PROFILE_SCREEN_S);
                        break;
                    case Constants.ScreenRedirections.HOME_SCREEN_S:
                        updateCurrentFragment(new HomeFragment(), Constants.ScreenRedirections.HOME_SCREEN_S);
                        break;
                    case Constants.ScreenRedirections.OFFLINE_RIDES_S:
                        if (AppPreferences.getAvailableStatus()) {
                            Dialogs.INSTANCE.showAlertDialogTick(context, StringUtils.EMPTY, context.getString(R.string.offline_ride_notice), view -> {
                            });
                        } else {
                            updateCurrentFragment(new OfflineRidesFragment(), Constants.ScreenRedirections.OFFLINE_RIDES_S);
                        }
                        break;
                    case Constants.ScreenRedirections.TRIP_HISTORY_SCREEN_S:
                        //TODO need to add backward compatibility here
                        String screenFlag = Constants.ScreenRedirections.TRIP_HISTORY_SCREEN_S;
                        if (AppPreferences.getDriverSettings() != null &&
                                AppPreferences.getDriverSettings().getData() != null &&
                                StringUtils.isNotBlank(AppPreferences.getDriverSettings().getData().getBookingLisitingForDriverUrl())) {
                            updateCurrentFragment(new TripHistoryFragment(), screenFlag);
                        } else {
                            updateCurrentFragment(new BookingListingFragment(), screenFlag);
                        }
                        break;
                    case Constants.ScreenRedirections.WALLET_SCREEN_S:
                        updateCurrentFragment(new WalletFragment(), Constants.ScreenRedirections.WALLET_SCREEN_S);
                        break;
                    case Constants.ScreenRedirections.HOW_IT_WORKS_SCREEN_S:
                        Utils.startCustomWebViewActivity(mainActivity, AppPreferences.getSettings().getSettings().getHowItWorksUrl(), context.getString(R.string.how_it_works));
                        break;
                    case Constants.ScreenRedirections.CONTACT_US_SCREEN_S:
                        updateCurrentFragment(new ContactUsFragment(), Constants.ScreenRedirections.CONTACT_US_SCREEN_S);
                        break;
                }
            } else {
                Dialogs.INSTANCE.showNegativeAlertDialog(context, context.getString(R.string.logout_text_ur), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Connectivity.isConnectedFast(context)) {
                            AppPreferences.setAvailableStatus(false);
                            Dialogs.INSTANCE.dismissDialog();
//                                Dialogs.INSTANCE.showLoader(context);
                            UserRepository repository = new UserRepository();
                            repository.requestPilotLogout(context, new UserDataHandler());
                            Utils.logout(context);
                            TelloTalkManager.instance().logout();
                        }
                    }
                }, null);
            }
        }


    }

    /**
     * Update Current Fragment With The Requested One
     *
     * @param fragment : Replace Previous Fragment With The Fragment
     * @param pos      : Handle Toggle Icon Two Show Or Not
     */
    private void updateCurrentFragment(Fragment fragment, String pos) {
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.containerView, fragment)
                .commit();
        HomeActivity.visibleFragmentNumber = pos;
        /**
         * when navigation in on home screen, show bottom sheet and connection status
         * otherwise hide both
         */
        if (pos.equals(HOME_SCREEN_S)) {
            ((HomeActivity) context).toggleAchaConnection(View.VISIBLE);
            //View.VISIBLE is not used for bottom sheet because when homefragment inflate it will automatically visible
        } else {
            ((HomeActivity) context).toggleAchaConnection(View.GONE);
            ((HomeActivity) context).toggleBottomSheetOnNavigationMenuSelection(View.GONE);
        }
    }

    /**
     * Replace current fragment with the offline fragment
     */
    public void updateCurrentFragmentWithOffline() {
        updateCurrentFragment(new OfflineRidesFragment(), Constants.ScreenRedirections.OFFLINE_RIDES_S);
        notifyDataSetChanged();
    }

    /**
     * This is called�every time�when we need a new ViewHolder and a new ViewHolder is required for every item in RecyclerView.
     * Then this ViewHolder is passed to onBindViewHolder to display items.
     */

    @Override
    public NavDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (viewType == 1) {
            View itemLayout = layoutInflater.inflate(R.layout.drawer_item_layout, parent, false);
            return new ViewHolder(itemLayout, viewType, context);
        } else if (viewType == 0) {
            View itemHeader = layoutInflater.inflate(R.layout.drawer_header_layout, parent, false);
            return new ViewHolder(itemHeader, viewType, context);
        } else if (viewType == 2) {
            View itemLayout = layoutInflater.inflate(R.layout.drawer_footer_layout, parent, false);
            return new ViewHolder(itemLayout, viewType, context);
        }


        return null;
    }

    /**
     * This method is called by RecyclerView.Adapter to display the data at the specified position.�
     * This method should update the contents of the itemView to reflect the item at the given position.
     * So here , if position!=0 it implies its a row_item and we set the title and icon of the view.
     */

    @Override
    public void onBindViewHolder(NavDrawerAdapter.ViewHolder holder, int position) {

        if (position > 0 && position < getItemCount() - 1) {
            if (getItem(position - 1).equals(HomeActivity.visibleFragmentNumber)) {
                holder.navTitle.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else {
                holder.navTitle.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
            }
            holder.navTitle.setText(titles[position - 1]);
            holder.navIcon.setText(icons[position - 1]);
            if (newLabel[position - 1].equals("1"))
                holder.newLabel.setVisibility(View.VISIBLE);
            else
                holder.newLabel.setVisibility(View.INVISIBLE);
        } else if (position == 0) {
            holder.driverName.setText(((HomeActivity) context).getPilotData().getFullName());
            if (StringUtils.isNotBlank(((HomeActivity) context).getPilotData().getRating())) {
                holder.driverRb.setRating(Float.parseFloat(((HomeActivity) context).getPilotData().getRating()));
                String rating = Utils.formatDecimalPlaces(((HomeActivity) context).getPilotData().getRating());
                if (rating.equalsIgnoreCase("0")) {
                    holder.tvRating.setText("Rating N/A");
                } else {
                    holder.tvRating.setText("Rating " + rating);
                }
            }
            if (AppPreferences.isProfileUpdated()
                    && StringUtils.isNotBlank(AppPreferences.getPilotData().getPilotImage())) {
                Utils.loadImgPicasso(holder.driverImage, R.drawable.profile_pic,
                        Utils.getImageLink(AppPreferences.getPilotData().getPilotImage()));
                AppPreferences.setProfileUpdated(false);
            }
        } else if (position == getItemCount() - 1) {
            // No values assignment requires.
        }

    }

    /**
     * It returns the total no. of items . We +1 count to include the header view.
     * So , it the total count is 5 , the method returns 6.
     * This 6 implies that there are 5 row_items and 1 header view with header at position zero.
     */

    @Override
    public int getItemCount() {
        return titles.length + 2;// plus 2 is for top and bottom layouts
    }


    /**
     * This methods returns 0 if the position of the item is '0'.
     * If the position is zero its a header view and if its anything else
     * its a row_item with a title and icon.
     */

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == getItemCount() - 1) {
            return 2;
        } else {
            return 1;
        }
    }

    private String getItem(int id) {
        return titles[id];
    }
}
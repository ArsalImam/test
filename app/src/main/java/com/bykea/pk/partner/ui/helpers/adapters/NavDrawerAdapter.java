package com.bykea.pk.partner.ui.helpers.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bykea.pk.partner.R;
import com.bykea.pk.partner.repositories.UserDataHandler;
import com.bykea.pk.partner.repositories.UserRepository;
import com.bykea.pk.partner.ui.activities.HomeActivity;
import com.bykea.pk.partner.ui.fragments.ContactUsFragment;
import com.bykea.pk.partner.ui.fragments.HomeFragment;
import com.bykea.pk.partner.ui.fragments.HowItWorksFragment;
import com.bykea.pk.partner.ui.fragments.ProfileFragment;
import com.bykea.pk.partner.ui.fragments.TripHistoryFragment;
import com.bykea.pk.partner.ui.fragments.WalletFragment;
import com.bykea.pk.partner.ui.helpers.AppPreferences;
import com.bykea.pk.partner.utils.Connectivity;
import com.bykea.pk.partner.utils.Dialogs;
import com.bykea.pk.partner.utils.Utils;
import com.bykea.pk.partner.widgets.FontTextView;

import org.apache.commons.lang3.StringUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {
    private String[] titles;
    private String[] icons;
    private Context context;

    // The default constructor to receive titles,icons and context from MainActivity.
    public NavDrawerAdapter(String[] titles, String[] icons, Context context) {

        this.titles = titles;
        this.icons = icons;
        this.context = context;
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

        Context context;
        HomeActivity mainActivity;
        FragmentManager fragmentManager;

        ViewHolder(View drawerItem, int itemType, Context context) {

            super(drawerItem);
            this.context = context;
            drawerItem.setOnClickListener(this);

            mainActivity = (HomeActivity) context;
            fragmentManager = mainActivity.getSupportFragmentManager();
            if (itemType == 1) {
                navTitle = (FontTextView) itemView.findViewById(R.id.tv_NavTitle);
                navIcon = (FontTextView) itemView.findViewById(R.id.iv_NavIcon);
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

            switch (getLayoutPosition()) {
                case 0:// This case is for driver header part click.
                    if (HomeActivity.visibleFragmentNumber != 0) {
                        updateCurrentFragment(new ProfileFragment(), 0);
                    }
                    break;
                case 1:
                    if (HomeActivity.visibleFragmentNumber != 1) {
                        updateCurrentFragment(new HomeFragment(), 1);
                    }
                    break;
                case 2:
                    if (HomeActivity.visibleFragmentNumber != 2) {
                        updateCurrentFragment(new TripHistoryFragment(), 2);
                    }
                    break;
                case 3:
                    if (HomeActivity.visibleFragmentNumber != 3) {
                        updateCurrentFragment(new WalletFragment(), 3);
                    }
                    break;
                case 4:
                    if (HomeActivity.visibleFragmentNumber != 4) {
                        updateCurrentFragment(new HowItWorksFragment(), 4);
                    }
                    break;
                case 5:
                    if (HomeActivity.visibleFragmentNumber != 5) {
                        updateCurrentFragment(new ContactUsFragment(), 5);
                    }
                    break;

                case 6://this case is for logout footer part click.
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
                            }
                        }
                    });
                    break;
            }

        }

        private void updateCurrentFragment(Fragment fragment, int pos) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.containerView, fragment)
                    .commit();
            HomeActivity.visibleFragmentNumber = pos;
            if(pos == 1){
                ((HomeActivity)context).toggleAchaConnection(View.VISIBLE);
                //View.VISIBLE is not used for bottom sheet because when homefragment inflate it will automatically visible
            } else {
                ((HomeActivity)context).toggleAchaConnection(View.GONE);
                ((HomeActivity)context).toggleBottomSheetOnNavigationMenuSelection(View.GONE);
            }
        }
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

            if (position == HomeActivity.visibleFragmentNumber) {
                holder.navTitle.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            } else {
                holder.navTitle.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
            }
            holder.navTitle.setText(titles[position - 1]);
            holder.navIcon.setText(icons[position - 1]);

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
                Utils.loadImgPicasso(context, holder.driverImage, R.drawable.profile_pic,
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

}
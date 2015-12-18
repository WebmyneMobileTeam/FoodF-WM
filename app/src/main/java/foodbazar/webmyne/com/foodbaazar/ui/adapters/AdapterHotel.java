package foodbazar.webmyne.com.foodbaazar.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexzh.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.model.Hotel;
import foodbazar.webmyne.com.foodbaazar.ui.activity.HotelDetailsActivity;

public class AdapterHotel extends RecyclerView.Adapter<AdapterHotel.CustomViewHolder> {
    private List<Hotel> hotelList;
    private Context mContext;
    View customView;
    List<Hotel> newHotelList;
    List<Hotel> tempHotelList;
    List<Hotel> originalHotelList;
    int rating;

    public AdapterHotel(Context context, List<Hotel> hotelList) {
        this.hotelList = hotelList;
        this.originalHotelList = hotelList;
        tempHotelList = hotelList;
        this.mContext = context;

    }

    public void setAlpha() {
        hotelList = originalHotelList;

        Collections.sort(hotelList, new Comparator<Hotel>() {
            public int compare(Hotel lhs, Hotel rhs) {
                return lhs.HotelName.compareToIgnoreCase(rhs.HotelName);
            }
        });
        tempHotelList = hotelList;
        notifyDataSetChanged();
    }

    public void clearAll() {
        hotelList = originalHotelList;

        notifyDataSetChanged();
    }

    public void clearCuisine() {
        hotelList = tempHotelList;

        notifyDataSetChanged();
    }


    public void setCuisineFilter(ArrayList<Integer> selectedItems) {
        List<Hotel> blankHotelList = new ArrayList<>();

        if (selectedItems.size() == 0) {
            clearCuisine();

        } else {

            blankHotelList = new ArrayList<>();

            for (int i = 0; i < tempHotelList.size(); i++) {

                for (int j = 0; j < selectedItems.size(); j++) {

                    if (tempHotelList.get(i).getCuisinesArray().contains(selectedItems.get(j))) {
                        blankHotelList.add(tempHotelList.get(i));
                        break;
                    }

                }
            }
            hotelList = blankHotelList;
            notifyDataSetChanged();
        }
    }

    public void setVeg() {
        newHotelList = new ArrayList<>();

        for (int i = 0; i < originalHotelList.size(); i++) {
            if (originalHotelList.get(i).VegNonveg == 1 || originalHotelList.get(i).VegNonveg == 3) {
                newHotelList.add(originalHotelList.get(i));
            }
        }

        hotelList = newHotelList;
        tempHotelList = hotelList;
        notifyDataSetChanged();
    }

    public void setNonveg() {
        newHotelList = new ArrayList<>();

        for (int i = 0; i < originalHotelList.size(); i++) {
            if (originalHotelList.get(i).VegNonveg == 2 || originalHotelList.get(i).VegNonveg == 3) {
                newHotelList.add(originalHotelList.get(i));
            }
        }

        hotelList = newHotelList;
        tempHotelList = hotelList;
        notifyDataSetChanged();
    }

    public void setHomeDelivery() {
        newHotelList = new ArrayList<>();

        for (int i = 0; i < originalHotelList.size(); i++) {
            if (originalHotelList.get(i).IsDelivery) {
                newHotelList.add(originalHotelList.get(i));
            }
        }

        hotelList = newHotelList;
        tempHotelList = hotelList;
        notifyDataSetChanged();
    }

    public void setPickUp() {
        newHotelList = new ArrayList<>();

        for (int i = 0; i < originalHotelList.size(); i++) {
            if (originalHotelList.get(i).IsPickUp) {
                newHotelList.add(originalHotelList.get(i));
            }
        }

        hotelList = newHotelList;
        tempHotelList = hotelList;
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        customView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_hotel, null);

        CustomViewHolder viewHolder = new CustomViewHolder(customView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {

        customViewHolder.textView.setText(hotelList.get(i).HotelName);

        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < hotelList.get(i).lstCuisine.size(); j++) {
            sb.append(hotelList.get(i).lstCuisine.get(j).CuisineName + ", ");
            String str = sb.toString().substring(0, sb.toString().length() - 2);
            customViewHolder.txtCuisine.setText(str);
        }

        if (hotelList.get(i).VegNonveg == 1) { // veg
            customViewHolder.imgVeg.setVisibility(View.VISIBLE);
            customViewHolder.imgNonveg.setVisibility(View.GONE);

        } else if (hotelList.get(i).VegNonveg == 2) { // non-veg
            customViewHolder.imgVeg.setVisibility(View.GONE);
            customViewHolder.imgNonveg.setVisibility(View.VISIBLE);

        } else {
            customViewHolder.imgVeg.setVisibility(View.VISIBLE);
            customViewHolder.imgNonveg.setVisibility(View.VISIBLE);
        }

        if (hotelList.get(i).IsDelivery) {
            customViewHolder.imgHome.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.imgHome.setVisibility(View.GONE);
        }

        if (hotelList.get(i).IsPickUp) {
            customViewHolder.imgPick.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.imgPick.setVisibility(View.GONE);
        }

        if (hotelList.get(i).LogoPath == null || hotelList.get(i).LogoPath.equals("")) {
            Glide.with(mContext).load(R.drawable.bg_image_small).into(customViewHolder.iconHotel);
        } else {
            Glide.with(mContext).load(hotelList.get(i).LogoPath).into(customViewHolder.iconHotel);
        }

        customViewHolder.imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.snack(customView, "Home Delivery");
            }
        });

        customViewHolder.imgPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.snack(customView, "Pick Up");
            }
        });

        rating = 0;
        int ratingSize = hotelList.get(i).lstRR.size();
        if (ratingSize > 0) {
            for (int j = 0; j < ratingSize; j++) {
                rating += hotelList.get(i).lstRR.get(j).Rating;
            }

            customViewHolder.txtRating.setText(new DecimalFormat("##.#").format((double) rating / ratingSize));
            customViewHolder.txtRating.setVisibility(View.VISIBLE);

        } else {
            customViewHolder.txtRating.setVisibility(View.GONE);
        }

        customViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hotelId = hotelList.get(i).HotelId;
                String hotelName = hotelList.get(i).HotelName;
                boolean IsDelivery = hotelList.get(i).IsDelivery;
                boolean IsPickUp = hotelList.get(i).IsPickUp;

                Intent i = new Intent(mContext, HotelDetailsActivity.class);
                i.putExtra("hotelId", hotelId);
                i.putExtra("hotelName", hotelName);
                i.putExtra("hotelCuisine", customViewHolder.txtCuisine.getText().toString());
                i.putExtra("IsDelivery", IsDelivery);
                i.putExtra("IsPickUp", IsPickUp);
                i.putExtra("cuisineID", -1);
                mContext.startActivity(i);
            }
        });

        customViewHolder.textView.setTag(customViewHolder);
    }

    @Override
    public int getItemCount() {
        return (null != hotelList ? hotelList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textView;
        protected TextView txtCuisine;
        protected TextView txtRating;
        protected CircleImageView iconHotel;
        private ImageView imgHome, imgPick;
        protected ImageView imgVeg, imgNonveg;
        protected CardView cardView;

        public CustomViewHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.title);
            this.txtCuisine = (TextView) view.findViewById(R.id.txtCuisine);
            imgVeg = (ImageView) view.findViewById(R.id.imgVeg);
            imgNonveg = (ImageView) view.findViewById(R.id.imgNonveg);
            imgHome = (ImageView) view.findViewById(R.id.imgHome);
            imgPick = (ImageView) view.findViewById(R.id.imgPick);
            this.iconHotel = (CircleImageView) view.findViewById(R.id.iconHotel);
            cardView = (CardView) view.findViewById(R.id.cardView);
            this.txtRating = (TextView) view.findViewById(R.id.txtRating);

        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated

        Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        viewToAnimate.startAnimation(animation);
        //  lastPosition = position;

    }


}

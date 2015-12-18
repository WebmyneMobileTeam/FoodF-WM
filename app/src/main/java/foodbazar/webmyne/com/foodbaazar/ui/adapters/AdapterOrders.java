package foodbazar.webmyne.com.foodbaazar.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.custom.AddRatingDialog;
import foodbazar.webmyne.com.foodbaazar.helpers.ComplexPreferences;
import foodbazar.webmyne.com.foodbaazar.model.UserOrder;
import foodbazar.webmyne.com.foodbaazar.ui.activity.OrderDetailsActivity;

public class AdapterOrders extends RecyclerView.Adapter<AdapterOrders.CustomViewHolder> {
    private List<UserOrder> ordersList;
    private Context mContext;
    onRefreshListener onRefreshListener;
    int type;

    public void setOnRefreshListener(AdapterOrders.onRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public AdapterOrders(Context context, ArrayList<UserOrder> ordersList, int type) {
        this.ordersList = ordersList;
        this.mContext = context;
        this.type = type;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {

        customViewHolder.textHotelName.setText(ordersList.get(i).HotelName);
        customViewHolder.txtOrderDate.setText(ordersList.get(i).CreatedOnString);
        customViewHolder.txtTotal.setText(mContext.getResources().getString(R.string.Rs) + " " + Math.round(ordersList.get(i).PriceToPay));
        String rating = ordersList.get(i).Rating;

        String status = "";
        switch (ordersList.get(i).OrderStatus) {
            case 1:
                status = "Pending";
                customViewHolder.btnSubmitReview.setVisibility(View.GONE);
                customViewHolder.ratingBar.setVisibility(View.GONE);
                break;
            case 2:
                customViewHolder.btnSubmitReview.setVisibility(View.GONE);
                customViewHolder.ratingBar.setVisibility(View.GONE);

                if (rating == null) {
                    customViewHolder.btnSubmitReview.setVisibility(View.VISIBLE);
                    customViewHolder.ratingBar.setVisibility(View.GONE);

                } else {
                    customViewHolder.ratingBar.setVisibility(View.VISIBLE);
                    customViewHolder.btnSubmitReview.setVisibility(View.GONE);

                    customViewHolder.ratingBar.setRating(Float.valueOf(rating));

                    LayerDrawable stars = (LayerDrawable) customViewHolder.ratingBar.getProgressDrawable();
                    stars.getDrawable(2).setColorFilter(mContext.getResources().getColor(R.color.button_dark), PorterDuff.Mode.SRC_ATOP);
                }
                status = "Paid and Delievered";
                break;

            case 3:
                customViewHolder.btnSubmitReview.setVisibility(View.GONE);
                customViewHolder.ratingBar.setVisibility(View.GONE);
                status = "Cancelled";
                break;
        }

        customViewHolder.txtStatus.setText(status);
        customViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = i;
                UserOrder order = ordersList.get(position);

                ComplexPreferences complexPreferences;
                complexPreferences = ComplexPreferences.getComplexPreferences(mContext, "user_pref", 0);
                complexPreferences.putObject("current-order", order);
                complexPreferences.commit();

                mContext.startActivity(new Intent(mContext, OrderDetailsActivity.class));
            }
        });

        customViewHolder.btnSubmitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRatingDialog ratingDialog = new AddRatingDialog(mContext, ordersList.get(i).HotelId, ordersList.get(i).OrderId);
                ratingDialog.setOnButtonsEventListener(new AddRatingDialog.OnButtonEventListener() {
                    @Override
                    public void onSubmit() {
                        notifyDataSetChanged();
                        if (onRefreshListener != null) {
                            onRefreshListener.onRefresh();
                        }
                    }
                });
                ratingDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {

        int count = 0;

        if (type == 0) {
            if (ordersList.size() == 0)
                count = 0;
            else
                count = ordersList.size();
        } else {
            if (ordersList.size() == 0)
                count = 0;
            else if (ordersList.size() > 10)
                count = 10;
            else
                count = ordersList.size();
        }

        return count;
        //return (null != ordersList ? ordersList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textHotelName, txtOrderDate, txtTotal, txtStatus;
        protected CardView cardView;
        protected TextView btnSubmitReview;
        protected RatingBar ratingBar;

        public CustomViewHolder(View view) {
            super(view);
            this.cardView = (CardView) view.findViewById(R.id.cardView);
            this.textHotelName = (TextView) view.findViewById(R.id.textHotelName);
            this.txtOrderDate = (TextView) view.findViewById(R.id.txtOrderDate);
            this.txtTotal = (TextView) view.findViewById(R.id.txtTotal);
            this.txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            this.btnSubmitReview = (TextView) view.findViewById(R.id.btnSubmitReview);
            this.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        }
    }

    public interface onRefreshListener {
        void onRefresh();
    }
}

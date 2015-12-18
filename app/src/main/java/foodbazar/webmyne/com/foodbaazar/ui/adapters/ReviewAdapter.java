package foodbazar.webmyne.com.foodbaazar.ui.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.helpers.Functions;
import foodbazar.webmyne.com.foodbaazar.model.RatingModel;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.CustomViewHolder> {
    ArrayList<RatingModel> lstRR;
    private Context mContext;

    public ReviewAdapter(Context context, ArrayList<RatingModel> lstRR) {
        this.lstRR = lstRR;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rating_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, final int i) {

        customViewHolder.txtBy.setText("By " + lstRR.get(i).FirstName + " " + lstRR.get(i).LastName);
        customViewHolder.txtDate.setText(Functions.parseDate(lstRR.get(i).CreatedOnString, "dd-MM-yyyy hh:mm:ss", "MMMM dd, yyyy hh:mm aa"));

       /* Log.e("orig_date", lstRR.get(i).CreatedOnString);
        Log.e("date", Functions.parseDate(lstRR.get(i).CreatedOnString, "dd-MM-yyyy hh:mm:ss", "MMMM dd, yyyy hh:mm aa"));*/

        if (lstRR.get(i).Review == null || lstRR.get(i).Review.length() == 0)
            customViewHolder.txtComments.setVisibility(View.GONE);
        else
            customViewHolder.txtComments.setText("Comments: " + lstRR.get(i).Review);

        customViewHolder.ratingBar.setRating(lstRR.get(i).Rating);
        LayerDrawable stars = (LayerDrawable) customViewHolder.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(mContext.getResources().getColor(R.color.button_bg), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    public int getItemCount() {
        return (null != lstRR ? lstRR.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtBy, txtDate, txtComments;
        protected RatingBar ratingBar;

        public CustomViewHolder(View view) {
            super(view);
            this.txtBy = (TextView) view.findViewById(R.id.txtBy);
            this.txtDate = (TextView) view.findViewById(R.id.txtDate);
            this.txtComments = (TextView) view.findViewById(R.id.txtComments);
            this.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        }
    }

}

package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 07-12-2015.
 */
public class RatingModel {

    @SerializedName("CreatedOnString")
    public String CreatedOnString;

    @SerializedName("FirstName")
    public String FirstName;

    @SerializedName("LastName")
    public String LastName;

    @SerializedName("Rating")
    public int Rating;

    @SerializedName("Review")
    public String Review;
}

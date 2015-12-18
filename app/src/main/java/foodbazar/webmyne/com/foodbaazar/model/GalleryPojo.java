package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sagartahelyani on 01-12-2015.
 */
public class GalleryPojo {

    @SerializedName("CuisineID")
    public int CuisineID;

    @SerializedName("GalleryName")
    public String GalleryName;

    @SerializedName("Image")
    public String Image;

    @SerializedName("ImagePath")
    public String ImagePath;

    @SerializedName("RestaurantID")
    public int RestaurantID;
}

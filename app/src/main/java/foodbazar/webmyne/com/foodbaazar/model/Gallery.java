package foodbazar.webmyne.com.foodbaazar.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sagartahelyani on 01-12-2015.
 */
public class Gallery {

    @SerializedName("ResponseId")
    public String ResponseId;

    @SerializedName("ResponseMsg")
    public String ResponseMsg;

    @SerializedName("lstGallery")
    public List<GalleryPojo> lstGallery;

}

package foodbazar.webmyne.com.foodbaazar.helpers;

/**
 * Created by palak on 17-04-2015.
 */
public class AppConstants {

    public static final boolean DEBUG = true;
    public static final String DEBUG_TAG = "Food FAD";

    public static int TYPE_NOT_CONNECTED = 0;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_2G = 3;
    public static int TYPE_3G = 4;
    public static int TYPE_4G = 5;

        public static final String BASE_URL = "http://ws.foodfad.in/";
//    public static final String BASE_URL = "http://ws-srv-net.in.webmyne.com/applications/FoodBaazarWS_v2/";
    public static final String GET_CITY = "Hotel.svc/json/GetCityList/1";
    public static final String GET_HOTELS = "Hotel.svc/json/GetAllHotels/";

    public static final String GET_GALLERY_INFO = "Hotel.svc/json/GetGalleryInfo";
    public static final String GET_HOTELS_MENU = "Hotel.svc/json/GetHotelMenuNew/";
    public static final String CHECKOUT_ORDER = "Hotel.svc/json/CheckOutOrdered";
    public static final String LOGIN = "User.svc/json/Login";
    public static final String SIGNUP = "User.svc/json/RegistrationAndroid";
    public static final String ORDER_HISTORY = "User.svc/json/CheckOutOrderedList/";
    public static final String GET_BALANCE = "User.svc/json/GetBalance/";
    public static final String CHANGE_PASSWORD = "User.svc/json/ChangePasswordAndroid";
    public static final String FORGOT_PASSWORD = "User.svc/json/ForGotPassForAndroid/";
    public static final String PLACE_ORDER = BASE_URL + "Hotel.svc/json/AddNewOrderAndroid";

    public static final String GIVE_RATING = "Hotel.svc/json/AddRatingReviewAndroid";
    public static final String CONTACT_US = "User.svc/json/AddContactEnquiryAndroid";

//    public static final String IMAGE_PREFIX = "http://ws-srv-net.in.webmyne.com/Applications/FoodBaazarV2/";
    public static final String IMAGE_PREFIX = "http://foodfad.in/";

    public static final String GENERATE_PDF = IMAGE_PREFIX + "UserDetail/DownloadPDFandroid/";

    public static final int CASH_ON_DELIVERY = 1;
    public static final int CREDIT_CARD = 2;
    public static final int NET_BANKING = 3;
    public static final int DEBIT_CARD = 4;

    public static final int PENDING = 1;

    public static final int COMPLETED = 2;

    public static final int CANCELLED = 3;


    public static final int ProfileImageRes=1001;
    public static int ProfileImageReq=1000;
}

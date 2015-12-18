package foodbazar.webmyne.com.foodbaazar.helpers;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.List;

import foodbazar.webmyne.com.foodbaazar.R;
import foodbazar.webmyne.com.foodbaazar.model.CartPojo;

/**
 * Created by dhruvil on 18-08-2015.
 */
public class ToolHelper {

    Context _ctx;
    View view;
    ImageView imgCartMenu;
    BadgeView badge;
    List<CartPojo> cartPojoList;
    DatabaseHandler handler;
    int value = 0;

    public ToolHelper(Context _ctx, View view) {
        this._ctx = _ctx;
        this.view = view;
        imgCartMenu = (ImageView) view.findViewById(R.id.imgCartMenu);
        badge = new BadgeView(_ctx, imgCartMenu);

    }

    public void displayBadge() {
        cartPojoList = new ArrayList<>();

        handler = new DatabaseHandler(_ctx);
        cartPojoList = handler.getCartPojos();

        value = cartPojoList.size();

        if (value > 0) {

            imgCartMenu.setPadding(0, 18, 20, 0);
            badge.setText(String.valueOf(value));
            badge.setTextSize(_ctx.getResources().getDimension(R.dimen.BADGE_TEXT_SIZE));
            badge.setTextColor(_ctx.getResources().getColor(R.color.choco_color));
            badge.setBadgeBackgroundColor(_ctx.getResources().getColor(R.color.button_bg));
            badge.show();
            ObjectAnimator animator = ObjectAnimator.ofFloat(badge, "rotationY", 0f, 360f);
            animator.setDuration(2500);
            animator.start();

        } else {
            imgCartMenu.setPadding(0, 0, 0, 0);
            badge.hide();
        }

    }

}

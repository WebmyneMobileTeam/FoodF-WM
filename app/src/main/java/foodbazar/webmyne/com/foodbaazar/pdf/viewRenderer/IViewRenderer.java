package foodbazar.webmyne.com.foodbaazar.pdf.viewRenderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import foodbazar.webmyne.com.foodbaazar.pdf.interfaces.IData;
import foodbazar.webmyne.com.foodbaazar.pdf.interfaces.IDisposable;


public interface IViewRenderer extends IDisposable, IData {

    /**
     * attach a context to the renderer
     *
     * @param ctx the context
     */
    void attachContext(Context ctx);

    /**
     * render the view
     *
     * @param width the width
     * @param height the height
     */
    Bitmap render(int width, int height);

    /**
     * render the view
     *
     * @param bitmap a bitmap to render into
     * @param width the width
     * @param height the height
     */
    Bitmap render(Bitmap bitmap, int width, int height);

    /**
     * return the rendered bitmap
     *
     * @return the rendered bitmap
     */
    Bitmap getBitmap();

    /**
     * get the view to render
     *
     * @return the view
     */
    View getView();

    /**
     * width of the canvas
     *
     * @return width
     */
    int getWidth();

    /**
     * height of the canvas
     *
     * @return height
     */
    int getHeight();
}

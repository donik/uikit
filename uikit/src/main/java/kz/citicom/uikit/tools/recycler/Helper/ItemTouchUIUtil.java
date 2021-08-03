package kz.citicom.uikit.tools.recycler.Helper;

import android.graphics.Canvas;
import android.view.View;

import kz.citicom.uikit.tools.recycler.RecyclerView;

/**
 * Created by donik102 on 19.05.2018.
 */

public interface ItemTouchUIUtil {

    /**
     * The default implementation for {@link ItemTouchHelper.Callback#onChildDraw(Canvas,
     * RecyclerView, RecyclerView.ViewHolder, float, float, int, boolean)}
     */
    void onDraw(Canvas c, RecyclerView recyclerView, View view,
                float dX, float dY, int actionState, boolean isCurrentlyActive);

    /**
     * The default implementation for {@link ItemTouchHelper.Callback#onChildDrawOver(Canvas,
     * RecyclerView, RecyclerView.ViewHolder, float, float, int, boolean)}
     */
    void onDrawOver(Canvas c, RecyclerView recyclerView, View view,
                    float dX, float dY, int actionState, boolean isCurrentlyActive);

    /**
     * The default implementation for {@link ItemTouchHelper.Callback#clearView(RecyclerView,
     * RecyclerView.ViewHolder)}
     */
    void clearView(View view);

    /**
     * The default implementation for {@link ItemTouchHelper.Callback#onSelectedChanged(
     * RecyclerView.ViewHolder, int)}
     */
    void onSelected(View view);
}
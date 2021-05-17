package com.AHeaven.ui.tabs.DragNDrop;

/**
 * Interface to notify a {@link androidx.recyclerview.widget.RecyclerView.Adapter} of moving and dismissal event from a {@link
 * androidx.recyclerview.widget.ItemTouchHelper.Callback}.
 */
public interface MovingAdapter {

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and not at the end of a "drop" event.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then end position of the moved item.
     */
    void onItemMove(int fromPosition, int toPosition);


    /**
     * Called when an item has been dismissed by a swipe.
     *
     * @param position The position of the item dismissed.
     */
    void onItemDismiss(int position);
}

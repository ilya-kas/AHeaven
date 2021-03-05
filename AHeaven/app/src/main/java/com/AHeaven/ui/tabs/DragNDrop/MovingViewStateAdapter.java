package com.AHeaven.ui.tabs.DragNDrop;

/**
 * Interface to notify an item ViewHolder of relevant callbacks from {@link
 * androidx.recyclerview.widget.ItemTouchHelper.Callback}.
 */
public interface MovingViewStateAdapter {
    /**
     * Called when the {@link androidx.recyclerview.widget.ItemTouchHelper} first registers an item as being moved or swiped.
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();

    /**
     * Called when the {@link androidx.recyclerview.widget.ItemTouchHelper} has completed the move or swipe, and the active item
     * state should be cleared.
     */
    void onItemReleased();
}

package kz.citicom.uikit.tools.recycler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.collection.ArrayMap;
import androidx.collection.LongSparseArray;
import androidx.core.util.Pools;

import kz.citicom.uikit.tools.recycler.RecyclerView.ItemAnimator.ItemHolderInfo;
import kz.citicom.uikit.tools.recycler.RecyclerView.ViewHolder;

import static kz.citicom.uikit.tools.recycler.ViewInfoStore.InfoRecord.FLAG_APPEAR;
import static kz.citicom.uikit.tools.recycler.ViewInfoStore.InfoRecord.FLAG_APPEAR_AND_DISAPPEAR;
import static kz.citicom.uikit.tools.recycler.ViewInfoStore.InfoRecord.FLAG_APPEAR_PRE_AND_POST;
import static kz.citicom.uikit.tools.recycler.ViewInfoStore.InfoRecord.FLAG_DISAPPEARED;
import static kz.citicom.uikit.tools.recycler.ViewInfoStore.InfoRecord.FLAG_POST;
import static kz.citicom.uikit.tools.recycler.ViewInfoStore.InfoRecord.FLAG_PRE;
import static kz.citicom.uikit.tools.recycler.ViewInfoStore.InfoRecord.FLAG_PRE_AND_POST;

/**
 * Created by donik102 on 19.05.2018.
 */

class ViewInfoStore {

    private static final boolean DEBUG = false;

    /**
     * View data records for pre-layout
     */
    @VisibleForTesting
    final ArrayMap<ViewHolder, InfoRecord> mLayoutHolderMap = new ArrayMap<>();

    @VisibleForTesting
    final LongSparseArray<ViewHolder> mOldChangedHolders = new LongSparseArray<>();

    /**
     * Clears the state and all existing tracking data
     */
    void clear() {
        mLayoutHolderMap.clear();
        mOldChangedHolders.clear();
    }

    /**
     * Adds the item information to the prelayout tracking
     * @param holder The ViewHolder whose information is being saved
     * @param info The information to save
     */
    void addToPreLayout(ViewHolder holder, ItemHolderInfo info) {
        InfoRecord record = mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            mLayoutHolderMap.put(holder, record);
        }
        record.preInfo = info;
        record.flags |= InfoRecord.FLAG_PRE;
    }

    boolean isDisappearing(ViewHolder holder) {
        final InfoRecord record = mLayoutHolderMap.get(holder);
        return record != null && ((record.flags & InfoRecord.FLAG_DISAPPEARED) != 0);
    }

    /**
     * Finds the ItemHolderInfo for the given ViewHolder in preLayout list and removes it.
     *
     * @param vh The ViewHolder whose information is being queried
     * @return The ItemHolderInfo for the given ViewHolder or null if it does not exist
     */
    @Nullable
    ItemHolderInfo popFromPreLayout(ViewHolder vh) {
        return popFromLayoutStep(vh, InfoRecord.FLAG_PRE);
    }

    /**
     * Finds the ItemHolderInfo for the given ViewHolder in postLayout list and removes it.
     *
     * @param vh The ViewHolder whose information is being queried
     * @return The ItemHolderInfo for the given ViewHolder or null if it does not exist
     */
    @Nullable
    ItemHolderInfo popFromPostLayout(ViewHolder vh) {
        return popFromLayoutStep(vh, InfoRecord.FLAG_POST);
    }

    private ItemHolderInfo popFromLayoutStep(ViewHolder vh, int flag) {
        int index = mLayoutHolderMap.indexOfKey(vh);
        if (index < 0) {
            return null;
        }
        final InfoRecord record = mLayoutHolderMap.valueAt(index);
        if (record != null && (record.flags & flag) != 0) {
            record.flags &= ~flag;
            final ItemHolderInfo info;
            if (flag == InfoRecord.FLAG_PRE) {
                info = record.preInfo;
            } else if (flag == InfoRecord.FLAG_POST) {
                info = record.postInfo;
            } else {
                throw new IllegalArgumentException("Must provide flag PRE or POST");
            }
            // if not pre-post flag is left, clear.
            if ((record.flags & (InfoRecord.FLAG_PRE | InfoRecord.FLAG_POST)) == 0) {
                mLayoutHolderMap.removeAt(index);
                InfoRecord.recycle(record);
            }
            return info;
        }
        return null;
    }

    /**
     * Adds the given ViewHolder to the oldChangeHolders list
     * @param key The key to identify the ViewHolder.
     * @param holder The ViewHolder to store
     */
    void addToOldChangeHolders(long key, ViewHolder holder) {
        mOldChangedHolders.put(key, holder);
    }

    /**
     * Adds the given ViewHolder to the onAppeared in pre layout list. These are Views added by the
     * LayoutManager during a pre-layout pass. We distinguish them from other views that were
     * already in the pre-layout so that ItemAnimator can choose to run a different animation for
     * them.
     *
     * @param holder The ViewHolder to store
     * @param info The information to save
     */
    void addToAppearedInPreLayoutHolders(ViewHolder holder, ItemHolderInfo info) {
        InfoRecord record = mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            mLayoutHolderMap.put(holder, record);
        }
        record.flags |= InfoRecord.FLAG_APPEAR;
        record.preInfo = info;
    }

    /**
     * Checks whether the given ViewHolder is in preLayout list
     * @param viewHolder The ViewHolder to query
     *
     * @return True if the ViewHolder is pushViewController in preLayout, false otherwise
     */
    boolean isInPreLayout(ViewHolder viewHolder) {
        final InfoRecord record = mLayoutHolderMap.get(viewHolder);
        return record != null && (record.flags & InfoRecord.FLAG_PRE) != 0;
    }

    /**
     * Queries the oldChangeHolder list for the given key. If they are not tracked, simply returns
     * null.
     * @param key The key to be used to find the ViewHolder.
     *
     * @return A ViewHolder if exists or null if it does not exist.
     */
    ViewHolder getFromOldChangeHolders(long key) {
        return mOldChangedHolders.get(key);
    }

    /**
     * Adds the item information to the post layout list
     * @param holder The ViewHolder whose information is being saved
     * @param info The information to save
     */
    void addToPostLayout(ViewHolder holder, ItemHolderInfo info) {
        InfoRecord record = mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            mLayoutHolderMap.put(holder, record);
        }
        record.postInfo = info;
        record.flags |= InfoRecord.FLAG_POST;
    }

    /**
     * A ViewHolder might be added by the LayoutManager just to animate its disappearance.
     * This list holds such items so that we can animate / recycle these ViewHolders properly.
     *
     * @param holder The ViewHolder which disappeared during a layout.
     */
    void addToDisappearedInLayout(ViewHolder holder) {
        InfoRecord record = mLayoutHolderMap.get(holder);
        if (record == null) {
            record = InfoRecord.obtain();
            mLayoutHolderMap.put(holder, record);
        }
        record.flags |= InfoRecord.FLAG_DISAPPEARED;
    }

    /**
     * Removes a ViewHolder from disappearing list.
     * @param holder The ViewHolder to be removed from the disappearing list.
     */
    void removeFromDisappearedInLayout(ViewHolder holder) {
        InfoRecord record = mLayoutHolderMap.get(holder);
        if (record == null) {
            return;
        }
        record.flags &= ~InfoRecord.FLAG_DISAPPEARED;
    }

    void process(ProcessCallback callback) {
        for (int index = mLayoutHolderMap.size() - 1; index >= 0; index --) {
            final ViewHolder viewHolder = mLayoutHolderMap.keyAt(index);
            final InfoRecord record = mLayoutHolderMap.removeAt(index);
            if ((record.flags & InfoRecord.FLAG_APPEAR_AND_DISAPPEAR) == InfoRecord.FLAG_APPEAR_AND_DISAPPEAR) {
                // Appeared then disappeared. Not useful for animations.
                callback.unused(viewHolder);
            } else if ((record.flags & InfoRecord.FLAG_DISAPPEARED) != 0) {
                // Set as "disappeared" by the LayoutManager (addDisappearingView)
                if (record.preInfo == null) {
                    // similar to appear disappear but happened between different layout passes.
                    // this can happen when the layout manager is using auto-measure
                    callback.unused(viewHolder);
                } else {
                    callback.processDisappeared(viewHolder, record.preInfo, record.postInfo);
                }
            } else if ((record.flags & InfoRecord.FLAG_APPEAR_PRE_AND_POST) == InfoRecord.FLAG_APPEAR_PRE_AND_POST) {
                // Appeared in the layout but not in the adapter (e.g. entered the viewport)
                callback.processAppeared(viewHolder, record.preInfo, record.postInfo);
            } else if ((record.flags & InfoRecord.FLAG_PRE_AND_POST) == InfoRecord.FLAG_PRE_AND_POST) {
                // Persistent in both passes. Animate persistence
                callback.processPersistent(viewHolder, record.preInfo, record.postInfo);
            } else if ((record.flags & InfoRecord.FLAG_PRE) != 0) {
                // Was in pre-layout, never been added to post layout
                callback.processDisappeared(viewHolder, record.preInfo, null);
            } else if ((record.flags & InfoRecord.FLAG_POST) != 0) {
                // Was not in pre-layout, been added to post layout
                callback.processAppeared(viewHolder, record.preInfo, record.postInfo);
            } else if ((record.flags & InfoRecord.FLAG_APPEAR) != 0) {
                // Scrap view. RecyclerView will handle removing/recycling this.
            } else if (DEBUG) {
                throw new IllegalStateException("record without any reasonable flag combination:/");
            }
            InfoRecord.recycle(record);
        }
    }

    /**
     * Removes the ViewHolder from all list
     * @param holder The ViewHolder which we should stop tracking
     */
    void removeViewHolder(ViewHolder holder) {
        for (int i = mOldChangedHolders.size() - 1; i >= 0; i--) {
            if (holder == mOldChangedHolders.valueAt(i)) {
                mOldChangedHolders.removeAt(i);
                break;
            }
        }
        final InfoRecord info = mLayoutHolderMap.remove(holder);
        if (info != null) {
            InfoRecord.recycle(info);
        }
    }

    void onDetach() {
        InfoRecord.drainCache();
    }

    public void onViewDetached(ViewHolder viewHolder) {
        removeFromDisappearedInLayout(viewHolder);
    }

    interface ProcessCallback {
        void processDisappeared(ViewHolder viewHolder, @NonNull ItemHolderInfo preInfo,
                                @Nullable ItemHolderInfo postInfo);
        void processAppeared(ViewHolder viewHolder, @Nullable ItemHolderInfo preInfo,
                             ItemHolderInfo postInfo);
        void processPersistent(ViewHolder viewHolder, @NonNull ItemHolderInfo preInfo,
                               @NonNull ItemHolderInfo postInfo);
        void unused(ViewHolder holder);
    }

    static class InfoRecord {
        // disappearing list
        static final int FLAG_DISAPPEARED = 1;
        // appear in pre layout list
        static final int FLAG_APPEAR = 1 << 1;
        // pre layout, this is necessary to distinguish null item info
        static final int FLAG_PRE = 1 << 2;
        // post layout, this is necessary to distinguish null item info
        static final int FLAG_POST = 1 << 3;
        static final int FLAG_APPEAR_AND_DISAPPEAR = FLAG_APPEAR | FLAG_DISAPPEARED;
        static final int FLAG_PRE_AND_POST = FLAG_PRE | FLAG_POST;
        static final int FLAG_APPEAR_PRE_AND_POST = FLAG_APPEAR | FLAG_PRE | FLAG_POST;
        int flags;
        @Nullable
        ItemHolderInfo preInfo;
        @Nullable
        ItemHolderInfo postInfo;
        static Pools.Pool<InfoRecord> sPool = new Pools.SimplePool<>(20);

        private InfoRecord() {
        }

        static InfoRecord obtain() {
            InfoRecord record = sPool.acquire();
            return record == null ? new InfoRecord() : record;
        }

        static void recycle(InfoRecord record) {
            record.flags = 0;
            record.preInfo = null;
            record.postInfo = null;
            sPool.release(record);
        }

        static void drainCache() {
            //noinspection StatementWithEmptyBody
            while (sPool.acquire() != null);
        }
    }
}
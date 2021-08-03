package kz.citicom.uikit.tools.recycler;

import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

/**
 * Created by donik102 on 20.05.2018.
 */

public class RecyclerViewAccessibilityDelegate extends AccessibilityDelegateCompat {
    final RecyclerView mRecyclerView;


    public RecyclerViewAccessibilityDelegate(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    boolean shouldIgnore() {
        return mRecyclerView.hasPendingAdapterUpdates();
    }

    @Override
    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (super.performAccessibilityAction(host, action, args)) {
            return true;
        }
        if (!shouldIgnore() && mRecyclerView.getLayoutManager() != null) {
            return mRecyclerView.getLayoutManager().performAccessibilityAction(action, args);
        }

        return false;
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        info.setClassName(RecyclerView.class.getName());
        if (!shouldIgnore() && mRecyclerView.getLayoutManager() != null) {
            mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfo(info);
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(host, event);
        event.setClassName(RecyclerView.class.getName());
        if (host instanceof RecyclerView && !shouldIgnore()) {
            RecyclerView rv = (RecyclerView) host;
            if (rv.getLayoutManager() != null) {
                rv.getLayoutManager().onInitializeAccessibilityEvent(event);
            }
        }
    }

    /**
     * Gets the AccessibilityDelegate for an individual item in the RecyclerView.
     * A basic item delegate is provided by default, but you can override this
     * method to provide a custom per-item delegate.
     */
    public AccessibilityDelegateCompat getItemDelegate() {
        return mItemDelegate;
    }

    final AccessibilityDelegateCompat mItemDelegate = new AccessibilityDelegateCompat() {
        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            if (!shouldIgnore() && mRecyclerView.getLayoutManager() != null) {
                mRecyclerView.getLayoutManager().
                        onInitializeAccessibilityNodeInfoForItem(host, info);
            }
        }

        @Override
        public boolean performAccessibilityAction(View host, int action, Bundle args) {
            if (super.performAccessibilityAction(host, action, args)) {
                return true;
            }
            if (!shouldIgnore() && mRecyclerView.getLayoutManager() != null) {
                return mRecyclerView.getLayoutManager().
                        performAccessibilityActionForItem(host, action, args);
            }
            return false;
        }
    };
}
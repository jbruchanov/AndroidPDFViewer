package com.scurab.android.pdf2jpg;

import java.util.LinkedList;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * An extended PagerAdapter that recycles views
 */
public abstract class RecyclePagerAdapter<T extends View> extends PagerAdapter {

    private LinkedList<T> mViews;
    private int mCurrentIndex;
    private T mCurrentView;

    public RecyclePagerAdapter(){
        mViews = new LinkedList<T>();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public T instantiateItem(ViewGroup container, int position) {
        T v = getView(position, mViews.poll(), container);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        @SuppressWarnings("unchecked")
        T v = (T)object;
        removeFromParent(v);
        mViews.addLast(v);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentIndex = position;
        mCurrentView = (T)object;
    }

    private void removeFromParent(View v){
        ViewGroup vg = (ViewGroup) v.getParent();
        vg.removeView(v);
    }

    public abstract T getView(int position, T convertView, View container);

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    public T getCurrentView() {
        return mCurrentView;
    }
}
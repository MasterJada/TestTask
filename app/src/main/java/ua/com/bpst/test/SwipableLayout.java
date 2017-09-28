package ua.com.bpst.test;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by olegreksa on 28.09.17.
 */

public class SwipableLayout extends FrameLayout {
   private ArrayList<ImageModel> items = new ArrayList<>();

    private int offset = 0;
    public SwipableLayout(@NonNull Context context) {
        super(context);
    }

    public SwipableLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipableLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Load items and remove old
     * @param items
     */
    public void updateItems(ArrayList<ImageModel> items) {
        this.items = items;
        if (this.items.size() > 3){
            offset = this.items.size() - 3;
        }
        populateItems();
    }

    /**
     * Add items into view
     * @param items
     */
    public void appendItemms(ArrayList<ImageModel> items){
        this.items.addAll(items);
        if (this.items.size() > 3){
            offset = this.items.size() - 3;
        }
        populateItems();
    }

    public int getItemsCount(){
        return items.size();
    }

    public SwipeCallback callback = null;


    private void populateItems() {
        removeAllViews();
        for (int i = offset; i < items.size(); i++) {
            View view = LayoutInflater.from(this.getContext()).inflate(R.layout.image_item, this, false);
            Picasso.with(this.getContext())
                    .load(items.get(i).url)
                    .resize(600, 600)
                    .into((ImageView) view.findViewById(R.id.iv_image));
            view.setOnTouchListener(onTouchListener);
            view.setTag(i);
            addView(view);
        }
    }

    float oldX = 0, oldY = 0, startX = 0;
    OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                oldX = v.getX() - event.getRawX();
                oldY = event.getRawY();
                startX = v.getX();
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float diff = event.getRawX() + oldX;
                if(callback != null){
                    callback.onMove(diff, v);
                }
                v.setX(diff);
                v.setRotation(diff / 30);
                float scaleFactor = Math.max(1 - Math.abs(diff) / v.getWidth(), 0.8F);
                v.setScaleX(scaleFactor);
                v.setScaleY(scaleFactor);

                if (Math.abs(diff) > v.getWidth() / 2) {
                    items.remove((int)v.getTag());
                    if (diff > 0) {
                        if (callback != null) {
                            callback.onSwipedRight(v);
                        }
                    } else {
                        if (callback != null) {
                            callback.onSwipedLeft(v);
                        }
                    }
                    offset --;
                    if (offset < 0){
                        offset = 0;
                    }
                    removeView(v);
                    populateItems();
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float diff = event.getRawX() + oldX;
                if (Math.abs(diff) < getWidth() / 2) {
                    v.setX(startX);
                    v.setRotation(0);
                    v.setScaleX(1);
                    v.setScaleY(1);
                    if(callback != null) callback.onCancel(v);
                }
            }

            if(event.getAction() == MotionEvent.ACTION_CANCEL){
                float diff = event.getRawX() + oldX;
                if (Math.abs(diff) < getWidth() / 2) {
                    v.setX(startX);
                    v.setRotation(0);
                    v.setScaleX(1);
                    v.setScaleY(1);
                    if(callback != null) callback.onCancel(v);
                }
            }


            return true;
        }
    };


    interface SwipeCallback {
        void onMove(float distance, View view);
        void onSwipedRight(View view);
        void onSwipedLeft(View view);
        void onCancel(View view);
    }

}

package ua.com.bpst.test;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    SwipableLayout swipable;
    ArrayList<ImageModel> items = new ArrayList<>();
    TextView tv_likes, tv_dislike, tv_total_count;
    EditText et_search;
    int like = 0, dislike = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_likes = (TextView) findViewById(R.id.tv_likes);
        tv_dislike = (TextView) findViewById(R.id.tv_dislikes);
        swipable = (SwipableLayout) findViewById(R.id.swipable);
        swipable.updateItems(items);
        swipable.callback = swipeCallback;
        et_search = (EditText) findViewById(R.id.et_search);
        findViewById(R.id.bt_search).setOnClickListener(onSearchClick);
        tv_total_count = (TextView)findViewById(R.id.tv_total_count);
        tv_total_count.setText("Total count: "+String.valueOf(swipable.getItemsCount()));
        et_search.setOnEditorActionListener(editorActionListener);
    }

    SwipableLayout.SwipeCallback swipeCallback = new SwipableLayout.SwipeCallback() {
        @Override
        public void onMove(float distance, View view) {
            ConstraintLayout parent = (ConstraintLayout) view.findViewById(R.id.parent);
            ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);

            if (distance > 0) {
                parent.setBackgroundColor(getResources().getColor(R.color.blue));
                view.setAlpha(1 - (distance / iv_image.getWidth()));
            }
            if (distance < 0) {
                parent.setBackgroundColor(getResources().getColor(R.color.red));
                view.setAlpha(1 + (distance / iv_image.getWidth()));
            }
            tv_total_count.setText("Total count: "+String.valueOf(swipable.getItemsCount()));
        }

        @Override
        public void onSwipedRight(View v) {
            like++;
            tv_likes.setText(getResources().getString(R.string.likes_counter, like));
            tv_total_count.setText("Total count: "+String.valueOf(swipable.getItemsCount()));
        }

        @Override
        public void onSwipedLeft(View v) {
            dislike++;
            tv_dislike.setText(getResources().getString(R.string.dislikes_counter, dislike));
            tv_total_count.setText("Total count: "+String.valueOf(swipable.getItemsCount()));
        }

        @Override
        public void onCancel(View view) {
            ConstraintLayout parent = (ConstraintLayout) view.findViewById(R.id.parent);
            ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);
            parent.setBackgroundColor(Color.TRANSPARENT);
            view.setAlpha(1.0F);

        }
    };


    TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchClick.onClick(v);
                return true;
            }
            return false;
        }
    };


    View.OnClickListener onSearchClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            InputMethodManager in = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading data");
            progressDialog.show();

            ApiHelper.getInstance().searchImages(et_search.getText().toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<ResponseModel>() {
                        @Override
                        public void accept(ResponseModel responseModel) throws Exception {

                            if (responseModel.images.size() == 0) {
                                Toast.makeText(MainActivity.this, "Nothing founded", Toast.LENGTH_SHORT).show();
                            }
                            swipable.appendItemms(responseModel.images);
                            progressDialog.dismiss();
                            tv_total_count.setText("Total count: "+String.valueOf(swipable.getItemsCount()));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                          Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
//
        }
    };


}

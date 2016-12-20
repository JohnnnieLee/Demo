package com.ccstudio.demo;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String GET_PRODUCT_URL = "https://s3.amazonaws.com/carousell/static/android/product";
    private static final String PRODUCT_FIELD_NAME = "product";
    private static final String PRODUCT_NAME = "name";
    private static final String PRODUCT_IMAGE_URL = "image_url";
    private static final String GET_MESSAGES = "https://s3.amazonaws.com/carousell/static/android/chat";
    private static final String CHAT = "chats";
    private static final String TIME_STAMP = "timestamp";
    private static final String MESSAGE = "message";
    private static final String TYPE = "type";
    private static final String OFFER = "offer";
    private static final String BUYER_NAME = "buyer_name";
    private static final String BUYER_IMAGE_URL = "buyer_image_url";
    private static final String SELLER_NAME = "seller_name";
    private static final String SELLER_IMAGE_URL = "seller_image_url";

    public final static DisplayImageOptions DISPLAY_IMAGE_OPTIONS =
            new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();

    private final SimpleDateFormat mDateTimeFormatter = new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault());

    private RequestQueue mVolleyQueue;

    private ActionBar mActionBar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MessageFlowAdapter mAdapter;
    private View mEnterBtn;
    private EditText mInputField;

    // TODO: Apply MVP pattern, this should not be here
    private String mCurrentUser = "";
    private String mCurrentAvatarUrl = "";
    private int mCurrentUserType = MessageData.USER_TYPE_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVolley();
        initImageLoader();
        initView();

        queryProductInfo();
        queryMessages();
    }

    private void initImageLoader() {
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(this);
        builder.tasksProcessingOrder(QueueProcessingType.FIFO);
        ImageLoader.getInstance().init(builder.build());
    }

    private void initVolley() {
        mVolleyQueue = Volley.newRequestQueue(this);
        mVolleyQueue.start();
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.message_flow);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new MessageFlowAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mEnterBtn = findViewById(R.id.enter);
        mEnterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mInputField.getText().toString());
                mInputField.setText("");
            }
        });
        mInputField = (EditText) findViewById(R.id.input);
        mInputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            sendMessage(mInputField.getText().toString());
                            mInputField.setText("");
                            return true;
                    }
                }
                return false;
            }
        });
    }

    private void sendMessage(String msg) {
        MessageData data = new MessageData(mCurrentUser, msg,
                mDateTimeFormatter.format(new Date()), mCurrentAvatarUrl, mCurrentUserType);
        mAdapter.appendData(data);
        mLinearLayoutManager.scrollToPosition(mAdapter.getItemCount()-1);
    }

    private void queryProductInfo() {
        JsonObjectRequest requestProductInfo = new JsonObjectRequest
                (Request.Method.GET, GET_PRODUCT_URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject product = response.getJSONObject(PRODUCT_FIELD_NAME);
                            String productName = product.getString(PRODUCT_NAME);
                            String productImgUrl = product.getString(PRODUCT_IMAGE_URL);
                            updateActionBar(productName, productImgUrl);
                        } catch (JSONException e) {
                            Log.i(TAG, "Parsing product fail", e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Get product info fail: "+error.getMessage());
                    }
                });
        mVolleyQueue.add(requestProductInfo);
    }

    private void updateActionBar(String productName, String productImgUrl) {
        mActionBar.setTitle(productName);
        ImageLoader.getInstance().loadImage(productImgUrl, new ImageSize(48, 48), DISPLAY_IMAGE_OPTIONS, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                RoundedBitmapDrawable logo =
                        RoundedBitmapDrawableFactory.create(getResources(), loadedImage);
                logo.setCircular(true);

                mActionBar.setDisplayUseLogoEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
                mActionBar.setLogo(logo);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    private void queryMessages() {
        JsonObjectRequest requestProductInfo = new JsonObjectRequest
                (Request.Method.GET, GET_MESSAGES, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject offer = response.getJSONObject(OFFER);
                            String buyerName = offer.getString(BUYER_NAME);
                            String buyImageUrl = offer.getString(BUYER_IMAGE_URL);
                            String sellerName = offer.getString(SELLER_NAME);
                            String sellerImageUrl = offer.getString(SELLER_IMAGE_URL);

                            JSONArray chats = response.getJSONArray(CHAT);
                            ArrayList<MessageData> messageData = new ArrayList<>(chats.length());
                            for (int i = 0; i < chats.length(); i++) {
                                JSONObject obj = chats.getJSONObject(i);
                                String timeStamp = "";
                                try {
                                    Date date = ISO8601Utils.parse(obj.getString(TIME_STAMP), new ParsePosition(0));
                                    timeStamp = mDateTimeFormatter.format(date);
                                } catch (ParseException e) {
                                    Log.d(TAG, "Parse timestmp fail", e);
                                }
                                String message = obj.getString(MESSAGE);
                                String type = obj.getString(TYPE);

                                String username;
                                String avatarUrl;
                                @MessageData.UserType int userType;
                                switch (type) {
                                    case "b":
                                        username = buyerName;
                                        avatarUrl = buyImageUrl;
                                        userType = MessageData.USER_TYPE_BUYER;
                                        break;
                                    case "s":
                                        username = sellerName;
                                        avatarUrl = sellerImageUrl;
                                        userType = MessageData.USER_TYPE_SELLER;

                                        // Current user data should not get from this api...
                                        mCurrentUser = username;
                                        mCurrentAvatarUrl = avatarUrl;
                                        mCurrentUserType = MessageData.USER_TYPE_SELLER;
                                        break;
                                    default:
                                        Log.i(TAG, "Invalid user type= "+type);
                                        continue;
                                }
                                messageData.add(new MessageData(username, message, timeStamp, avatarUrl, userType));
                            }
                            mAdapter.setData(messageData);
                        } catch (JSONException e) {
                            Log.i(TAG, "Parsing product fail", e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Get product info fail: "+error.getMessage());
                    }
                });
        mVolleyQueue.add(requestProductInfo);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mVolleyQueue.stop();
        ImageLoader.getInstance().destroy();
    }
}

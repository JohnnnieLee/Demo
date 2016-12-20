package com.ccstudio.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IHandle {
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

    private final RequestQueue mVolleyQueue = Volley.newRequestQueue(this);
    private RecyclerView mRecyclerView;
    private MessageFlowAdapter mAdapter;

    // TODO: Move to other place
    private String mBuyerName;
    private String mBuyImageUrl;
    private String mSellerName;
    private String mSellerImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        queryProductInfo();
        queryMessages();
        mVolleyQueue.start();
    }

    private void initView() {
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.message_flow);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageFlowAdapter(this, this); // TODO: Consider to remove handle and store data in MesssageData
        mRecyclerView.setAdapter(mAdapter);
    }

    private void queryProductInfo() {
        JsonObjectRequest requestProductInfo = new JsonObjectRequest
                (Request.Method.GET, GET_PRODUCT_URL, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject product = response.getJSONObject(PRODUCT_FIELD_NAME);
                            String name = product.getString(PRODUCT_NAME);
                            String imageUrl = product.getString(PRODUCT_IMAGE_URL);
                            // TODO: Update toolbar here
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

    private void queryMessages() {
        JsonObjectRequest requestProductInfo = new JsonObjectRequest
                (Request.Method.GET, GET_MESSAGES, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray chats = response.getJSONArray(CHAT);
                            ArrayList<MessageData> messageData = new ArrayList<>(chats.length());
                            for (int i = 0; i < chats.length(); i++) {
                                JSONObject obj = chats.getJSONObject(i);
                                String timeStamp = obj.getString(TIME_STAMP);
                                String message = obj.getString(MESSAGE);
                                String type = obj.getString(TYPE);

                                messageData.add(new MessageData(message, timeStamp, type.equals("b") ? MessageData.USER_TYPE_BUYER : MessageData.USER_TYPE_SELLER));
                            }
                            mAdapter.setData(messageData);

                            JSONObject offer = response.getJSONObject(OFFER);
                            mBuyerName = response.getString(BUYER_NAME);
                            mBuyImageUrl = response.getString(BUYER_IMAGE_URL);
                            mSellerName = response.getString(SELLER_NAME);
                            mSellerImageUrl = response.getString(SELLER_IMAGE_URL);
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
    protected void onDestroy() {
        super.onDestroy();

        mVolleyQueue.stop();
    }

    @Override
    public String getBuyerName() {
        return mBuyerName;
    }

    @Override
    public String getBuyerImgUrl() {
        return mBuyImageUrl;
    }

    @Override
    public String getSellerName() {
        return mSellerName;
    }

    @Override
    public String getSellerImgeUrl() {
        return mSellerImageUrl;
    }
}

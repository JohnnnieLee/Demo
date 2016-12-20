package com.ccstudio.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Johnny on 2016/12/20.
 */

public class MessageFlowAdapter extends RecyclerView.Adapter<MessageFlowAdapter.MessageViewHolder> {
    private static final String TAG = MessageViewHolder.class.getSimpleName();

    private static final int VIEWTYPE_UNKNOWN = 0;
    private static final int VIEWTYPE_LEFT = 1;
    private static final int VIEWTYPE_RIGHT = 2;

    // TODO: Consider to use dagger here
    private final LayoutInflater mInflater;
    private final ImageLoader mImageLoader;
    private ArrayList<MessageData> mMessages;
    public MessageFlowAdapter(final Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoader = ImageLoader.getInstance();
        mMessages = new ArrayList<>();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName;
        TextView mMessage;
        TextView mDate;
        ImageView mAvatar;
        public MessageViewHolder(View itemView) {
            super(itemView);

            mUserName = (TextView)itemView.findViewById(R.id.user_name);
            mMessage = (TextView)itemView.findViewById(R.id.message_text);
            mDate = (TextView)itemView.findViewById(R.id.date);
            mAvatar = (ImageView)itemView.findViewById(R.id.avatar);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEWTYPE_LEFT:
            {
                View v = mInflater.inflate(R.layout.message_layout_left, null);
                return new MessageViewHolder(v);
            }
            case VIEWTYPE_RIGHT:
            {
                View v = mInflater.inflate(R.layout.message_layout_right, null);
                return new MessageViewHolder(v);
            }
            default:
                Log.i(TAG, "Unknown viewtype= "+viewType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageData data = mMessages.get(position);
        holder.mMessage.setText(data.message);
        holder.mDate.setText(data.date);
        holder.mUserName.setText(data.name);
        mImageLoader.displayImage(data.avatarUrl, holder.mAvatar);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageData data = mMessages.get(position);
        switch (data.userType) {
            case MessageData.USER_TYPE_BUYER:
                return VIEWTYPE_LEFT;
            case MessageData.USER_TYPE_SELLER:
                return VIEWTYPE_RIGHT;
        }
        return VIEWTYPE_UNKNOWN;
    }

    public void setData(ArrayList<MessageData> data) {
        mMessages = data;
        notifyDataSetChanged();
    }

    public void appendData(MessageData data) {
        mMessages.add(data);
        notifyItemInserted(mMessages.size()-1);
    }
}

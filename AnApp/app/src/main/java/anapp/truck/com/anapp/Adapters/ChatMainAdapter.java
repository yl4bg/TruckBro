package anapp.truck.com.anapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anapp.truck.com.anapp.chatDataClasses.ChatData;
import anapp.truck.com.anapp.R;


/**
 * Created by Anna on 6/17/15.
 */
public class ChatMainAdapter extends BaseAdapter{
    private Context mContext;
    private List<ChatData> list;

    private ImageView mUserPicView;
    private TextView mUserNameView;
    private TextView mUserMSGView;
    private TextView mLastTimeView;
    private View mChatListView;
    private ImageView isLockedView;
    private ImageView isMutedView;
    private TextView isReadView;
    private RelativeLayout mChatCell;

    public ChatMainAdapter(Context context, List<ChatData> list) {

        this.mContext = context;
        this.list = list;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mChatListView = inflater.inflate(R.layout.chat_main_list_cell, null);
        mChatCell = (RelativeLayout) mChatListView.findViewById(R.id.chat_main_list_cell_relative);

        ChatData chatData = list.get(position);

        if (chatData.isPinned())
            mChatCell.setBackgroundColor(mContext.getResources().getColor(R.color.pinned_bg_color));;

        mUserPicView = (ImageView) mChatListView.findViewById(R.id.chat_main_list_user_pic);
        mUserPicView.setImageResource(chatData.getPicId());


        mUserMSGView = (TextView) mChatListView.findViewById(R.id.chat_main_list_last_msg);
        mUserMSGView.setText(chatData.getMsg());

        mUserNameView = (TextView) mChatListView.findViewById(R.id.chat_main_list_user_name);
        String userName = chatData.getName();
        mUserNameView.setText(userName);

//        TODO 这里check是不是求助信号然后设文字的颜色
        if (userName.equals("求助信号")) {
            mUserMSGView.setTextColor(mContext.getResources().getColor(R.color.sos_text_color));
            mUserNameView.setTextColor(mContext.getResources().getColor(R.color.sos_text_color));
        }


        mLastTimeView = (TextView) mChatListView.findViewById(R.id.chat_main_list_last_time);
        mLastTimeView.setText(chatData.getTime());

//        设置小锁和小喇叭的显示
        if (chatData.isLocked()) {
            isLockedView = (ImageView) mChatListView.findViewById(R.id.chat_main_list_locked);
            isLockedView.setVisibility(View.VISIBLE);
        }

        if (chatData.isMuted()) {
            isMutedView = (ImageView) mChatListView.findViewById(R.id.chat_main_list_muted);
            isMutedView.setVisibility(View.VISIBLE);
        }

        if (chatData.isRead()) {
            isReadView = (TextView) mChatListView.findViewById(R.id.chat_main_list_unread_textview);
            if(chatData.getUnreadCount().equals("0")){
                isReadView.setVisibility(View.INVISIBLE);
            } else {
                isReadView.setVisibility(View.VISIBLE);
                isReadView.setText(chatData.getUnreadCount());
            }
        }
        return mChatListView;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}

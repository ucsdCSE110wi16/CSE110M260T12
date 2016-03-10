package com.cards.flash.testez;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.List;

public class ShareAdapter extends ArrayAdapter<ParseUser> {
    private OnShareListener listener;
    private List<ParseUser> contentList;

    public ShareAdapter(Context context, List<ParseUser> contentList, OnShareListener listener) {
        super(context, R.layout.item_share, contentList);
        this.listener = listener;
        this.contentList = contentList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            //creating new view if view already exist - reusing old view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_share, null);
            convertView.setTag(new ViewHolder(convertView));
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        ParseObject user = getItem(position);
        viewHolder.email.setText(user.getString("email"));
        viewHolder.name.setText(user.getString("name"));

        ParseUser userInVariableList = getItem(position);
        viewHolder.shareButton.setTag(user.getString("name"));
        if (ShareActivity.getBaseUserList().contains(userInVariableList)){
            viewHolder.shareButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_unshare));
            viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUnshare(getItem(position));
                }
            });
        }else{
            viewHolder.shareButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_share));
            viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShare(getItem(position));
                }
            });
        }

        return convertView;
    }
    public void updateData(List<ParseUser> data){
        contentList = data;
    }
    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public ParseUser getItem(int position) {
        return contentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private static class ViewHolder {
        TextView name, email;
        ImageView shareButton;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            email = (TextView) view.findViewById(R.id.email);
            shareButton = (ImageView) view.findViewById(R.id.share_button);

        }
    }

    public interface OnShareListener {
        void onShare(ParseUser user);
        void onUnshare(ParseUser user);
    }
}

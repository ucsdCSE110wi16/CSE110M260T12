package com.cards.flash.testez.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cards.flash.testez.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class ShareAdapter extends ArrayAdapter<ParseUser> {
    private OnShareListener listener;
    private ParseObject category;
    private List<Boolean> isShared;

    public ShareAdapter(Context context, List<ParseUser> contentList, ParseObject category, OnShareListener listener, List<Boolean> isShared) {
        super(context, R.layout.item_share, contentList);
        this.listener = listener;
        this.category = category;
        this.isShared = isShared;
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
        boolean breaked = false;
        if (isShared.get(position)) {
            ((ImageView) viewHolder.shareButton.findViewById(R.id.share_button)).setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_unshare));
            final View finalConvertView = convertView;
            viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUnshare(getItem(position), finalConvertView);
                }
            });
        } else {
            ((ImageView) viewHolder.shareButton.findViewById(R.id.share_button)).setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_share));
            final View finalConvertView1 = convertView;
            viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShare(getItem(position), finalConvertView1);
                }
            });
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name, email;
        View shareButton;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            email = (TextView) view.findViewById(R.id.email);
            shareButton = view.findViewById(R.id.share_button);
        }
    }

    public interface OnShareListener {
        public void onShare(ParseUser user, View view);

        public void onUnshare(ParseUser user, View view);
    }
}

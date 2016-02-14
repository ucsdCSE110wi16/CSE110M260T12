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
    private List<ParseObject> sharedList;

    public ShareAdapter(Context context, List<ParseUser> contentList, List<ParseObject> sharedList, OnShareListener listener) {
        super(context, R.layout.item_share, contentList);
        this.listener = listener;
        this.sharedList = sharedList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            //creating new view if view already exist - reusing old view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_share, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        ParseObject user = getItem(position);
        viewHolder.email.setText(user.getString("email"));
        viewHolder.name.setText(user.getString("name"));
        ((ImageView) viewHolder.shareButton.findViewById(R.id.share_button)).setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_share));
        boolean breaked = false;
        if (sharedList != null)
            for (int i = 0; i < sharedList.size(); i++) {
                if (sharedList.get(i).getString("user_id").equalsIgnoreCase(getItem(position).getObjectId())) {
                    ((ImageView) viewHolder.shareButton.findViewById(R.id.share_button)).setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_unshare));
                    breaked = true;
                    break;
                }
            }
        final View finalConvertView = convertView;
        if (breaked) {
            viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onUnshare(getItem(position), finalConvertView);
                }
            });
        } else {
            viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onShare(getItem(position), finalConvertView);
                }
            });
        }
        return convertView;
    }

    public void updateShareList(List<ParseObject> sharedList) {
        this.sharedList = sharedList;
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
        public void onShare(ParseObject user, View view);

        public void onUnshare(ParseObject user, View view);
    }
}

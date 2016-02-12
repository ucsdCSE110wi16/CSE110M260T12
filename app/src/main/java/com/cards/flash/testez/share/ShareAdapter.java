package com.cards.flash.testez.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cards.flash.testez.R;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class ShareAdapter extends ArrayAdapter<ParseUser> {
    private OnShareListener listener;
    private List<ParseObject> sharedList;
    public ShareAdapter(Context context, List<ParseUser> contentList, List<ParseObject> sharedList,OnShareListener listener) {
        super(context, R.layout.item_share,contentList);
        this.listener = listener;
        this.sharedList = sharedList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            //creating new view if view already exist - reusing old view
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_share,null);
            convertView.setTag(new ViewHolder(convertView));
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        ParseObject user = getItem(position);
        viewHolder.email.setText(user.getString("email"));
        viewHolder.name.setText(user.getString("name"));
        viewHolder.shareButton.setVisibility(View.VISIBLE);
        for(int i = 0; i<sharedList.size(); i++){
            if(sharedList.get(i).getString("user_id").equals(getItem(position).getObjectId()))
                viewHolder.shareButton.setVisibility(View.GONE);
        }
        viewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShare(getItem(position));
            }
        });
        return convertView;
    }

    private static class ViewHolder{
        TextView name, email;
        View shareButton;
        public ViewHolder(View view){
            name = (TextView) view.findViewById(R.id.name);
            email = (TextView) view.findViewById(R.id.email);
            shareButton = view.findViewById(R.id.share_button);
        }
    }

    public interface OnShareListener {
        public void onShare(ParseObject user);
    }
}

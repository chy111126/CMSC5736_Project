package cuhk.cse.cmsc5736project.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.models.Friend;

/**
 * Created by TCC on 12/10/2017.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ItemVH> {

    //  Data
    static public ArrayList<Friend> friendList = new ArrayList<>();

    String[] nameArray = {"Friend 1", "Friend 2", "Friend 3", "Friend 4", "Friend 5", "Friend 6", "Friend 7", "Friend 8", "Friend 9", "Friend 10"};
    String[] descArray = {"Male/Female toilet", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!"};


    private Context context;

    public FriendListAdapter(Context context, boolean isAddNewFriend) {
        this.context = context;
        if (isAddNewFriend) {
            populateSampleData();
        } else {
            // TODO: Replace with list of proximate friends (that equipped with the same app)
        }
    }

    private void populateSampleData() {
        // Populate sample friend list
        for (int i = 0; i < nameArray.length; i++) {
            Friend friend = new Friend(
                    nameArray[i],
                    descArray[i],
                    ""
            );
            friendList.add(friend);
        }
    }

    @Override
    public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);

        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(ItemVH holder, int position) {
        Friend item = friendList.get(position);

        holder.txtTitle.setText(item.getName());
        holder.txtDesc.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return friendList != null ? friendList.size() : 0;
    }

    protected static class ItemVH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc;

        public ItemVH(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.item_friend_name);
            txtDesc = (TextView) itemView.findViewById(R.id.item_friend_desc);
        }
    }




}

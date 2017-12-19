package cuhk.cse.cmsc5736project.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cuhk.cse.cmsc5736project.LocationManager;
import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.interfaces.OnFriendListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnFriendResultListener;
import cuhk.cse.cmsc5736project.interfaces.OnFriendSelectedListener;
import cuhk.cse.cmsc5736project.models.Friend;

/**
 * Created by TCC on 12/10/2017.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ItemVH> {

    // Add new friend flag
    private boolean isAddNewFriend = false;

    //  Data
    private List<Friend> friendList = new ArrayList<>();

    String[] nameArray = {"Friend 1", "Friend 2", "Friend 3", "Friend 4", "Friend 5", "Friend 6", "Friend 7", "Friend 8", "Friend 9", "Friend 10"};
    String[] descArray = {"Male/Female toilet", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!"};

    private Activity activity;
    private OnFriendSelectedListener onFriendSelectedListener;

    public FriendListAdapter(Context context, Activity activity, boolean isAddNewFriend, OnFriendSelectedListener onFriendSelectedListener) {
        this.activity = activity;
        this.isAddNewFriend = isAddNewFriend;
        this.onFriendSelectedListener = onFriendSelectedListener;

        if (isAddNewFriend) {
            //populateSampleData();
            LocationManager.getInstance().getSimulatedFriendDefinitions(context, new OnFriendResultListener() {
                @Override
                public void onRetrieved(List<Friend> friendList) {
                    FriendListAdapter.this.friendList = friendList;
                    FriendListAdapter.this.notifyDataSetChanged();
                }
            });
        } else {
            LocationManager.getInstance().getCurrentUserFriendList(context, new OnFriendResultListener() {
                @Override
                public void onRetrieved(List<Friend> friendList) {
                    FriendListAdapter.this.friendList = friendList;
                    FriendListAdapter.this.notifyDataSetChanged();
                }
            });

            // For the listener, it synchronize operation with that from LocationManager
            LocationManager.getInstance().setOnFriendListChangeListener(new OnFriendListChangeListener() {
                @Override
                public void onAdded(Friend item) {
                    friendList.add(item);
                    sortViewList();
                    FriendListAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void onDeleted(Friend item) {
                    friendList.remove(item);
                    sortViewList();
                    FriendListAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void onChanged() {
                    sortViewList();
                    FriendListAdapter.this.notifyDataSetChanged();
                }
            });
        }
    }

    private void sortViewList() {
        Collections.sort(friendList, new Comparator<Friend>() {
            @Override
            public int compare(Friend f1, Friend f2) {
                if(f1.getBeacon().getRSSI() > f2.getBeacon().getRSSI()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
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
        final Friend item = friendList.get(position);

        holder.txtTitle.setText(item.getName() + " : RSSI= " + item.getBeacon().getRSSI());
        holder.txtDesc.setText(item.getDescription());

        if(isAddNewFriend) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onFriendSelectedListener != null) {
                        onFriendSelectedListener.onSelect(v, item);
                    }
                }
            });
        } else {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(onFriendSelectedListener != null) {
                        onFriendSelectedListener.onSelect(view, item);
                    }
                    return false;
                }
            });
        }
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

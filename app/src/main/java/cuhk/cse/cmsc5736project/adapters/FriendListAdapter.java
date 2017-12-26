package cuhk.cse.cmsc5736project.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    private Activity activity;
    private OnFriendSelectedListener onFriendSelectedListener;

    public FriendListAdapter(Context context, Activity activity, boolean isAddNewFriend, OnFriendSelectedListener onFriendSelectedListener) {
        this.activity = activity;
        this.isAddNewFriend = isAddNewFriend;
        this.onFriendSelectedListener = onFriendSelectedListener;



        if (isAddNewFriend) {
            //populateSampleData();
            LocationManager.getInstance().getFriendDefinitions(context, new OnFriendResultListener() {
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
                // No last-update logic
                if(f1.getLastUpdatedDate() == null) {
                    return 1;
                }
                if(f2.getLastUpdatedDate() == null) {
                    return -1;
                }

                if(f1.getLastUpdatedDate().getTime() < f2.getLastUpdatedDate().getTime() ) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    @Override
    public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        final ItemVH vh = new ItemVH(v);

        // For event listener, it should be set when list item view is populated, rather than when binding with new list items
        // ... as it is "recycling" already populated view, thus this avoids repeated setting event listeners
        if(isAddNewFriend) {
            // Hide some UI elements as well
            vh.txtLastUpdated.setVisibility(View.INVISIBLE);
            vh.ivProximityIndicator_1.setVisibility(View.INVISIBLE);
            vh.ivProximityIndicator_2.setVisibility(View.INVISIBLE);
            vh.ivProximityIndicator_3.setVisibility(View.INVISIBLE);
            vh.ivProximityIndicator_4.setVisibility(View.INVISIBLE);

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Friend item = friendList.get(vh.getAdapterPosition());
                    if(onFriendSelectedListener != null) {
                        onFriendSelectedListener.onSelect(v, item);
                    }

                }
            });
        } else {
            vh.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Friend item = friendList.get(vh.getAdapterPosition());
                    if(onFriendSelectedListener != null) {
                        onFriendSelectedListener.onSelect(view, item);
                    }
                    return false;
                }
            });
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(ItemVH holder, int position) {
        final Friend item = friendList.get(position);

        if(isAddNewFriend) {
            // Add friend: Show name + device ID only
            holder.txtTitle.setText(item.getName());
            holder.txtDesc.setText(item.getMAC());
        } else {
            // Current friend list: Show name + nearby POI + last update
            holder.txtTitle.setText(item.getName());
            if(item.getNearestLocation() != null) {
                holder.txtDesc.setText("Near: " + item.getNearestLocation().getName());
            } else {
                holder.txtDesc.setText("Near: Undetermined");
            }

            int prox = item.getProximityToCurrentUserPos(0, 0);
            switch (prox) {
                case Friend.PROXIMITY_VERY_CLOSE:
                    holder.ivProximityIndicator_1.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_2.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_3.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_4.setVisibility(View.VISIBLE);
                    break;
                case Friend.PROXIMITY_CLOSE:
                    holder.ivProximityIndicator_1.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_2.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_3.setVisibility(View.VISIBLE);
                    holder.ivProximityIndicator_4.setVisibility(View.INVISIBLE);
                    break;
                case Friend.PROXIMITY_FAR:
                    holder.ivProximityIndicator_1.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_2.setVisibility(View.VISIBLE);
                    holder.ivProximityIndicator_3.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_4.setVisibility(View.INVISIBLE);
                    break;
                case Friend.PROXIMITY_UNDETERMINED:
                    holder.ivProximityIndicator_1.setVisibility(View.VISIBLE);
                    holder.ivProximityIndicator_2.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_3.setVisibility(View.INVISIBLE);
                    holder.ivProximityIndicator_4.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }


            if(item.getLastUpdatedDate() != null) {
                long last_update_in_sec = (new Date().getTime() - item.getLastUpdatedDate().getTime()) / 1000;
                if(last_update_in_sec < 30) {
                    // < 30s = just now
                    holder.txtLastUpdated.setText("Just now");
                } else if(last_update_in_sec < 60) {
                    // < 60s = in terms of seconds
                    holder.txtLastUpdated.setText(last_update_in_sec + " sec. ago");
                } else if(last_update_in_sec < 60 * 60) {
                    // < 1 hr = in terms of minutes
                    int mins = (int) last_update_in_sec / 60;
                    holder.txtLastUpdated.setText(mins + " min. ago");
                } else {
                    // just show >1hr
                    holder.txtLastUpdated.setText("More than an hour");
                }
            } else {
                holder.txtLastUpdated.setText("---");
            }

        }
    }

    @Override
    public int getItemCount() {
        return friendList != null ? friendList.size() : 0;
    }

    protected static class ItemVH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc, txtLastUpdated;
        ImageView ivProximityIndicator_1, ivProximityIndicator_2, ivProximityIndicator_3, ivProximityIndicator_4;

        public ItemVH(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.item_friend_name);
            txtDesc = (TextView) itemView.findViewById(R.id.item_friend_desc);
            txtLastUpdated = (TextView) itemView.findViewById(R.id.item_friend_last_updated);
            ivProximityIndicator_1 = (ImageView) itemView.findViewById(R.id.item_friend_proximity_indicator_1);
            ivProximityIndicator_2 = (ImageView) itemView.findViewById(R.id.item_friend_proximity_indicator_2);
            ivProximityIndicator_3 = (ImageView) itemView.findViewById(R.id.item_friend_proximity_indicator_3);
            ivProximityIndicator_4 = (ImageView) itemView.findViewById(R.id.item_friend_proximity_indicator_4);
        }
    }

}

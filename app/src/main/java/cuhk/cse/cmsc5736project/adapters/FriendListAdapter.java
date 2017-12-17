package cuhk.cse.cmsc5736project.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private List<Friend> items = new ArrayList<>();

    private Context context;

    public FriendListAdapter(Context context) {
        this.context = context;
        populateSampleData();
    }

    private void populateSampleData() {

        String[] nameArray = {"Friend 1", "Friend 2", "Friend 3", "Friend 4", "Friend 5", "Friend 6", "Friend 7", "Friend 8", "Friend 9", "Friend 10"};
        String[] descArray = {"Male/Female toilet", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!"};

        final int SIZE = nameArray.length;

        for (int i = 0; i < SIZE; i++) {
            Friend dessert = new Friend(
                    nameArray[i],
                    descArray[i]
            );

            items.add(dessert);
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
        Friend item = items.get(position);

        holder.txtTitle.setText(item.getName());
        holder.txtDesc.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
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

package cuhk.cse.cmsc5736project.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import cuhk.cse.cmsc5736project.LocationManager;
import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.interfaces.OnPOIResultListener;
import cuhk.cse.cmsc5736project.models.Dessert;
import cuhk.cse.cmsc5736project.models.POI;

/**
 * Created by TCC on 12/10/2017.
 */

public class POIListAdapter extends RecyclerView.Adapter<POIListAdapter.ItemVH> {

    //  Data
    private List<POI> items = new ArrayList<>();

    private Context context;

    public POIListAdapter(Context context) {
        this.context = context;
        LocationManager.getInstance().getPOIDefinitions(context, new OnPOIResultListener() {
            @Override
            public void onRetrieved(List<POI> poiList) {
                items = poiList;
                POIListAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poi, parent, false);
        final ItemVH vh = new ItemVH(v);

        // For event listener, it should be set when list item view is populated, rather than when binding with new list items
        // ... as it is "recycling" already populated view, thus this avoids repeated setting event listeners
        vh.toggleBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                POI poi = items.get(vh.getAdapterPosition());
                Toast.makeText(context, poi.getName() + poi.isBookmarked(), Toast.LENGTH_SHORT).show();
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ItemVH holder, int position) {
        POI item = items.get(position);

        holder.txtTitle.setText(item.getName());
        holder.txtDesc.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    protected static class ItemVH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc;
        ToggleButton toggleBookmark;

        public ItemVH(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.item_poi_name);
            txtDesc = (TextView) itemView.findViewById(R.id.item_poi_desc);
            toggleBookmark = (ToggleButton) itemView.findViewById(R.id.item_poi_bookmark_toggle);
        }
    }
}

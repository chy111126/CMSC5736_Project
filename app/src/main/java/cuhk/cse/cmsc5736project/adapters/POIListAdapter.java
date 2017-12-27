package cuhk.cse.cmsc5736project.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cuhk.cse.cmsc5736project.LocationManager;
import cuhk.cse.cmsc5736project.POINotifManager;
import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.interfaces.OnPOIListChangeListener;
import cuhk.cse.cmsc5736project.interfaces.OnPOIResultListener;
import cuhk.cse.cmsc5736project.models.Beacon;
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
                sortViewList();
                POIListAdapter.this.notifyDataSetChanged();
            }
        });
        LocationManager.getInstance().setPOISyncDataListener(new OnPOIResultListener() {
            @Override
            public void onRetrieved(List<POI> poiList) {
                items = poiList;
                sortViewList();
                POIListAdapter.this.notifyDataSetChanged();
            }
        });
        LocationManager.getInstance().setOnPOIChangedListener(new OnPOIListChangeListener() {
            @Override
            public void onChanged() {
                sortViewList();
                checkIfArrivedPOI(POIListAdapter.this.context);
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
                poi.setBookmarked(!poi.isBookmarked());
                if(poi.isBookmarked()) {
                    Toast.makeText(context, "You have bookmarked " + poi.getName() + ".", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return vh;
    }

    private void checkIfArrivedPOI(Context context) {
        for(POI poi : items) {
            if(poi.isBookmarked()) {
                // Check for proximity
                if(poi.getBeacon().getProximity() == Beacon.PROXIMITY_VERY_CLOSE) {
                    poi.setBookmarked(false);
                    POINotifManager.notifyPOIArrival(context, poi);
                }
            }
        }
    }

    private void sortViewList() {
        Collections.sort(items, new Comparator<POI>() {
            @Override
            public int compare(POI f1, POI f2) {
                if(f1.getBeacon().getRSSI() < f2.getBeacon().getRSSI()) {
                    return 1;
                } else if(f1.getBeacon().getRSSI() > f2.getBeacon().getRSSI()){
                    return -1;
                } else {
                    if(f1.getName().compareTo(f2.getName()) != 0) {
                        return f1.getName().compareTo(f2.getName());
                    }
                }
                return 0;
            }
        });
    }

    @Override
    public void onBindViewHolder(ItemVH holder, int position) {
        POI item = items.get(position);

        holder.txtTitle.setText(item.getName());
        holder.txtDesc.setText(item.getDescription());
        DecimalFormat df = new DecimalFormat("#.##");

        if(item.getBeacon().getRSSI() == -9999) {
            holder.txtRSSI.setText("---");
        } else {
            //holder.txtRSSI.setText(" " + item.getBeacon().getRSSI());
            holder.txtRSSI.setText(" " + df.format(item.getBeacon().getDistance()) + "/" + item.getBeacon().getRSSI());
        }
        holder.toggleBookmark.setChecked(item.isBookmarked());

        int poiProximity = item.getBeacon().getProximity();
        switch (poiProximity) {
            case Beacon.PROXIMITY_VERY_CLOSE:
                holder.ivProximityIndicator_1.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_2.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_3.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_4.setVisibility(View.VISIBLE);
                break;
            case Beacon.PROXIMITY_CLOSE:
                holder.ivProximityIndicator_1.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_2.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_3.setVisibility(View.VISIBLE);
                holder.ivProximityIndicator_4.setVisibility(View.INVISIBLE);
                break;
            case Beacon.PROXIMITY_FAR:
                holder.ivProximityIndicator_1.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_2.setVisibility(View.VISIBLE);
                holder.ivProximityIndicator_3.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_4.setVisibility(View.INVISIBLE);
                break;
            case Beacon.PROXIMITY_UNDETERMINED:
                holder.ivProximityIndicator_1.setVisibility(View.VISIBLE);
                holder.ivProximityIndicator_2.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_3.setVisibility(View.INVISIBLE);
                holder.ivProximityIndicator_4.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    protected static class ItemVH extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDesc, txtRSSI;
        ToggleButton toggleBookmark;
        ImageView ivProximityIndicator_1, ivProximityIndicator_2, ivProximityIndicator_3, ivProximityIndicator_4;

        public ItemVH(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.item_poi_name);
            txtDesc = (TextView) itemView.findViewById(R.id.item_poi_desc);
            txtRSSI = (TextView) itemView.findViewById(R.id.item_poi_rssi_value);
            toggleBookmark = (ToggleButton) itemView.findViewById(R.id.item_poi_bookmark_toggle);
            ivProximityIndicator_1 = (ImageView) itemView.findViewById(R.id.item_poi_proximity_indicator_1);
            ivProximityIndicator_2 = (ImageView) itemView.findViewById(R.id.item_poi_proximity_indicator_2);
            ivProximityIndicator_3 = (ImageView) itemView.findViewById(R.id.item_poi_proximity_indicator_3);
            ivProximityIndicator_4 = (ImageView) itemView.findViewById(R.id.item_poi_proximity_indicator_4);
        }
    }
}

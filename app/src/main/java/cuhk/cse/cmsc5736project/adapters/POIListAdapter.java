package cuhk.cse.cmsc5736project.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        //populateSampleData();
    }

    private void populateSampleData() {

        String[] nameArray = {"Toilet", "Booth 1", "Booth 2", "Booth 3", "Booth 4", "Booth 5", "Booth 6", "Booth 7"};
        String[] descArray = {"Male/Female toilet", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!", "Buy book here!"};

        final int SIZE = nameArray.length;

        for (int i = 0; i < SIZE; i++) {
            POI dessert = new POI(
                    "0",
                    nameArray[i],
                    descArray[i]
            );

            items.add(dessert);
        }
    }

    @Override
    public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poi, parent, false);

        return new ItemVH(v);
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

        public ItemVH(View itemView) {
            super(itemView);

            txtTitle = (TextView) itemView.findViewById(R.id.item_poi_name);
            txtDesc = (TextView) itemView.findViewById(R.id.item_poi_desc);
        }
    }
}

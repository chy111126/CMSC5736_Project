package cuhk.cse.cmsc5736project.interfaces;

import java.util.List;

import cuhk.cse.cmsc5736project.models.POI;

public interface OnPOIResultListener {
    void onRetrieved(List<POI> poiList);
}

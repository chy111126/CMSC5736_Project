package cuhk.cse.cmsc5736project.interfaces;

import java.util.List;

import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.models.POI;

public interface OnFriendResultListener {
    void onRetrieved(List<Friend> friendList);
}

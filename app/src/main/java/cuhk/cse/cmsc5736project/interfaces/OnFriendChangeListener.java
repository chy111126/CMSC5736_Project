package cuhk.cse.cmsc5736project.interfaces;

import java.util.List;

import cuhk.cse.cmsc5736project.models.Friend;

public interface OnFriendChangeListener {
    void onAdded(Friend item);
    void onDeleted(Friend item);
    void onChanged();
}

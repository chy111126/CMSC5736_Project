package cuhk.cse.cmsc5736project.interfaces;

import android.view.View;

import java.util.List;

import cuhk.cse.cmsc5736project.models.Friend;

/**
 * Created by TCC on 12/18/2017.
 */

public interface OnFriendSelectedListener {
    void onSelect(View v, Friend item);
}


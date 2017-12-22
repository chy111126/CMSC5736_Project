package cuhk.cse.cmsc5736project.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import cuhk.cse.cmsc5736project.AddFriendActivity;
import cuhk.cse.cmsc5736project.AddNewFriendActivity;
import cuhk.cse.cmsc5736project.LocationManager;
import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.adapters.FriendListAdapter;
import cuhk.cse.cmsc5736project.interfaces.OnFriendSelectedListener;
import cuhk.cse.cmsc5736project.models.Friend;
import cuhk.cse.cmsc5736project.utils.Utility;

import static android.app.Activity.RESULT_OK;


public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();
    public static final String INTENT_KEY_NEW_FRIEND = "new_friend";
    public static final int REQUEST_CODE_ADD_FRIEND = 1999;

    // Variable for fragment coloring
    private static final String ARG_COLOR = "color";
    private int color;

    // UI elements
    private RecyclerView recyclerView;
    private FriendListAdapter adapter;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            color = getArguments().getInt(ARG_COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutColor = Utility.getLighterColor(color);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        rootView.setBackgroundColor(layoutColor);

        // Friend list through RecyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_friends_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setBackgroundColor(layoutColor);

        adapter = new FriendListAdapter(getContext(), getActivity(), false, new OnFriendSelectedListener() {
            @Override
            public void onSelect(View v, Friend item) {
                // TODO: Add confirm modal
                showConfirmRemoveFriendDialog(item);
            }
        });
        recyclerView.setAdapter(adapter);

        // Add friend with FAB
        FloatingActionButton myFab = (FloatingActionButton) rootView.findViewById(R.id.fab_add_friend);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddFriendActivity();
            }
        });

        return rootView;
    }

    public void updateFriendList()
    {
        adapter.notifyDataSetChanged();
    }

    private void showAddFriendActivity() {
        startActivityForResult(new Intent(getContext(), AddNewFriendActivity.class), REQUEST_CODE_ADD_FRIEND);
    }

    public void showConfirmRemoveFriendDialog(final Friend item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());
        alert.setTitle("Remove selected friend?");

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Your action here
                LocationManager.getInstance().removeFriend(item);
            }
        });

        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FriendsFragment.REQUEST_CODE_ADD_FRIEND && resultCode == RESULT_OK && data != null) {
            Friend newFriend = (Friend) data.getSerializableExtra(FriendsFragment.INTENT_KEY_NEW_FRIEND);
            LocationManager.getInstance().putFriend(newFriend);
            LocationManager.getInstance().addFriendToServer(newFriend,getContext());
        }
    }

}
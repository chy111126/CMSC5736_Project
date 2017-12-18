package cuhk.cse.cmsc5736project.fragments;

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

import cuhk.cse.cmsc5736project.AddNewFriendActivity;
import cuhk.cse.cmsc5736project.R;
import cuhk.cse.cmsc5736project.adapters.FriendListAdapter;
import cuhk.cse.cmsc5736project.models.Friend;


public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    // Variable for fragment coloring
    private static final String ARG_COLOR = "color";
    private int color;

    // UI elements
    private RecyclerView recyclerView;
    static private FriendListAdapter adapter;

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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        rootView.setBackgroundColor(getLighterColor(color));

        // Friend list through RecyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_friends_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setBackgroundColor(getLighterColor(color));

        adapter = new FriendListAdapter(getContext());
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
    static public void AddFriend(Friend friend)
    {
        FriendListAdapter.curFriendList.add(friend);
        adapter.notifyDataSetChanged();
    }

    private void showAddFriendActivity() {
        startActivity(new Intent(getContext(), AddNewFriendActivity.class));
    }

    private int getLighterColor(int color) {
        // Lighter color = 30% of given color hex
        return Color.argb(10,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

}
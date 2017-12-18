package cuhk.cse.cmsc5736project;

/**
 * Created by TCC on 12/17/2017.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import cuhk.cse.cmsc5736project.adapters.FriendListAdapter;

public class AddNewFriendActivity extends AppCompatActivity {
    private static final String TAG = AddNewFriendActivity.class.getSimpleName();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add new friend");
        setContentView(R.layout.activity_add_friend);
        
        setupFriendList();
    }

    private void setupFriendList() {
        // Friend list through RecyclerView
        //recyclerView = (RecyclerView) findViewById(R.id.add_friend_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FriendListAdapter adapter = new FriendListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
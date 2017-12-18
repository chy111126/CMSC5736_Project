package cuhk.cse.cmsc5736project;

/**
 * Created by TCC on 12/17/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import cuhk.cse.cmsc5736project.adapters.FriendListAdapter;
import cuhk.cse.cmsc5736project.fragments.FriendsFragment;
import cuhk.cse.cmsc5736project.interfaces.OnFriendSelectedListener;
import cuhk.cse.cmsc5736project.models.Friend;

public class AddNewFriendActivity extends AppCompatActivity {
    private static final String TAG = AddNewFriendActivity.class.getSimpleName();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add new friend");
        setContentView(R.layout.activity_add_new_friend);
        
        setupFriendList();
    }

    private void setupFriendList() {
        // Friend list through RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.add_friend_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FriendListAdapter adapter = new FriendListAdapter(this, this, true, new OnFriendSelectedListener() {
            @Override
            public void onSelect(View v, Friend item) {
                // Add the friend to current friend list, and go back
                Intent output = new Intent();
                output.putExtra(FriendsFragment.INTENT_KEY_NEW_FRIEND, 1);
                AddNewFriendActivity.this.setResult(RESULT_OK, output);
                AddNewFriendActivity.this.finish();
            }
        });
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

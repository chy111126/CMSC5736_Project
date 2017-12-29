package cuhk.cse.cmsc5736project.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import cuhk.cse.cmsc5736project.R;

/**
 * Created by Charmy on 28/12/2017.
 */

public class FriendListDialog extends Dialog implements
        View.OnClickListener {

    private Button ok;
    private TextView txtView;
    private String message;

    public FriendListDialog(Activity a, String message) {
        super(a);
        // TODO Auto-generated constructor stub
        //this.c = a;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pin_dialog);
        ok = (Button) findViewById(R.id.btn_ok);
        ok.setOnClickListener(this);

        txtView = (TextView)findViewById(R.id.txt_list);
        txtView.setText(message);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == ok.getId()) {
            dismiss();
        }
    }

    private void setMessage(String message){
        txtView.setText(message);
    }

}

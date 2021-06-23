package bolomagic.in.CustomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import bolomagic.in.R;
import bolomagic.in.ReferActivity;

public class CustomDialogShare extends Dialog implements android.view.View.OnClickListener {

    public Activity activity;
    public Button shareButton;

    public CustomDialogShare(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_share);
        shareButton = (Button) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shareButton) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, ReferActivity.sharingMessage);
            intent.setType("text/plain");
            activity.startActivity(intent);
        }
        dismiss();
    }
}

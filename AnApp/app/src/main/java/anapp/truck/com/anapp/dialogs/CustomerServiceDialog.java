package anapp.truck.com.anapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import anapp.truck.com.anapp.R;

/**
 * Created by angli on 3/30/15.
 */
public class CustomerServiceDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定呼叫万能客服 0710-3394466?")
                .setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String url = "tel:" + getString(R.string.service_phone_num);
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                        startActivity(callIntent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
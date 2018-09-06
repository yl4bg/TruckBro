package anapp.truck.com.anapp.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import anapp.truck.com.anapp.R;

/**
 * Created by LaIMaginE on 4/8/2015.
 */
public class SOSMsgDialog extends DialogFragment {

    private String phoneNum;

    public SOSMsgDialog(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText input = new EditText(getActivity().getApplicationContext());
        input.setTextSize(20);
        input.setTextColor(getResources().getColor(R.color.Black));

        phoneNum = getArguments().getString("phoneNum");

        builder.setTitle("请输入短信内容")
                .setView(input)
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            sendSMS(phoneNum, input.getText().toString());
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "信息发送至" + phoneNum,
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    getString(R.string.sms_failed),
                                    Toast.LENGTH_SHORT).show();
                            Log.e("invite-sms", e.getMessage(), e);
                            return;
                        }
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

    private void sendSMS(String phoneNumber, String message) throws Exception
    {
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e){
            throw new Exception(e);
        }
    }
}

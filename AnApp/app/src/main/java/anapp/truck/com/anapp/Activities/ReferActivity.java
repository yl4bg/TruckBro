package anapp.truck.com.anapp.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.adapters.ReferCellAdapter;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.ToastUtil;

/**
 * Created by angli on 4/3/15.
 */
public class ReferActivity extends DefaultActivity {

    private static final int PICK_CONTACT = 1;
    private BroadcastReceiver broad1;
    private BroadcastReceiver broad2;
    private List<String> phoneNumList;
    private ListView numListView;
    private EditText smsEditText;
    private ReferCellAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_bro);

        phoneNumList = new ArrayList<>();

        final EditText phoneNumField = (EditText) findViewById(R.id.refer_enter_number);
        final ImageButton toContacts = (ImageButton) findViewById(R.id.refer_from_contact);
        final TextView addNum = (TextView) findViewById(R.id.refer_add_number);
        numListView = (ListView) findViewById(R.id.refer_phone_num_list);
        smsEditText = (EditText) findViewById(R.id.sms_edittext);

        adapter = new ReferCellAdapter(ReferActivity.this, phoneNumList);
        numListView.setAdapter(adapter);
        numListView.invalidateViews();

        toContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        addNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num = phoneNumField.getText().toString();
                if(!validatePhoneNumFormat(num)){
                    ToastUtil.show(ReferActivity.this, "号码格式错误，请确认号码为11位数字");
                    return;
                }
                phoneNumList.add(num);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        phoneNumField.setText("");
                        adapter.notifyDataSetChanged();
                        numListView.invalidateViews();
                    }
                });
            }
        });

        ImageButton submit = (ImageButton) findViewById(R.id.refer_confirm);
        ImageButton cancel = (ImageButton) findViewById(R.id.refer_cancel);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(String num : phoneNumList){
                    sendSmsInviteToNumber(num);
                }
                ReferActivity.this.finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReferActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(broad1 != null)
            unregisterReceiver(broad1);
        if(broad2 != null)
            unregisterReceiver(broad2);
    }

    private void sendSmsInviteToNumber(String phoneNumber){
        try {
            if(!validatePhoneNumFormat(phoneNumber)){
                Toast.makeText(getApplicationContext(),
                        "号码格式不正确，邀请未成功。请改正格式或设置通讯录权限。",
                        Toast.LENGTH_LONG).show();
            } else {
                //"我是" + CookieManager.getInstance().getUserID() + "，正在使用卡车兄弟俱乐部专用APP。路况、执法信息早知道。一起来用吧。 "
                smsEditText.setText(smsEditText.getText().toString() + getString(R.string.download_link) + "?referralID=" + CookieManager.getInstance().getUserID());
                sendSMS(phoneNumber);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.invite_sent) + phoneNumber,
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.sms_failed),
                    Toast.LENGTH_SHORT).show();
            Log.e("invite-sms", e.getMessage(), e);
            return;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode, resultCode, data);

        switch(reqCode)
        {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK)
                {
                    Log.i("debug", "result ok");
                    Uri contactData = data.getData();
                    Log.i("debug-uri", contactData.toString());
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    Log.i("debug-cursor", c.getCount()+"");

                    if (c.moveToFirst())
                    {
                        Log.i("debug", "in here");
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =
                                c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Log.i("debug", "has phone");
                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.i("debug", cNumber);
                            phoneNumList.add(cNumber);
                            adapter.notifyDataSetChanged();
                            numListView.invalidateViews();
                        }
                    }
                }
        }
    }

    private void sendSMS(String phoneNumber) throws Exception
    {
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null,  smsEditText.getText().toString(), null, null);
        } catch (Exception e){
            throw new Exception(e);
        }
    }

    private boolean validatePhoneNumFormat(String phoneNum) {
        try {
            if (phoneNum.length() < 11) {
                throw new FormatException();
            }
            Long.parseLong(phoneNum);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

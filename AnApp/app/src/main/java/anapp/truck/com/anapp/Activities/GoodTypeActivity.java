package anapp.truck.com.anapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;

/**
 * Created by LaIMaginE on 4/8/2015.
 */
public class GoodTypeActivity extends DefaultActivity {

    private static final Map<String, Integer> types;
    static{
        types = new HashMap<>();
        types.put("零担", R.id.i1);
        types.put("绿通", R.id.i2);
        types.put("快递", R.id.i3);
        types.put("冷链", R.id.i4);
        types.put("资源", R.id.i5);
        types.put("危化", R.id.i6);
        types.put("车辆", R.id.i7);
        types.put("大件", R.id.i8);
        types.put("港口", R.id.i9);
        types.put("其他", R.id.i10);
    }
    private String[] names = {"零担","绿通","快递","冷链","资源","危化","车辆","大件","港口","其他"};
    private boolean[] selected = {false, false, false, false, false, false, false, false, false, false};
    private String initValue;

    private int findIndex(String s){
        for(int i=0;i<names.length;i++){
            if(names[i].equals(s))
                return i;
        }
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.goodtype_layout);

        Bundle bundle = getIntent().getExtras();
        initValue = bundle.getString("initValue").replaceAll(" ", "").replaceAll("noGoodType", "");
        if(initValue != null && !initValue.equals("")){
            String[] goods = initValue.split(",");
            for(String g : goods){
                ((ImageView)findViewById(types.get(g))).setImageResource(R.drawable.new_selected_box);
                selected[findIndex(g)] = true;
            }
        }


        RelativeLayout b1 = (RelativeLayout) findViewById(R.id.b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[0])
                    ((ImageView)findViewById(R.id.i1)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i1)).setImageResource(R.drawable.new_unselected_box);
                selected[0] = !selected[0];
            }
        });
        RelativeLayout b2 = (RelativeLayout) findViewById(R.id.b2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[1])
                    ((ImageView)findViewById(R.id.i2)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i2)).setImageResource(R.drawable.new_unselected_box);
                selected[1] = !selected[1];
            }
        });
        RelativeLayout b3 = (RelativeLayout) findViewById(R.id.b3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[2])
                    ((ImageView)findViewById(R.id.i3)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i3)).setImageResource(R.drawable.new_unselected_box);
                selected[2] = !selected[2];
            }
        });
        RelativeLayout b4 = (RelativeLayout) findViewById(R.id.b4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[3])
                    ((ImageView)findViewById(R.id.i4)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i4)).setImageResource(R.drawable.new_unselected_box);
                selected[3] = !selected[3];
            }
        });
        RelativeLayout b5 = (RelativeLayout) findViewById(R.id.b5);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[4])
                    ((ImageView)findViewById(R.id.i5)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i5)).setImageResource(R.drawable.new_unselected_box);
                selected[4] = !selected[4];
            }
        });
        RelativeLayout b6 = (RelativeLayout) findViewById(R.id.b6);
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[5])
                    ((ImageView)findViewById(R.id.i6)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i6)).setImageResource(R.drawable.new_unselected_box);
                selected[5] = !selected[5];
            }
        });
        RelativeLayout b7 = (RelativeLayout) findViewById(R.id.b7);
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[6])
                    ((ImageView)findViewById(R.id.i7)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i7)).setImageResource(R.drawable.new_unselected_box);
                selected[6] = !selected[6];
            }
        });
        RelativeLayout b8 = (RelativeLayout) findViewById(R.id.b8);
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[7])
                    ((ImageView)findViewById(R.id.i8)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i8)).setImageResource(R.drawable.new_unselected_box);
                selected[7] = !selected[7];
            }
        });
        RelativeLayout b9 = (RelativeLayout) findViewById(R.id.b9);
        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[8])
                    ((ImageView)findViewById(R.id.i9)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i9)).setImageResource(R.drawable.new_unselected_box);
                selected[8] = !selected[8];
            }
        });
        RelativeLayout b10 = (RelativeLayout) findViewById(R.id.b10);
        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected[9])
                    ((ImageView)findViewById(R.id.i10)).setImageResource(R.drawable.new_selected_box);
                else
                    ((ImageView)findViewById(R.id.i10)).setImageResource(R.drawable.new_unselected_box);
                selected[9] = !selected[9];
            }
        });

        RelativeLayout cancel = (RelativeLayout) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodTypeActivity.this.finish();
            }
        });

        RelativeLayout submit = (RelativeLayout) findViewById(R.id.submit_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selectedGoods = new ArrayList<String>();
                for(int i=0;i<selected.length;i++){
                    if(selected[i]) selectedGoods.add(names[i]);
                }
                String list = selectedGoods.toString();
                list = list.substring(1, list.length()-1).replaceAll(" ","");
                new UpdateGoodTypesTask().execute(CookieManager.getInstance().getCookie(), list);
            }
        });
    }

    private class UpdateGoodTypesTask extends AsyncTask<String, Void, ErrorMsg> {

        @Override
        /**
         * locInfo: roadNum, province, city, district
         */
        protected ErrorMsg doInBackground(String... info) {
            final String url = GoodTypeActivity.this.getApplicationContext().getString(R.string.server_prefix)
                    + "/updateGoodType.json";
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            Map<String, String> urlArgs = new HashMap<>();
            urlArgs.put("cookie", info[0]);
            urlArgs.put("goodTypeList", info[1]);
            try {
                return restTemplate.getForObject(
                        url + "?cookie={cookie}&goodTypeList={goodTypeList}",
                        ErrorMsg.class, urlArgs);
            } catch (Exception e) {
                Log.e("AsyncTask", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ErrorMsg msg) {
            if (msg == null) {
                Toast.makeText(getApplicationContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), msg.getText(), Toast.LENGTH_SHORT).show();
            GoodTypeActivity.this.finish();
        }

    }
}

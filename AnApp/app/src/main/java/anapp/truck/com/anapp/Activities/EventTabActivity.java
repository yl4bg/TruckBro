package anapp.truck.com.anapp.activities;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import java.util.Random;

import anapp.truck.com.anapp.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by angli on 3/27/15.
 */
public class EventTabActivity extends TabActivity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_tab_activity);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);


        TabHost.TabSpec tab1 = tabHost.newTabSpec("路况列表");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("地图模式");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("路况搜索");

        // select the first tab
        TabWidget tabs = (TabWidget) findViewById(android.R.id.tabs);
        tabs.setBackgroundResource(R.drawable.tab1);

        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("路况列表");
        tab1.setContent(new Intent(this, EventTableActivity.class));

        tab2.setIndicator("地图模式");
        tab2.setContent(new Intent(this, MapViewActivity.class));

        tab3.setIndicator("路况搜索");
        tab3.setContent(new Intent(this, SearchActivity.class));

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);

        //customized tab selector image
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                TabWidget tabs = (TabWidget) findViewById(android.R.id.tabs);
                if ("路况列表".equals(tabId)) {
                    tabs.setBackgroundResource(R.drawable.tab1);
                }
                if ("地图模式".equals(tabId)) {
                    tabs.setBackgroundResource(R.drawable.tab2);
                }
                if ("路况搜索".equals(tabId)) {
                    tabs.setBackgroundResource(R.drawable.tab3);
                }
            }
        });

        Random r = new Random();

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setTextSize(15);
        }

    }

//    /**
//     * 这部分也很重要！每个activity都要放 不然字体就不会被改
//     * @param newBase
//     */
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
}

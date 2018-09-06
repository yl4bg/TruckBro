package anapp.truck.com.anapp.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.ErrorMsg;
import anapp.truck.com.anapp.utility.CookieManager;
import anapp.truck.com.anapp.utility.valueObjects.Event;


/**
 * Created by ZYL on 1/31/2015.
 */
public class EventCellAdapter extends ArrayAdapter<Event> {

    private Context context;
    private Event[] values;
    private String imageHint;
    private boolean longVersion;

    public EventCellAdapter(Context context, Event[] values, boolean longVersion) {
        super(context, R.layout.event_single_cell, values);
        this.context = context;
        this.values = values;
        this.imageHint = context.getString(R.string.click_view_image_hint);
        this.longVersion = longVersion;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final Event e = values[position];

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView;

        if(!e.type.equals(this.context.getString(R.string.event_type_help))) {

            if(longVersion){
                rowView = inflater.inflate(R.layout.event_single_cell_long, parent, false);
            } else {
                rowView = inflater.inflate(R.layout.event_single_cell, parent, false);
            }

            TextView eventTypeView = (TextView) rowView.findViewById(R.id.typeText);
            eventTypeView.setText(e.type);

        } else {
            if(longVersion){
                rowView = inflater.inflate(R.layout.event_single_cell_sos_long, parent, false);
            } else {
                rowView = inflater.inflate(R.layout.event_single_cell_sos, parent, false);
            }

        }

        TextView distanceView = (TextView) rowView.findViewById(R.id.distanceText);
        TextView directionView = (TextView) rowView.findViewById(R.id.directionText);
        TextView userName = (TextView)rowView.findViewById(R.id.left_banner_user_name);
        TextView timeView = (TextView) rowView.findViewById(R.id.timeText);
        TextView roadNumView = (TextView) rowView.findViewById(R.id.roadNum);
        TextView upvoteText = (TextView) rowView.findViewById(R.id.upvote_text);
        TextView reportText = (TextView) rowView.findViewById(R.id.report_text);
//        ImageView hasImage = (ImageView) rowView.findViewById(R.id.hasImageIcon);

        userName.setText(e.senderName);
        distanceView.setText(String.format("%.1f",e.distance)+ this.context.getString(R.string.kilometer));
        directionView.setText(e.direction);
        timeView.setText(e.time);
        roadNumView.setText(e.roadNumText);
        upvoteText.setText(upvoteText.getText() + "(" + e.upCnt + ")");
        reportText.setText(reportText.getText() + "(" + e.reportCnt + ")");

//        if(e.picIDs.size() == 0){
//            hasImage.setVisibility(View.INVISIBLE);
//        }

        ImageView typeIcon = (ImageView) rowView.findViewById(R.id.event_icon);
        if(e.type.equals(this.context.getString(R.string.event_type_accident))){
            typeIcon.setImageResource(R.drawable.accident_new_symbol);
        } else if(e.type.equals(this.context.getString(R.string.event_type_construction))){
            typeIcon.setImageResource(R.drawable.construction_new_symbol);
        } else if(e.type.equals(this.context.getString(R.string.event_type_limitedflow))){
            typeIcon.setImageResource(R.drawable.limited_new_symbol);
        } else if(e.type.equals(this.context.getString(R.string.event_type_regulation))){
            typeIcon.setImageResource(R.drawable.regulation_new_symbol);
        } else if(e.type.equals(this.context.getString(R.string.event_type_trafficjam))){
            typeIcon.setImageResource(R.drawable.crowded_new_symbol);
        } else if(e.type.equals(this.context.getString(R.string.event_type_help))) {
            // intentionally empty
        } else {
            typeIcon.setImageResource(R.drawable.other_new_symbol);
        }

        if(e.official && !e.type.equals(this.context.getString(R.string.event_type_help))){

            // change left banner and background to offical banner
            LinearLayout left_banner = (LinearLayout) rowView.findViewById(R.id.single_cell_left_banner);
            left_banner.setBackgroundResource(R.drawable.event_single_cell_offical_banner);
            rowView.setBackgroundResource(R.drawable.event_single_background_offical_new);

            // change user name to offical
            userName.setText(R.string.offical_name);

//            TextView color_banner = (TextView) rowView.findViewById(R.id.color_banner);
//            ImageView upvoteIcon = (ImageView) rowView.findViewById(R.id.upvote_icon);
//            ImageView reportIcon = (ImageView) rowView.findViewById(R.id.report_icon);

//            color_banner.setBackgroundResource(R.color.Warm_Red);
//            upvoteIcon.setImageResource(R.drawable.upvote_icon_official);
//            reportIcon.setImageResource(R.drawable.report_icon_official);

            //change upvote position to report position and hide report layout
            LinearLayout upvote_layout = (LinearLayout) rowView.findViewById(R.id.upvote_layout);
            LinearLayout report_layout = (LinearLayout) rowView.findViewById(R.id.report_layout);
            LinearLayout right_layout = (LinearLayout) rowView.findViewById(R.id.single_cell_right_layout);
            right_layout.removeView(upvote_layout);
            right_layout.addView(upvote_layout);
            report_layout.setVisibility(View.INVISIBLE);

        }
        return rowView;
    }

}

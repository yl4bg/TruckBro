package anapp.truck.com.anapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import anapp.truck.com.anapp.R;
import anapp.truck.com.anapp.rest.PointsHistoryList;

/**
 * Created by angli on 7/5/15.
 */
public class PointsHistoryAdapter extends ArrayAdapter<PointsHistoryList.PointsHistory> {

    private Activity context;
    private List<PointsHistoryList.PointsHistory> values;

    public PointsHistoryAdapter(Activity context, List<PointsHistoryList.PointsHistory> values) {
        super(context, R.layout.points_cell, values);
        this.context = context;
        this.values = values;
    }

    public void setValues(List<PointsHistoryList.PointsHistory> values) {
        this.values = values;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.points_cell, parent, false);

        ((TextView) rowView.findViewById(R.id.dateText)).setText(values.get(position).getDate());
        ((TextView) rowView.findViewById(R.id.descriptionText)).setText(values.get(position).getDescription());
        ((TextView) rowView.findViewById(R.id.pointsDiffText)).setText(values.get(position).getPointsDiff());

        return rowView;
    }

}

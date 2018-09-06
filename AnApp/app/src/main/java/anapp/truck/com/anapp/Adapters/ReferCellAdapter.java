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

/**
 * Created by angli on 7/6/15.
 */
public class ReferCellAdapter extends ArrayAdapter<String> {

    private Activity context;
    private List<String> values;

    public ReferCellAdapter(Activity context, List<String> values) {
        super(context, R.layout.refer_cell, values);
        this.context = context;
        this.values = values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.refer_cell, parent, false);

        ((TextView) rowView.findViewById(R.id.refer_cell_number)).setText(
                values.get(position)
        );

        rowView.findViewById(R.id.refer_cell_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values.remove(position);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }
}

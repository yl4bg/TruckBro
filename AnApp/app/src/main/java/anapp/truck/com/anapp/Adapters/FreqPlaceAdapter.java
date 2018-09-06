package anapp.truck.com.anapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import anapp.truck.com.anapp.activities.FrequentPlaceActivity;
import anapp.truck.com.anapp.R;

/**
 * Created by angli on 6/17/15.
 */
public class FreqPlaceAdapter extends ArrayAdapter<String> {

    private Activity context;
    private List<String> values;

    public FreqPlaceAdapter(Activity context, List<String> values) {
        super(context, R.layout.freqplace_cell_layout, values);
        this.context = context;
        this.values = values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.freqplace_cell_layout, parent, false);

        final TextView freqPlaceText = (TextView) rowView.findViewById(R.id.freqPlace_text);
        freqPlaceText.setText(values.get(position));

        TextView deleteButton = (TextView) rowView.findViewById(R.id.delete_btn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values.remove(values.get(position));
                FreqPlaceAdapter.this.notifyDataSetChanged();
                ((FrequentPlaceActivity) context).updateHintText();
                ((FrequentPlaceActivity) context).setListViewHeightBasedOnItems();

            }
        });
        return rowView;
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

}

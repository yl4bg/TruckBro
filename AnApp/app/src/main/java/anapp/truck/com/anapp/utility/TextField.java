package anapp.truck.com.anapp.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import anapp.truck.com.anapp.R;

/**
 * Created by Anna on 6/17/15.
 * 为改字体用的
 *
 */
public class TextField extends TextView {

    public TextField(final Context context, final AttributeSet attrs) {
        super(context, attrs, R.attr.textFieldStyle);
    }

}
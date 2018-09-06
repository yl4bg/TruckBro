package anapp.truck.com.anapp.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by angli on 7/10/15.
 */
public class ResizableListView extends ListView{

        public ResizableListView (Context context) {
            super(context);
        }

        public ResizableListView (Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ResizableListView (Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
            super.onSizeChanged(xNew, yNew, xOld, yOld);

            setSelection(getCount());

        }
}

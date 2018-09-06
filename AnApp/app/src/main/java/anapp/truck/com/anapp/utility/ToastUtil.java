package anapp.truck.com.anapp.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by LaIMaginE on 3/10/2015.
 */
public class ToastUtil {

    public static void show(Context context, String info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, int info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

}

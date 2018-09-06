package anapp.truck.com.anapp.utility.smack;

import android.app.Activity;

/**
 * Created by angli on 7/6/15.
 */
public interface ChatRelatedActivity {

    public void refreshUI();
    public void clearUnreadCount(int index);
    public void upUnreadCount(int index);
    public Activity getActivity();
    public void updateLastestText(String date, String msg, int index);
}

package anapp.truck.com.anapp.rest;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by angli on 7/16/15.
 */
public class PointsHistoryList {

    private List<PointsHistory> poinstHistories;

    public List<PointsHistory> getPoinstHistories() {
        return poinstHistories;
    }

    public void setPoinstHistories(List<PointsHistory> poinstHistories) {
        this.poinstHistories = poinstHistories;
    }

    public static class PointsHistory {

        private String date;
        private String description;
        private String pointsDiff;

        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getPointsDiff() {
            return pointsDiff;
        }
        public void setPointsDiff(String pointsDiff) {
            this.pointsDiff = pointsDiff;
        }
    }
}
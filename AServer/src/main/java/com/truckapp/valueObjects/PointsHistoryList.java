package com.truckapp.valueObjects;

import java.text.SimpleDateFormat;
import java.util.List;

import com.truckapp.util.Constants;

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
        
		public PointsHistory(com.truckapp.database.PointsHistory ph){
			this.date = new SimpleDateFormat(Constants.POINTS_HISTORY_DATE_FORMAT).format(ph.getpDate());
			this.description = ph.getpDesc();
			this.pointsDiff = ph.getpDiff();
		}
    }
}

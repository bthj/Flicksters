package is.bthj.itu.datamining.flicksters.data;

import java.util.List;

import uk.me.jstott.jcoord.LatLng;

public class FlickrPhotoCluster {

	private double centroidLongitude;
	private double centroidLatitude;
	private int memberCount;
	private String mostFrequentTag;
	private List<FlickrPhoto> tuples;
	
	
	public double getCentroidLongitude() {
		return centroidLongitude;
	}
	public void setCentroidLongitude(double centroidLongitude) {
		this.centroidLongitude = centroidLongitude;
	}
	public double getCentroidLatitude() {
		return centroidLatitude;
	}
	public void setCentroidLatitude(double centroidLatitude) {
		this.centroidLatitude = centroidLatitude;
	}
	public int getMemberCount() {
		return memberCount;
	}
	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}
	public String getMostFrequentTag() {
		return mostFrequentTag;
	}
	public void setMostFrequentTag(String mostFrequentTag) {
		this.mostFrequentTag = mostFrequentTag;
	}
	public List<FlickrPhoto> getTuples() {
		return tuples;
	}
	public void setTuples(List<FlickrPhoto> tuples) {
		this.tuples = tuples;
	}

}

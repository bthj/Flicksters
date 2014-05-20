package is.bthj.itu.datamining.flicksters.data;

import uk.me.jstott.jcoord.LatLng;

public class ClusterCentroid {

	private double centroidLongitude;
	private double centroidLatitude;
	private int memberCount;
	
	@Override
	public boolean equals( Object o ) {
		if( !(o instanceof ClusterCentroid) ) {
			return false;
		}
		return this.centroidLatitude == ((ClusterCentroid) o).getCentroidLatitude() &&
				this.centroidLongitude == ((ClusterCentroid) o).getCentroidLongitude();
	}
	
	@Override
	public int hashCode() {
		// based on java.awt.geom.Point2D.hashCode() - http://stackoverflow.com/a/9252050/169858
		long bits = java.lang.Double.doubleToLongBits(this.centroidLatitude);
	    bits ^= java.lang.Double.doubleToLongBits(this.centroidLongitude) * 31;
	    return (((int) bits) ^ ((int) (bits >> 32)));
	}

	
	public LatLng getCentroid() {
		return new LatLng(getCentroidLatitude(), getCentroidLongitude());
	}
//	public void setCentroid( LatLng centroid ) {
//		this.centroidLatitude = centroid.getLat();
//		this.centroidLongitude = centroid.getLng();
//	}
	
	
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
	
}

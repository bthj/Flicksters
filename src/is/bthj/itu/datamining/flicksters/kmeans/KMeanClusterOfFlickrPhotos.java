package is.bthj.itu.datamining.flicksters.kmeans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;
import is.bthj.itu.datamining.flicksters.data.FlickrPhoto;

public class KMeanClusterOfFlickrPhotos extends KMeanCluster<FlickrPhoto, LatLng> {
	
	private LatLng clusterMean;
	
	private Map<String, UTMRef> tupleUTMRef;
	
	public KMeanClusterOfFlickrPhotos() {
		
		this.clusterMembers = new HashSet<FlickrPhoto>();
		this.tupleUTMRef = new HashMap<String, UTMRef>();
	}
	
	@Override
	public float getTupleDistanceToClusterMean(FlickrPhoto tuple) {
		float distance = 0;
		
		if( null == clusterMean ) updateClusterMean();
			
		LatLng tupleLatLng = new LatLng(tuple.getLatitude(), tuple.getLongitude());
		distance = (float) clusterMean.distance(tupleLatLng);
		
		return distance;
	}
	
	
	@Override
	public float getSumOfSquaredDistancesToMean() {
		float squaredDistancesSum = 0;
		for( FlickrPhoto oneTuple : clusterMembers ) {
			squaredDistancesSum += Math.pow( getTupleDistanceToClusterMean(oneTuple), 2 );
		}
		return squaredDistancesSum;
	}
	
	@Override
	public LatLng getClusterMean() {
		return clusterMean;
	}
	@Override
	public void setClusterMean( LatLng mean ) {
		this.clusterMean = mean;
	}
 
	public void updateClusterMean() {
		
		// TODO: converting to UTM on the fly may be too expensive!
		// 	we might instead want to do something like:  http://www.geomidpoint.com/calculation.html
		double eastingSums = 0, northingSums = 0;
		// TODO: Handle the case where cluster coordinates span different UTM zones - if needed?
		char latZone = '\u0000';
		int lngZone = 0;
		for( FlickrPhoto oneTuple : clusterMembers ) {
			
			LatLng tupleLatLng = new LatLng(oneTuple.getLatitude(), oneTuple.getLongitude());
			
			UTMRef tupleUTM = tupleUTMRef.get(oneTuple.getPhotoId());
			if( null == tupleUTM ) {
				tupleUTM = tupleLatLng.toUTMRef();
				tupleUTMRef.put(oneTuple.getPhotoId(), tupleUTM);
			}
			
			eastingSums += tupleUTM.getEasting();
			northingSums += tupleUTM.getNorthing();
			
			latZone = tupleUTM.getLatZone();
			lngZone = tupleUTM.getLngZone();
		}
		int totalMembers = clusterMembers.size();
		// we'll use the last tupleUTM zone info and hope we're not spanning UTM zones!
		UTMRef midpointUTM = new UTMRef(
				eastingSums / totalMembers, northingSums / totalMembers, 
				latZone, lngZone);
		
		clusterMean = midpointUTM.toLatLng();
	}

}

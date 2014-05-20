package is.bthj.itu.datamining.flicksters.kmeans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;
import is.bthj.itu.datamining.flicksters.data.ClusterCentroid;

public class KMeanClusterOfClusterCentroids extends KMeanCluster<ClusterCentroid, LatLng> {
	
	private LatLng clusterMean;
	
	private Map<Integer, UTMRef> tupleUTMRef;
	
	public KMeanClusterOfClusterCentroids() {
		
		this.clusterMembers = new HashSet<ClusterCentroid>();
		this.tupleUTMRef = new HashMap<Integer, UTMRef>();
	}

	@Override
	public float getTupleDistanceToClusterMean(ClusterCentroid tuple) {
		
		if( null == clusterMean ) updateClusterMean();
		
		return (float) clusterMean.distance( tuple.getCentroid() );
	}

	@Override
	public LatLng getClusterMean() {

		return clusterMean;
	}
	@Override
	public void setClusterMean( LatLng mean ) {
		this.clusterMean = mean;
	}

	@Override
	public float getSumOfSquaredDistancesToMean() {
		float squaredDistancesSum = 0;
		for( ClusterCentroid oneTuple : clusterMembers ) {
			squaredDistancesSum += Math.pow( getTupleDistanceToClusterMean(oneTuple), 2 );
		}
		return squaredDistancesSum;
	}

	@Override
	public void updateClusterMean() {

		double eastingSums = 0, northingSums = 0;
		// TODO: Handle the case where cluster coordinates span different UTM zones - if needed?
		char latZone = '\u0000';
		int lngZone = 0;
		int clusterMembersOfMembersCount = 0;
		for( ClusterCentroid oneTuple : clusterMembers ) {
			
			UTMRef tupleUTM = tupleUTMRef.get( oneTuple.hashCode() );
			if( null == tupleUTM ) {
				tupleUTM = oneTuple.getCentroid().toUTMRef();
				tupleUTMRef.put( oneTuple.hashCode(), tupleUTM );
			}
			// let the average be weighted by the member count from the centroid's cluster
			eastingSums += tupleUTM.getEasting() * oneTuple.getMemberCount();
			northingSums += tupleUTM.getNorthing() * oneTuple.getMemberCount();
			clusterMembersOfMembersCount += oneTuple.getMemberCount();
			
			latZone = tupleUTM.getLatZone();
			lngZone = tupleUTM.getLngZone();
		}
		UTMRef midpointUTM = new UTMRef(
				eastingSums / clusterMembersOfMembersCount, 
				northingSums / clusterMembersOfMembersCount, 
				latZone, lngZone );
		
		clusterMean = midpointUTM.toLatLng();
	}

}

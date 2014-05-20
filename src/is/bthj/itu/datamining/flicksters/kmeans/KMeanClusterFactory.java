package is.bthj.itu.datamining.flicksters.kmeans;

import is.bthj.itu.datamining.flicksters.data.ClusterCentroid;
import is.bthj.itu.datamining.flicksters.data.FlickrPhoto;

public class KMeanClusterFactory {

	@SuppressWarnings("unchecked")
	public static <T,M> KMeanCluster<T,M> createKMeanClusterWithSeedObject( T tuple ) {
		KMeanCluster<T,M> cluster;
		if( tuple instanceof FlickrPhoto ) {
			cluster = (KMeanCluster<T,M>) new KMeanClusterOfFlickrPhotos();
			cluster.addToCluster(tuple);
		} else if ( tuple instanceof ClusterCentroid ) {
			cluster = (KMeanCluster<T,M>) new KMeanClusterOfClusterCentroids();
			cluster.addToCluster(tuple);
		} else {
			cluster = null;
		}
		return cluster;
	}
}

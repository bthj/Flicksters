package is.bthj.itu.datamining.flicksters.execution;

import is.bthj.itu.datamining.flicksters.data.FlickrPhoto;
import is.bthj.itu.datamining.flicksters.data.FlickrPhotosPreprocessor;
import is.bthj.itu.datamining.flicksters.kmeans.KMeanCluster;
import is.bthj.itu.datamining.flicksters.kmeans.KMeans;

import java.util.List;

import uk.me.jstott.jcoord.LatLng;

/**
 * Organizes Flickr photo tuples into k partitions
 * by using the k-means method.
 * Prints out the partitioning result to a JSON file.
 * Keeps on creating cluster partitions until the execution is interrupted.
 * 
 * @author bthj
 *
 */

public class FlickrPhotoPartitioning {
	

	public static void main(String[] args) {
		String dataFilename = null;
		int k = 0;
		if( args.length != 2 ) {
			System.err.println(
					"A parameter with the data file name and a parameter with an integer value for k is required.");
			System.exit(0);
		} else {
			dataFilename = args[0];
			k = Integer.parseInt(args[1]);
		}
		
		List<FlickrPhoto> flickrPhotos = FlickrPhotosPreprocessor.getFlickrPhotos(dataFilename);
		
		KMeans<FlickrPhoto, LatLng> kMeans = new KMeans<FlickrPhoto, LatLng>();
		while( true ) {
			String kMeansClusterGroupFileName = 
					ClusterDataFileHelper.clusterGroupPrefix+k+"__"+(new java.util.Date().getTime())+".json";
			System.out.println("Starting on: " + kMeansClusterGroupFileName );
			
			List<KMeanCluster<FlickrPhoto, LatLng>> clusters = 
					kMeans.kMeansPartition( k, flickrPhotos );
			
			ClusterDataFileHelper.writeFlickrPhotoClusterGroupToJSONFile(
					clusters, 
					kMeansClusterGroupFileName,
					false );
			
			System.out.println("Wrote: " + kMeansClusterGroupFileName);
		}
	}
}

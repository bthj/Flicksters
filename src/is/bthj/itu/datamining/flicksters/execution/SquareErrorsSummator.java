package is.bthj.itu.datamining.flicksters.execution;

import is.bthj.itu.datamining.flicksters.data.FileWritingHelper;
import is.bthj.itu.datamining.flicksters.data.FlickrPhoto;
import is.bthj.itu.datamining.flicksters.data.FlickrPhotosPreprocessor;
import is.bthj.itu.datamining.flicksters.kmeans.KMeanCluster;
import is.bthj.itu.datamining.flicksters.kmeans.KMeans;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.me.jstott.jcoord.LatLng;

/**
 * Computes sums of squared errors in k-means partitions 
 * for varying (given) values of k
 * in a set og Flickr photos. 
 * 
 * @author bthj
 * 
 */

public class SquareErrorsSummator<T,M> {
	
	/**
	 * As on page 368 of Data Mining Concepts and Techniques, 3rd edition.
	 */
	public float getSumOfSquaredError( List<KMeanCluster<T,M>> clusters ) {
		float sumOfSquaredError = 0;
		for( KMeanCluster<T,M> oneCluster : clusters ) {
			sumOfSquaredError += oneCluster.getSumOfSquaredDistancesToMean();
		}
		return sumOfSquaredError;
	}
	

	public static void main(String[] args) {
		String dataFilename = null;
		int kFrom = 0, kTo = 0;
		if( args.length != 3 ) {
			System.err.println(
					"Required parameters missing: \n"+
					"A parameter with the data file name and \n" + 
					"two integer parameters defining a range of values of k \n" +
					"for which inclusive to compute sums of squared error from the k-means method.");
			System.exit(0);
		} else {
			dataFilename = args[0];
			kFrom = Integer.parseInt(args[1]);
			kTo = Integer.parseInt(args[2]);
		}
		
		SquareErrorsSummator<FlickrPhoto, LatLng> errorSummator = 
				new SquareErrorsSummator<FlickrPhoto, LatLng>();
		
		List<FlickrPhoto> flickrPhotos = FlickrPhotosPreprocessor.getFlickrPhotos(dataFilename);
		
		
		KMeans<FlickrPhoto,LatLng> kMeans = new KMeans<>();
		// collection of sums of squared errors, for different values of k, to save to file for plotting:
		Map<Integer, Float> kVSsumOfSquaredErrors = new TreeMap<Integer, Float>(); 
		for( int k = kFrom; k <= kTo; k++ ) {
			
			List<KMeanCluster<FlickrPhoto, LatLng>> clusters = kMeans.kMeansPartition( k, flickrPhotos );
			
			float sumOfSquaredError = errorSummator.getSumOfSquaredError(clusters);
			
			kVSsumOfSquaredErrors.put(k, sumOfSquaredError);
			
			String resultString = "Error for k = " + k + ": " + sumOfSquaredError;
			System.out.println( resultString );
//			FileWritingHelper.writeStringToFile(
//					resultString, 
//					"FlickrPhotos__kVSsumOfSquaredErrors__k_2-10__"+(new java.util.Date().getTime())+".txt" );
		}
		
		FileWritingHelper.writeMapToFile( 
				kVSsumOfSquaredErrors, 
				"FlickrPhotos__kVSsumOfSquaredErrors__k_2-10__"+(new java.util.Date().getTime())+".tab" );
	}
}

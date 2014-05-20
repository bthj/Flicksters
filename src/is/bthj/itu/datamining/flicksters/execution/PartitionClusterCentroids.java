package is.bthj.itu.datamining.flicksters.execution;

import is.bthj.itu.datamining.flicksters.data.ClusterCentroid;
import is.bthj.itu.datamining.flicksters.data.FlickrPhoto;
import is.bthj.itu.datamining.flicksters.data.FlickrPhotoCluster;
import is.bthj.itu.datamining.flicksters.data.FlickrPhotosPreprocessor;
import is.bthj.itu.datamining.flicksters.kmeans.KMeanCluster;
import is.bthj.itu.datamining.flicksters.kmeans.KMeanClusterOfFlickrPhotos;
import is.bthj.itu.datamining.flicksters.kmeans.KMeans;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.me.jstott.jcoord.LatLng;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * For an array of cluster groups, 
 * partition all the cluster centroids, 
 * where each centroid has a weight according to count of members in that cluster. 
 * @author bthj
 *
 */
public class PartitionClusterCentroids {
	
	public static void main(String[] args) throws IOException {
		String dataFilename = null;
		int k = 0;
		if( args.length != 2 ) {
			System.err.println("A parameter with the data file name \n"+
								"and an integer parameter for the value of k is needed, \n" + 
								"indicating the cluster partition files to find centroids from.");
			System.exit(0);
		} else {
			dataFilename = args[0];
			k = Integer.parseInt(args[1]);
		}
		
		final String finalDataFilename = dataFilename;
		final int finalK = k;
		final ObjectMapper mapper = new ObjectMapper();
		
		// let's find all cluster group files, read the info from them into a list of ClusterCentroid:
	    final PathMatcher matcher = 
	    		FileSystems.getDefault().getPathMatcher(
	    				"glob:./kMeansClusterGroups/"+ClusterDataFileHelper.clusterGroupPrefix + k + "__*");
	    Files.walkFileTree(Paths.get("./"), new SimpleFileVisitor<Path>() {
	    	
	    	List<ClusterCentroid> clusterCentroids = new ArrayList<ClusterCentroid>();
	    	
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	            if (matcher.matches(file)) {
	                System.out.println(file);
	                
	                // read from one cluster group json file:
					Map<String, FlickrPhotoCluster> flickrPhotoClusterGroup = 
	                		mapper.readValue(new File(file.toString()), 
	                				new TypeReference<Map<String, FlickrPhotoCluster>>() { } );
	                
	                // collect all centroids from that cluster group
	                for( FlickrPhotoCluster oneCluster : flickrPhotoClusterGroup.values() ) {
	                	
	                	if( !Double.isNaN(oneCluster.getCentroidLatitude()) && 
	                			!Double.isNaN(oneCluster.getCentroidLongitude()) ) {
	                		
		                	ClusterCentroid clusterCentroid = new ClusterCentroid();
		                	clusterCentroid.setCentroidLatitude( oneCluster.getCentroidLatitude() );
		                	clusterCentroid.setCentroidLongitude( oneCluster.getCentroidLongitude() );
		                	clusterCentroid.setMemberCount( oneCluster.getMemberCount() );
		                	clusterCentroids.add( clusterCentroid );	                		
	                	}
	                }
	            }
	            return FileVisitResult.CONTINUE;
	        }
	        
	        // Print each directory visited.
	        @Override
	        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
	            System.out.format("Directory: %s%n", dir);
	            
	            System.out.println("Collected #" + clusterCentroids.size() + " centroids");
	            
	            if( clusterCentroids.size() > finalK ) {
	            	
	            	KMeans<ClusterCentroid, LatLng> kMeans = new KMeans<ClusterCentroid, LatLng>();
		            
		            // Let's partition all those cluster centroids into clusters!
		            
		            List<KMeanCluster<ClusterCentroid, LatLng>> clusters = 
		            		kMeans.kMeansPartition( finalK, clusterCentroids );
		            
		            // and get photo clusters based on those cluster centroid clusters!
		            List<KMeanCluster<FlickrPhoto, LatLng>> flickrPhotoClusters = 
		            		getFlickrClustersPoulatedWithPhotoTuples(finalDataFilename, clusters);
		            
		            ClusterDataFileHelper.writeFlickrPhotoClusterGroupToJSONFile(
		            		flickrPhotoClusters, 
		            		ClusterDataFileHelper.clusterGroupCentroidsPrefix + 
		            			finalK + "__" + (new java.util.Date().getTime())+".json", 
		            		true );	
	            }

	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
	            return FileVisitResult.CONTINUE;
	        }
	    });
	}
	
	
	
	private static List<KMeanCluster<FlickrPhoto, LatLng>> getFlickrClustersPoulatedWithPhotoTuples ( 
			String dataFilename, List<KMeanCluster<ClusterCentroid, LatLng>> clusters ) {
		
		List<FlickrPhoto> flickrPhotos = FlickrPhotosPreprocessor.getFlickrPhotos(dataFilename);
		
		List<KMeanCluster<FlickrPhoto, LatLng>> flickrPhotoClusters = 
				getFlickrPhotoClustersFromCentroidClusters(clusters);
		
		populateClustersWithTuples(flickrPhotoClusters, flickrPhotos);
		
		return flickrPhotoClusters;
	}
	
	
	private static <T, M> void populateClustersWithTuples( 
			List<KMeanCluster<T, M>> clusters, List<T> data ) {
		
		KMeans<T, M> kMeans = new KMeans<T, M>();
		
		for( T oneTuple : data ) {
			
			kMeans.assignTupleToCluster( oneTuple, clusters );
		}
	}
	
	private static List<KMeanCluster<FlickrPhoto, LatLng>> getFlickrPhotoClustersFromCentroidClusters( 
			List<KMeanCluster<ClusterCentroid, LatLng>> clusters ) {
		
		List<KMeanCluster<FlickrPhoto, LatLng>> flickrPhotoClusters = 
				new ArrayList<KMeanCluster<FlickrPhoto,LatLng>>();
		
		for( KMeanCluster<ClusterCentroid, LatLng> oneCentroidCluster : clusters ) {
			
			KMeanCluster<FlickrPhoto, LatLng> flickrPhotoCluster = 
					new KMeanClusterOfFlickrPhotos();
			flickrPhotoCluster.setClusterMean( oneCentroidCluster.getClusterMean() );
			
			flickrPhotoClusters.add( flickrPhotoCluster );
		}
		
		return flickrPhotoClusters;
	}
}

package is.bthj.itu.datamining.flicksters.execution;

import is.bthj.itu.datamining.flicksters.data.FlickrPhoto;
import is.bthj.itu.datamining.flicksters.data.FlickrPhotoCluster;
import is.bthj.itu.datamining.flicksters.kmeans.KMeanCluster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.me.jstott.jcoord.LatLng;

import com.fasterxml.jackson.databind.ObjectMapper;


public class ClusterDataFileHelper {
	
	private static final Set<String> tagsStopList = new HashSet<String>();
	static {
		tagsStopList.add("");
		tagsStopList.add("copenhagen");
		tagsStopList.add("denmark");
		tagsStopList.add("københavn");
		tagsStopList.add("kobenhavn");
		tagsStopList.add("europe");
		tagsStopList.add("danmark");
		tagsStopList.add("copenhague");
		tagsStopList.add("kopenhagen");
		tagsStopList.add("nordic");
		tagsStopList.add("scandinavia");
		tagsStopList.add("northerneurope");
		tagsStopList.add("cph");
	}
	

	public static final String clusterGroupPrefix = "kMeansClusterGroupForK";
	
	public static final String clusterGroupCentroidsPrefix = "kMeansClusterCentroidsGroupForK";
	
	
	public static String findMostFrequentTagInFlickrPhotos( 
			List<FlickrPhoto> flickrPhotos) {
		String mostFrequentTag = null;
		
		Map<String, Integer> tagCounts = new HashMap<String, Integer>();
		
		for( FlickrPhoto onePhoto : flickrPhotos ) {
			String[] tags = onePhoto.getTags().split("\\s+");
			for( String oneTag : tags ) {
				if( ! tagsStopList.contains(oneTag) && 
						! oneTag.matches("[^\\s]*:[^\\s]*=[^\\s]*") &&
						! oneTag.matches("[0-9]+") ) {
					Integer oneTagCount = tagCounts.get(oneTag);
					if( null == oneTagCount ) {
						tagCounts.put(oneTag, 1);
					} else {
						tagCounts.put(oneTag, oneTagCount + 1);
					}	
				}
			}
		}
		
		Map.Entry<String, Integer> maxEntry = null;
		for( Map.Entry<String, Integer> oneEntry : tagCounts.entrySet() ) {
			
			if( maxEntry == null || oneEntry.getValue().compareTo(maxEntry.getValue()) > 0 ) {
				maxEntry = oneEntry;
			}
		}
		if( null != maxEntry ) {
			// let's return the first key with the max value (there might be more than one)
			mostFrequentTag = maxEntry.getKey();
		}
		
		return mostFrequentTag;
	}
	
	public static void writeFlickrPhotoClusterGroupToJSONFile( 
			List<KMeanCluster<FlickrPhoto, LatLng>> clusters, 
			String fileName, 
			boolean includeTuples ) {
		
		Map<String, FlickrPhotoCluster> flickrPhotoClusterGroup = 
				new LinkedHashMap<String, FlickrPhotoCluster>();
		
		int clusterCount = 0;
		for( KMeanCluster<FlickrPhoto, LatLng> oneKmeansCluster : clusters ) {
			
			FlickrPhotoCluster photoCluster = new FlickrPhotoCluster();
			photoCluster.setCentroidLatitude( oneKmeansCluster.getClusterMean().getLat() );
			photoCluster.setCentroidLongitude( oneKmeansCluster.getClusterMean().getLng() );
			photoCluster.setMemberCount( oneKmeansCluster.getClusterMembers().size() );
			
			List<FlickrPhoto> flickrPhotosInCluster =
					new ArrayList<FlickrPhoto>(oneKmeansCluster.getClusterMembers());
			if( includeTuples ) photoCluster.setTuples( flickrPhotosInCluster );
			photoCluster.setMostFrequentTag(
					findMostFrequentTagInFlickrPhotos(flickrPhotosInCluster) );
			
			flickrPhotoClusterGroup.put( "Cluster"+ ++clusterCount, photoCluster );
		}
		
		// see https://github.com/FasterXML/jackson-databind/
		ObjectMapper mapper = new ObjectMapper();
			
			try {
				mapper.writeValue( new File(fileName), flickrPhotoClusterGroup );
				
			} catch (IOException e) {
				System.err.println(e.getLocalizedMessage());
				e.printStackTrace();
			}
			

	}
}

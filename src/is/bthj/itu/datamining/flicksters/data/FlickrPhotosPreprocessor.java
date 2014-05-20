package is.bthj.itu.datamining.flicksters.data;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FlickrPhotosPreprocessor {

	public static List<FlickrPhoto> getFlickrPhotos(String fileName) {
		
		List<FlickrPhoto> flickrPhotos = new ArrayList<FlickrPhoto>();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			String[][] data = CSVFileReader.read( 
					fileName, ",", false );
			
			for( int i=0; i < data.length; i++ ) {
				
				FlickrPhoto oneFlickrPhoto = new FlickrPhoto();
				oneFlickrPhoto.setPhotoId( data[i][0] );
				oneFlickrPhoto.setOwnerId( data[i][1] );
				oneFlickrPhoto.setTags( data[i][2] );
				oneFlickrPhoto.setDateTaken( dateFormatter.parse(data[i][3]) );
				oneFlickrPhoto.setLatitude( Float.parseFloat( data[i][4] ) );
				oneFlickrPhoto.setLongitude( Float.parseFloat( data[i][5] ) );
				
				flickrPhotos.add( oneFlickrPhoto );
			}
			
		} catch (IOException | ParseException e) {

			System.err.println(e.getLocalizedMessage());
		}
		
		return flickrPhotos;
	}
}

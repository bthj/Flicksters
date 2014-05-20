package is.bthj.itu.datamining.flicksters.data;

import java.util.Date;

/**
 * Information about one photo from Flickr.
 * @author bthj
 *
 */
public class FlickrPhoto {

	String photoId;
	String ownerId;
	String tags;
	Date dateTaken;
	float latitude;
	float longitude;
	
	
	@Override
	public boolean equals( Object o ) {
		if( !(o instanceof FlickrPhoto) ) {
			return false;
		}
		return this.photoId.equals( ((FlickrPhoto) o).getPhotoId() );
	}
	
	@Override
	public int hashCode() {
		
		return this.photoId.hashCode();
	}
	
	
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public Date getDateTaken() {
		return dateTaken;
	}
	public void setDateTaken(Date dateTaken) {
		this.dateTaken = dateTaken;
	}
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
}

package groovy.aweber.mp3selector

import groovy.aweber.mp3selector.data.AlbumProperties

/** MVC data model for album properties. */ 
class AlbumPropertiesDataModel {

	AlbumProperties albumProperties  // aktuell ausgewaehltes Album

	boolean isAlbumSelected() {
		return albumProperties != null
	}

	String getGenre() {
		return albumProperties.getGenre()
	}

	String getPoints(String user, String fileName) {
		return albumProperties.getPoints(user, fileName)
	}

	void setCurrentAlbum(AlbumProperties prop) {
		this.albumProperties = prop
	}

	AlbumProperties getAlbumProperties() {
		return albumProperties
	}

}

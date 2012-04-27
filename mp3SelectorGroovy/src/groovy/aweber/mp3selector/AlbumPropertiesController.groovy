package groovy.aweber.mp3selector

import groovy.aweber.mp3selector.data.AlbumProperties
import groovy.aweber.mp3selector.data.Mp3File
import groovy.aweber.mp3selector.data.AlbumPropertiesAccessor
import groovy.aweber.mp3selector.util.Player;

/** MVC controller for album properties. */
class AlbumPropertiesController {
	AlbumPropertiesDataModel _propDataModel
	AlbumPropertiesAccessor _propAccessor
	List _statusObservers

	AlbumPropertiesController(AlbumPropertiesDataModel propModel) {
		_propDataModel = propModel
		_propAccessor = new AlbumPropertiesAccessor()
		_statusObservers = new ArrayList(1)
	}

	void registerForStatus(observer) {
		_statusObservers.add(observer)
	}

	void notifiySongsToPlay(Mp3File song, Object[] songArray, int songIndex) {
		try {
			final List<String> filesToPlay = new ArrayList<String>()
			for (int i = songIndex; i < songArray.length; i++) {
				filesToPlay.add(((Mp3File)songArray[i]).path)
			}
			Player.play(filesToPlay)
		}
		catch (IOException e) {
			sendErrorStatus(e.getMessage())
		}
	}

	void notifyPointsChanged(String user, Mp3File song, String points) {
		if ((user != null) && (song != null)) {
			String s = AlbumProperties.getUserPointsProperty(user, song.getFilename())
			_propDataModel.getAlbumProperties().put(s, points)
		}
	}

	void notifiyPropertySave(String artist, String album) {
		if (_propDataModel.isAlbumSelected() && (artist != null) && (album != null)) {
			try {
				_propAccessor.saveAlbumProperties(SelectorConfig.getAlbumPath(artist, album), album, _propDataModel.getAlbumProperties())
			}
			catch (IOException e) {
				sendErrorStatus(e.getMessage())
			}
		}
	}

	void notifiyGenreSelected(String genre) {
		if (_propDataModel.isAlbumSelected()) {
			_propDataModel.getAlbumProperties().put(AlbumProperties.PROP_GENRE, genre)
		}
	}

	void notifyAlbumSelected(String artist, String album) {
		final String albumPath = SelectorConfig.getAlbumPath(artist, album)
		AlbumProperties albumProperties = _propAccessor.getAlbumProperties(albumPath)
		if (albumProperties.isEmpty()) {
			try {
				albumProperties = _propAccessor.createPropertyFile(
					AlbumPropertiesAccessor.DEFAULT_USERS,  albumPath, album)
			}
			catch (IOException e) {
				sendErrorStatus(e.getMessage())
			}
		}
		_propDataModel.setCurrentAlbum(albumProperties)
	}
	
	private void sendErrorStatus(String errorMessage) {
		for (Object observer : _statusObservers) {
				observer.error(errorMessage)
		}
	}
}



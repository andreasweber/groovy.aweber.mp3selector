package groovy.aweber.mp3selector.data

/** 
 * Reads the music collection. Assumes structure artist/album/songs.
 * 
 * Album contents are read lazy, the music files of an album are loaded if needed.
 */
class Mp3CollectionReader {
	AlbumPropertiesAccessor _propHandler = new AlbumPropertiesAccessor()

	/** read whole music collection. */
	Mp3Collection readMp3Collection(String rootDir) throws IOException {
		Mp3Collection coll = new Mp3Collection(this)
		File root = new File(rootDir)
		root.eachDir { File artistDir ->
			artistDir.eachDir { File albumDir ->
				// we store the albumDir only, and load the contained music files if needed
				coll.addAlbum(artistDir.getName(), albumDir)
			}
		}
		return coll
	}

	/** read single album. */
	void readAlbum(String artist, File albumDir, Mp3Collection coll) throws IOException {
		AlbumProperties albumProps = _propHandler.getAlbumProperties(albumDir.getCanonicalPath())
		String genre = albumProps.getGenre()
		Set<String> userSet = albumProps.getUsers()
		albumDir.eachFileMatch(Mp3File.MP3_PATTERN) { File f ->
			Mp3File file = new Mp3File(artist: artist, album: albumDir.name,
					fileSize: f.length(), path : f.path)
			Map userPointMap = new HashMap()
			for (String user : userSet) {
				def points = albumProps.getPoints(user, file.getFilename())
				userPointMap.put(user, points)
			}
			coll.addFile(file, genre, userPointMap)
		}
	}
}
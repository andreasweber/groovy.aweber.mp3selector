package groovy.aweber.mp3selector.data

/** reads the music collection. Assumes structure artist/album/songs */
class Mp3CollectionReader {
	AlbumPropertiesAccessor _propHandler = new AlbumPropertiesAccessor()

	Mp3Collection readMp3Collection(String rootDir) throws IOException {
		Mp3Collection coll = new Mp3Collection()
		File root = new File(rootDir)
		root.eachDir { File artistDir ->
			coll.addArtist(artistDir.getName())
			artistDir.eachDir { File albumDir ->
				coll.addAlbum(albumDir.getName())
				AlbumProperties albumProps = _propHandler.getAlbumProperties(albumDir.getCanonicalPath())
				String genre = albumProps.getGenre()
				Set<String> userSet = albumProps.getUsers()
				albumDir.eachFileMatch(Mp3File.MP3_PATTERN) { File f ->
					Mp3File file = new Mp3File(artist: artistDir.name, album: albumDir.name,
							fileName: f.name, fileSize: f.length(), path : f.path)
					Map userPointMap = new HashMap()
					for (String user : userSet) {
						def points = albumProps.getPoints(user, file.fileName)
						userPointMap.put(user, points)
					}
					coll.addFile(file, genre, userPointMap)
				}
			}
		}
		return coll
	}
}

package groovy.aweber.mp3selector.data

/** create, read and save album properties. */
class AlbumPropertiesAccessor {
	static final PROPERTY_FILE_NAME = "props.txt"
	static final DEFAULT_USERS = Arrays.asList("Andreas", "Steffi") // TODO in config

	AlbumProperties createPropertyFile(List<String> users, String albumPath, String album) throws IOException {
		AlbumProperties props = new AlbumProperties()
		String propFilePath = albumPath + File.separator + PROPERTY_FILE_NAME
		File propFile = new File(propFilePath)
		if (propFile.exists()) {
			println("Property-File existiert")
			props = getAlbumProperties(propFilePath)
		}
		else {
			props.put(AlbumProperties.PROP_GENRE, AlbumProperties.DEFAULT_GENRE)
			File albumFile = new File(albumPath)
			albumFile.eachFileMatch(Mp3File.MP3_PATTERN) { File f ->
				String fileName = f.name
				for (String user : users) {
					String pointsProp = AlbumProperties.getUserPointsProperty(user, fileName)
					if (!props.containsKey(pointsProp)) {
						props.put(pointsProp, AlbumProperties.DEFAULT_POINTS)
					}
				}
			}
			propFile.withOutputStream { outStream ->
				props.store(outStream, album)
			}
		}
		return props
	}

	AlbumProperties getAlbumProperties(String path) {
		AlbumProperties props = new AlbumProperties()
		try {
			new File(path + File.separator + PROPERTY_FILE_NAME).withInputStream { inStream ->
				props.load(inStream)
			}
		} catch (FileNotFoundException e) {
			// that's ok, prop file doesn't exist yet
		}
		return props
	}

	void saveAlbumProperties(String path, String album, Properties props) throws IOException {
		new File(path + File.separator + PROPERTY_FILE_NAME).withOutputStream { outStream ->
			props.store(outStream, album)
		}
	}

}
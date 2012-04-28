package groovy.aweber.mp3selector

class SelectorConfig {
	static final String PROPERTY_FILE = "selector.props"
	static final String PROP_PLAYER_PATH = "player"
	static final String PROP_MUSIC_ROOT_DIR = "root"
	static final String PROP_PLAYLIST_SIZE = "playlist"
	static final String PROP_DEFAULT_USER = "user"
	
	static Properties _defaultProps
	static Properties _commandLineProps

	// static constructor
	static {
		_defaultProps = new Properties()
		URL url = ClassLoader.getSystemResource(PROPERTY_FILE)
		try {
			_defaultProps.load(url.openStream())
		} catch (Exception e) {
			throw new RuntimeException("Could not load property file: " + PROPERTY_FILE)
		}
	}

	private static String getProp(String key) {
		if (_commandLineProps.get(key) != null) {
			return _commandLineProps.get(key)
		}
		return _defaultProps.get(key)
	}

	static String getPlayerPath() {
		return getProp(PROP_PLAYER_PATH)
	}

	static int getPlaylistSize() {
		return Integer.valueOf(getProp(PROP_PLAYLIST_SIZE))
	}

	static String getMusicRootDir() {
		return getProp(PROP_MUSIC_ROOT_DIR)
	}

	static String getAlbumPath(String artist, String album) {
		getMusicRootDir() + File.separator + artist + File.separator + album
	}

	static void setCommandLineProps(Properties clProps) {
		_commandLineProps = clProps
	}
	
	static String getDefaultUser() {
		return getProp(PROP_DEFAULT_USER)
	}

}

package groovy.aweber.mp3selector.data

/** infos for a single music file. */
class Mp3File {
	static final SONG_PREFIX = '\\A\\s*\\d{0,2}\\s*[-]+\\s*'
	static final MP3_SUFFIX = '.([mM][pP]3)\\z'
	static final MP3_PATTERN = ~/.*\.mp3/

	String artist
	String album
	int fileSize // bytes
	String path

	@Override
	String toString() {
		return getSong()
	}

	String getSong() {
		return getFilename().replaceFirst(SONG_PREFIX, "").replaceFirst(MP3_SUFFIX, "")
	}
	
	String getFilename() {
		return path.substring(path.lastIndexOf(File.separator) + 1)
	}
}

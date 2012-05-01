package groovy.aweber.mp3selector.data

/** contains the properties for a single music album. */
class AlbumProperties extends Properties {
	static final PROP_GENRE = "genre"
	static final GENRE_ROCK = "ROCK"
	static final GENRE_METAL = "METAL"
	static final GENRE_PROG = "PROG"
	static final GENRE_POST = "POST"
	static final GENRE_PUNK = "PUNK"
	static final GENRE_ALT = "ALTERNATIVE"
	static final DEFAULT_GENRE = GENRE_ROCK
	static final DEFAULT_POINTS = "4"

	static String getUserPointsProperty(String user, String fileName) {
		return user + "_" + fileName
	}

	String getGenre() {
		return getProperty(PROP_GENRE)
	}

	Set<String> getUsers() {
		Set<String> userSet = new HashSet<String>(1)
		for (String p: keySet()) {
			if (p.indexOf("_") > 0) {
				String user = p.substring(0, p.indexOf("_"))
				userSet.add(user)
			}
		}
		return userSet
	}

	String getPoints(String user, String fileName) {
		String key = getUserPointsProperty(user, fileName)
		return getProperty(key, DEFAULT_POINTS)
	}
}

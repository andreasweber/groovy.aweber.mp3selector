package groovy.aweber.mp3selector.data

import groovy.aweber.mp3selector.data.Mp3File

/** contains the whole music collection. */
class Mp3Collection {
	int _numberOfArtists
	int _numberOfAlbums
	long _smallestFileSize
	List<Mp3File> _mp3List = new ArrayList<Mp3File>()
	// key: artist, value: Map (key: album, value: List of Mp3File))
	Map<String, TreeMap> _artistMap = new TreeMap<String, TreeMap>()
	// key: genre (String), value: List of Mp3File
	Map<String, List> _genreClassification = new HashMap<String, List>(3)
	// key: user (String), value: Map (key: Integer, value: List of Mp3File)
	Map<String, Map> _userClassification = new HashMap<String, Map>(2)

	void addArtist(String artist) {
		_numberOfArtists++
	}

	void addAlbum(String album) {
		_numberOfAlbums++
	}

	int getNumberOfAlbums() {
		return _numberOfAlbums
	}

	void setNumberOfAlbums(int numberOfAlbums) {
		numberOfAlbums = numberOfAlbums
	}

	int getNumberOfFiles() {
		return _mp3List.size()
	}

	int getNumberOfArtists() {
		return _numberOfArtists
	}

	void setNumberOfArtists(int numberOfArtists) {
		numberOfArtists = numberOfArtists
	}

	void addFile(Mp3File mp3, String genre, Map userPointMap) {
		_mp3List.add(mp3)
		if (mp3.artist != null) {
			Map<String, List> albumMap = _artistMap.get(mp3.artist)
			if (albumMap == null) {
				albumMap = new TreeMap<String, List>()
				_artistMap.put(mp3.artist, albumMap)
			}
			List<Mp3File> songs = albumMap.get(mp3.album)
			if (songs == null) {
				songs = new ArrayList<Mp3File>(10)
				albumMap.put(mp3.album, songs)
			}
			songs.add(mp3)
		}
		if (genre != null) {
			List l = _genreClassification.get(genre)
			if (l == null) {
				l = new ArrayList(100)
				_genreClassification.put(genre, l)
			}
			l.add(mp3)
		}
		if (userPointMap != null) {
			Iterator userIt = userPointMap.keySet().iterator()
			while (userIt.hasNext()) {
				String user = (String) userIt.next()
				Integer points = (Integer) userPointMap.get(user)
				Map m = _userClassification.get(user)
				if (m == null) {
					m = new HashMap(2)
					_userClassification.put(user, m)
				}
				List l = (List) m.get(points)
				if (l == null) {
					l = new ArrayList(100)
					m.put(points, l)
				}
				l.add(mp3)
			}
		}
	}

	Mp3File getFile(int index) {
		return _mp3List.get(index)
	}

	List getGenreCollection(String genre) {
		if (_genreClassification.get(genre) != null) {
			return _genreClassification.get(genre)
		}
		return new ArrayList(0)
	}

	Set getUserCollection(String user, Integer points) {
		Set resultSet = new HashSet()
		Map m = _userClassification.get(user)
		if (m != null) {
			Iterator it = m.keySet().iterator()
			while (it.hasNext()) {
				Integer p = (Integer) it.next()
				if (p.intValue() >= points.intValue()) {
					List l = m.get(p)
					resultSet.addAll(l)
				}
			}
		}
		return resultSet
	}

	List<Mp3File> getMp3List() {
		return _mp3List
	}

	Set getArtists() {
		return _artistMap.keySet()
	}

	Set getAlbums(String artist) {
		if (_artistMap.get(artist) != null) {
			return _artistMap.get(artist).keySet()
		}
		return new HashSet(0)
	}

	List getMp3List(String artist, String album) {
		Map<String, List> albumMap = _artistMap.get(artist)
		if (albumMap != null) {
			return albumMap.get(album)
		}
		return new ArrayList(0)
	}

	Set getUsers() {
		return _userClassification.keySet()
	}

}
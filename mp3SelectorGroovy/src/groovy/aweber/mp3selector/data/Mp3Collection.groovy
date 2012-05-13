package groovy.aweber.mp3selector.data

import java.util.concurrent.ConcurrentHashMap
import groovy.aweber.mp3selector.data.Mp3File

/** contains the whole music collection. */
class Mp3Collection {
	// contains all music files
	List<Mp3File> _mp3List = new ArrayList<Mp3File>()
	// key: artist, value: Map (key: album, value: List of Mp3File))
	Map _artistMap = new TreeMap()
	// key: genre (String), value: List of Mp3File
	Map<String, List> _genreClassification = new ConcurrentHashMap<String, List>(3)
	// key: user (String), value: Map (key: Integer, value: List of Mp3File)
	Map<String, Map> _userClassification = new ConcurrentHashMap<String, Map>()

	int _numberOfAlbums
	Mp3CollectionReader _reader // needed for lazy loading of music files
	boolean _loaded = false

	Mp3Collection(reader) {
		_reader = reader
	}

	/** albumDir is stored, album music files are loaded lazy when needed. */
	void addAlbum(String artist, File albumDir) {
		_numberOfAlbums++
		Map albumMap = _artistMap.get(artist)
		if (albumMap == null) {
			albumMap = new TreeMap()
			_artistMap.put(artist, albumMap)
		}
		albumMap.put(albumDir.getName(), albumDir)
	}

	int getNumberOfArtists() {
		return _artistMap.size()
	}

	int getNumberOfAlbums() {
		return _numberOfAlbums
	}

	int getNumberOfFiles() {
		return _mp3List.size()
	}

	/** ensure that all music files are loaded. */
	void ensureAllLoaded() {
		if (!_loaded) {
			for (final String artist : _artistMap.keySet()) {
				def albumMap = _artistMap.get(artist)
				for (final String album : albumMap.keySet()) {
					def songsOrAlbumdir = albumMap.get(album)
					if (!isAlbumLoaded(songsOrAlbumdir)) {
						// song list of this album not loaded yet
						_reader.readAlbum(artist, songsOrAlbumdir, this)
					}
				}
			}
			_loaded = true
		}
	}

	void addFile(Mp3File mp3, String genre, Map userPointMap) {
		_mp3List.add(mp3)
		if (mp3.artist != null) {
			Map albumMap = _artistMap.get(mp3.artist)
			if (albumMap == null) {
				albumMap = new TreeMap()
				_artistMap.put(mp3.artist, albumMap)
			}
			def songs = albumMap.get(mp3.album)
			if (!isAlbumLoaded(songs)) {
				// first song that's loaded for this album - create song list
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

	List getGenreCollection(String genre) {
		ensureAllLoaded()
		if (_genreClassification.get(genre) != null) {
			return _genreClassification.get(genre)
		}
		return Collections.EMPTY_LIST
	}

	Set getUserCollection(String user, Integer points) {
		ensureAllLoaded()
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
		ensureAllLoaded()
		return _mp3List
	}

	Set getArtists() {
		return _artistMap.keySet()
	}

	Set getAlbums(String artist) {
		if (_artistMap.get(artist) != null) {
			return _artistMap.get(artist).keySet()
		}
		return Collections.EMPTY_SET
	}

	List getMp3List(String artist, String album) {
		Map albumMap = _artistMap.get(artist)
		if (albumMap != null) {
			def a = albumMap.get(album)
			if (!isAlbumLoaded(a)) {
				_reader.readAlbum(artist, a, this)  // lazy reading (a = album file)
				if (!isAlbumLoaded(a)) {
					// there may be empty album folders, or some that don't match mp3 file pattern
					return Collections.EMPTY_LIST
				}
				return albumMap.get(album) // now we can be sure that music files are loaded
			}
			return a // list of music files
		}
		return Collections.EMPTY_LIST
	}

	/** whether the songs of this album have been already loaded. */
	boolean isAlbumLoaded(albumContent) {
		return (albumContent instanceof List)
		// if song list is not loaded yet, the albumContent is 'File albumDir'
	}

}
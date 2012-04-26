package groovy.aweber.mp3selector

import groovy.aweber.mp3selector.data.Mp3Collection
import groovy.aweber.mp3selector.data.Mp3File

/** helper class for creating a playlist. */
class PlaylistCreator {

	/** create playlist for givven genre and minimum points. */
	static List<Mp3File> createPlaylist(
		Mp3Collection coll, String user, Integer minPoints, String genre, int playlistSize) {
		
		println("Anzahl Artisten: " + coll.getNumberOfArtists())
		println("Anzahl Alben: " + coll.getNumberOfAlbums())
		println("Anzahl Mp3 Dateien: " + coll.getNumberOfFiles())

		// filter mp3 List
		List<Mp3File> mp3List = new ArrayList(coll.getMp3List()) // copy!
		if (genre != null) {
			mp3List.retainAll(coll.getGenreCollection(genre))
			println("Genre-Filter: " + genre)
			println("Anzahl Mp3 Dateien nach Genre-Filter: " + mp3List.size())
		}
		if ((user != null) && (minPoints != null)) {
			mp3List.retainAll(coll.getUserCollection(user, minPoints))
			println("User-Filter: " + user + ", Punkte: " + minPoints)
			println("Anzahl Mp3 Dateien nach User-Punkte-Filter: " + mp3List.size())
		}

		// calculate random collection of unique songs
		List<Mp3File> resultList = new ArrayList<Mp3File>(playlistSize)
		if (mp3List.size() > 0) {
			Random random = new Random()
			Set<Integer> alreadySelected = new HashSet<Integer>(playlistSize)
			for (int i=0; i<playlistSize; i++) {
				// random select of mp3 files
				int r = random.nextInt(mp3List.size()) // [0, (mp3List.size-1)]
				if (!alreadySelected.contains(r)) {
					alreadySelected.add(r)
					Mp3File file = mp3List.get(r)
					resultList.add(file)
				}
			}
		}
		return resultList
	}

}

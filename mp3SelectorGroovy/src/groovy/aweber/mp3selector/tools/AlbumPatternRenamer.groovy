package groovy.aweber.mp3selector.tools

/** Alle Album-Verzeichnisse die nicht dem angegebenen Muster entsprechen werden umbenannt. */
class AlbumPatternRenamer {
	static final rootDir = "C:/Musik/MP3"
	static final albumYearPattern1 = /\(\d\d\d\d\)/ // ... (yyyy)
	static final albumYearPattern2 = /(\d\d\d\d) - ([\S ]*)/ // yyyy - ...

	static main(args) {
		// starte mit root dir
		File root = new File(rootDir)
		int counter;

		// Closure mit eigentlicher Logik zum Umbenennen der Alben fuer pattern 1
		def renamer1 = {  File albumDir ->
			def fName = albumDir.name
			def matcher = fName =~ albumYearPattern1
			// wenn Verzeichnisname nicht korrektes Pattern hat (-> "(yyyy)" ist nicht vorne)
			if (matcher.find() && matcher.start() != 0) {
				println("Found album with wrong name: " + fName)
				def startOfYear = matcher.start()
				def album = fName.substring(0, startOfYear - 1)
				def year = matcher.group()
				println("  Album: " + album)
				println("  Year: " + year)
				File newAlbumDir = new File(albumDir.getParent(), year + " " + album)
				println("  Renaming to: " + newAlbumDir.getAbsolutePath())
				if (albumDir.renameTo(newAlbumDir))
					counter++
				else
					throw new RuntimeException("Renaming failed: " + albumDir)
			}
		}

		// Closure mit eigentlicher Logik zum Umbenennen der Alben fuer pattern 2
		def renamer2 = {  File albumDir ->
			def fName = albumDir.name
			def matcher = fName =~ albumYearPattern2
			// wenn Verzeichnisname nicht korrektes Pattern hat (-> "yyyy - " ist vorne)
			if (matcher.find() && matcher.start() == 0) {
				println("Found album with wrong name: " + fName)
				def year = matcher.group(1)
				def album = matcher.group(2)
				println("  Album: " + album)
				println("  Year: " + year)
				File newAlbumDir = new File(albumDir.getParent(), "(" + year + ") " + album)
				println("  Renaming to: " + newAlbumDir.getAbsolutePath())
				if (albumDir.renameTo(newAlbumDir))
					counter++
				else
					throw new RuntimeException("Renaming failed: " + albumDir)
			}
		}

		// Unterhalb von root sind die Artist-Verzeichnisse
		root.eachDir { // Closures fuer alle Album-Verzeichnisse anwenden
			File artistDir ->
			artistDir.eachDir(renamer1)
			artistDir.eachDir(renamer2)
		}

		println("Anzahl umbenannter Verzeichnisse: " + counter)
	}
}
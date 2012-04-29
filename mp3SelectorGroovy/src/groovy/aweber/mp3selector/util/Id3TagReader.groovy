package groovy.aweber.mp3selector.util

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.util.logging.Level;
import java.util.logging.Logger

/** helper class for reading ID3 tags from mp3 file (based on JAudioTagger lib). */
class Id3TagReader {
	
	static Id3Tags getId3Tags(file) {
		Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF)
		AudioFile f = AudioFileIO.read(file)
		Tag tag = f.getTag()
		AudioHeader h = f.getAudioHeader()

		Id3Tags result = new Id3Tags()
		result.setArtist(tag.getFirst(FieldKey.ARTIST))
		result.setAlbum(tag.getFirst(FieldKey.ALBUM))
		result.setTitle(tag.getFirst(FieldKey.TITLE))
		result.setYear(tag.getFirst(FieldKey.YEAR))
		result.setGenre(tag.getFirst(FieldKey.GENRE))
		result.setLengthInSeconds(h.getTrackLength())
		result.setSampleRate(h.getSampleRateAsNumber())
		return result
	}
}
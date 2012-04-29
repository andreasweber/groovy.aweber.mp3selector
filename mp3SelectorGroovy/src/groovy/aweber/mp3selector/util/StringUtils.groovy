package groovy.aweber.mp3selector.util

class StringUtils {

	/** convert seconds to a format "mm:ss", e.g. 311 -> 5:11. */
	static String secondsToPrettyLength(seconds) {
		if (seconds != null) {
			def minutes = (int) seconds / 60
			def remainingSeconds = seconds % 60
			def additionalZero = ""
			if (remainingSeconds < 10) {
				additionalZero = "0"
			}
			return minutes + ":" + additionalZero + remainingSeconds
		}
		return ""
	}
}

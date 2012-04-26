package groovy.aweber.mp3selector.util

import groovy.aweber.mp3selector.SelectorConfig;

/** helper class to call configured music player. */
class Player {
	static Runtime runtime = Runtime.getRuntime()

	static void play(final List<String> filesToPlay) throws IOException {
		final String command = getPlayerCall(filesToPlay)
		runtime.exec(command)
	}

	private static String getPlayerCall(final List<String> filesToPlay) {
		String s = SelectorConfig.getPlayerPath()
		for (final String fileToPlay : filesToPlay) {
			s = s + " \"" + fileToPlay + "\""
		}
		return s
	}
}

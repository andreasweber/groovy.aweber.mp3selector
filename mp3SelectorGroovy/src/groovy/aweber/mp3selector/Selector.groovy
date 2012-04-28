package groovy.aweber.mp3selector

// TODO 's
// - songs dynamisch (nach)laden
// - fix: drag and drop
// - ID3 tags in detail fields
// - neue Genres
// - pictures

/** Main start class. */
class Selector {
	
	static main(args) {
		def cli = new CliBuilder(usage: "Selector")
		cli.h(longOpt: 'help', 'display usage info')
		cli.r(longOpt: 'root', 'music root dir', args: 1, required: true)
		cli.p(longOpt: 'player', 'file path of used player', args: 1)
		cli.u(longOpt: 'user', 'default user', args: 1)
		cli.playlist('number of playlist songs', args: 1)
		def options = cli.parse(args)
		if (options == null || options.h) {
			cli.usage()
			println("(see 'selector.props' for defaults)")
			System.exit(0)
		}
		Properties props = new Properties()
		if (options.player) { 
			def playerPath = new File(options.player)
			if (!playerPath.exists() || !playerPath.canExecute()) {
				println("Can't find music player path: " + playerPath)
				System.exit(0)
			}
			props.put(SelectorConfig.PROP_PLAYER_PATH, options.player)
		}
		if (options.root) {
			def musicPath = new File(options.root)
			if (!musicPath.exists() || !musicPath.canRead()) {
				println("Can't find music root path: " + musicPath)
				System.exit(0)
			}
			props.put(SelectorConfig.PROP_MUSIC_ROOT_DIR, options.root)
		}
		if (options.playlist) {
			def playlistSize = options.playlist
			if (!playlistSize.isNumber()) {
				println("Playlist size must be a number, was: " + playlistSize)
				System.exit(0)
			}
			props.put(SelectorConfig.PROP_PLAYLIST_SIZE, options.playlist)
		}
		if (options.user) {
			props.put(SelectorConfig.PROP_DEFAULT_USER, options.user)
		}
		SelectorConfig.setCommandLineProps(props)
	
		SelectorGUI selectorGui = new SelectorGUI()
		selectorGui.init()
	}

}

package groovy.aweber.mp3selector

import java.awt.*
import java.awt.event.*
import javax.swing.*
import javax.swing.event.*

import groovy.swing.SwingBuilder

import groovy.aweber.mp3selector.data.AlbumProperties
import groovy.aweber.mp3selector.data.Mp3CollectionReader
import groovy.aweber.mp3selector.data.Mp3File
import groovy.aweber.mp3selector.data.Mp3Collection
import groovy.aweber.mp3selector.gui.SelectionListCellRenderer
import groovy.aweber.mp3selector.gui.DragAndDropHandler

class SelectorGUI {
	// Controller
	AlbumPropertiesController _propController
	// Data model
	Mp3Collection _mp3Collection // music files
	AlbumPropertiesDataModel _propModel // album property files
	ListModel _artistListModel, _albumListModel, _songListModel, _selectionListModel
	// for creating GUI elements
	SwingBuilder _swing = new SwingBuilder()

	SelectorGUI() throws IOException {
		_propModel = new AlbumPropertiesDataModel()
		_propController = new AlbumPropertiesController(_propModel)
		_propController.registerForStatus(this)
		_artistListModel = new DefaultListModel()
		_albumListModel = new DefaultListModel()
		_songListModel = new DefaultListModel()
		_selectionListModel = new DefaultListModel()
	}

	/** Create the GUI elements with Groovy SwingBuilder. */
	public void init() {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true)

		def mp3TransferHandler = new DragAndDropHandler()

		// GUI
		def mainPanel = _swing.panel(opaque: true, border: BorderFactory.createEtchedBorder()) {
			boxLayout(axis: BoxLayout.PAGE_AXIS)
			// list panel
			panel(border: BorderFactory.createEtchedBorder()) {
				gridLayout(rows: 1, cols: 3)
				// artist list
				scrollPane() {
					list(id: 'artistList', model: _artistListModel, valueChanged: valueChanged_artistList,
							selectionMode: ListSelectionModel.SINGLE_SELECTION, visibleRowCount: 10)
				}
				// album list
				scrollPane() {
					list(id: 'albumList', model: _albumListModel, valueChanged: valueChanged_albumList,
							selectionMode: ListSelectionModel.SINGLE_SELECTION, visibleRowCount: 10)
				}
				// song list
				scrollPane() {
					list(id: 'songList', model: _songListModel, valueChanged: valueChanged_songList, mouseClicked: mouse_doubleClicked,
							selectionMode: ListSelectionModel.SINGLE_SELECTION, visibleRowCount: 10, dragEnabled: true, transferHandler: mp3TransferHandler)
				}
			}
			// detail panel
			panel(border: BorderFactory.createLoweredBevelBorder()) {
				gridLayout(rows: 4, cols: 2)
				label(" Artist: ")
				textField(id: 'artistField', editable: false)
				label(" Album: ")
				textField(id: 'albumField', editable: false)
				label(" Dateiname: ")
				textField(id: 'filenameField', editable: false)
				label(" Größe (in Byte): ")
				textField(id: 'filesizeField', editable: false)
			}
			// genre panel
			panel() {
				flowLayout()
				buttonGroup(id: 'genreButtons')
				radioButton(id: 'rockButton', AlbumProperties.GENRE_ROCK, actionPerformed: action_genreButton,
						actionCommand: AlbumProperties.GENRE_ROCK, buttonGroup: _swing.genreButtons)
				radioButton(id: 'progButton', AlbumProperties.GENRE_PROG, actionPerformed: action_genreButton,
						actionCommand: AlbumProperties.GENRE_PROG, buttonGroup: _swing.genreButtons)
				radioButton(id: 'punkButton', AlbumProperties.GENRE_PUNK, actionPerformed: action_genreButton,
						actionCommand: AlbumProperties.GENRE_PUNK, buttonGroup: _swing.genreButtons)
			}
			// user points panel
			panel() {
				gridLayout(rows: 1, cols: 3, hgap: 0, vgap: 0)
				panel() {
					boxLayout(axis: BoxLayout.Y_AXIS)
					comboBox(id: 'userComboBox', actionPerformed: action_userComboBox,
							alignmentX: Component.CENTER_ALIGNMENT)
				}
				slider(id: 'pointSlider', stateChanged: pointSlider_stateChanged,
						orientation: JSlider.HORIZONTAL, minimum: 1, maximum: 5, value: 1,
						majorTickSpacing: 2, minorTickSpacing: 1, paintTicks: true, paintLabels: true)
				panel() {
					boxLayout(axis: BoxLayout.Y_AXIS)
					button(id: 'saveButton', "Speichern", actionPerformed: action_saveButton, alignmentX: Component.CENTER_ALIGNMENT)
				}
			}
			// playlist selection button panel
			panel() {
				gridLayout(rows: 1, cols: 4)
				button(id: 'selButton', "Auswahl", actionPerformed: action_selectionButton)
				button(id: 'selGenreButton', "Auswahl (Genre)", actionPerformed: action_selectionButton)
				button(id: 'selPointsButton', "Auswahl (Punkte)", actionPerformed: action_selectionButton)
				button(id: 'selPointsGenreButton', "Auswahl (Genre+Punkte)", actionPerformed: action_selectionButton)
			}
			// playlist panel
			panel() {
				borderLayout()
				scrollPane(constraints: BorderLayout.CENTER) {
					list(id: 'selectionList', model: _selectionListModel, mouseClicked: mouse_doubleClicked,
							selectionMode: ListSelectionModel.SINGLE_SELECTION, visibleRowCount: 2,
							transferHandler: mp3TransferHandler, cellRenderer: new SelectionListCellRenderer())
				}
				panel(constraints: BorderLayout.EAST) {
					gridLayout(rows: 3, cols: 1)
					button(id: 'plusButton', " + ", actionPerformed: action_plusButton)
					button(id: 'minusButton', " - ", actionPerformed: action_minusButton)
					button(id: 'clearButton', " [] ", actionPerformed: action_clearButton)
				}
			}
			// status panel
			panel() {
				flowLayout(alignment: FlowLayout.LEFT)
				label(id: 'statusLabel')
			}
		}

		initValues()

		def frame = _swing.frame(title: "SelectorGUI", defaultCloseOperation: JFrame.EXIT_ON_CLOSE,
				contentPane: mainPanel)
		frame.pack()
		// Frame in Mitte des Bildschirms
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize()
		frame.setLocation((int) (d.width - frame.getSize().width) / 2,
				(int) (d.height - frame.getSize().height) / 2)
		frame.setVisible(true)
	}

	/* closure: playlist "[]" button action */
	def action_clearButton = { actionEvent ->
		_selectionListModel.removeAllElements()
	}

	/* closure: prog/rock/bunk button action */
	def action_genreButton = { actionEvent ->
		_propController.notifiyGenreSelected(actionEvent.getActionCommand())
	}

	/* closure: playlist "-" button action */
	def action_minusButton = { actionEvent ->
		if (_swing.selectionList.getSelectedIndex() != -1) {
			Mp3File song = _swing.selectionList.getSelectedValue()
			int selIndex = _swing.selectionList.getSelectedIndex()
			_selectionListModel.removeElement(song)
			_swing.selectionList.setSelectedIndex(selIndex - 1)
			_swing.selectionList.ensureIndexIsVisible(selIndex - 1)
		}
	}

	/* closure: playlist "+" button action */
	def action_plusButton = { actionEvent ->
		if (_swing.songList.getSelectedIndex() != -1) {
			Mp3File song = _swing.songList.getSelectedValue()
			int selIndex = _swing.selectionList.getSelectedIndex()
			if (_selectionListModel.contains(song)) {
				selIndex = _selectionListModel.indexOf(song)
			} else {
				if (selIndex == -1) {
					// kein Eintrag ausgewaehlt -> an Ende anhaengen
					selIndex = _selectionListModel.getSize()
					_selectionListModel.add(selIndex, song)
				} else {
					// nach aktuell ausgewaehltem Eintrag anhaengen
					_selectionListModel.add(selIndex + 1, song)
					selIndex = _selectionListModel.indexOf(song)
				}
			}
			_swing.selectionList.setSelectedIndex(selIndex)
			_swing.selectionList.ensureIndexIsVisible(selIndex)
		}
	}

	/* closure: save button action */
	def action_saveButton = { actionEvent ->
		String artist = _swing.artistList.getSelectedValue()
		String album = _swing.albumList.getSelectedValue()
		_propController.notifiyPropertySave(artist, album)
		info("Informationen für Album '" + album + "' gespeichert")
	}

	/* closure: (one of) selection button(s) action */
	def action_selectionButton = { actionEvent ->
		String genre = null
		String user = null
		Integer points = null
		if (actionEvent.getSource().equals(_swing.selGenreButton)
		|| actionEvent.getSource().equals(_swing.selPointsGenreButton)) {
			if (_swing.rockButton.isSelected()) {
				genre = AlbumProperties.GENRE_ROCK
			} else if (_swing.progButton.isSelected()) {
				genre = AlbumProperties.GENRE_PROG
			} else if (_swing.punkButton.isSelected()) {
				genre = AlbumProperties.GENRE_PUNK
			}
		}
		if (actionEvent.getSource().equals(_swing.selPointsButton)
		|| actionEvent.getSource().equals(_swing.selPointsGenreButton)) {
			user = _swing.userComboBox.getSelectedItem()
			points = _swing.pointSlider.getValue()
		}
		int playlistSize = SelectorConfig.getPlaylistSize()
		final Collection mp3List = PlaylistCreator.createPlaylist(_mp3Collection, user, points, genre, playlistSize)
		_selectionListModel.removeAllElements()
		for (Mp3File mp3 : mp3List) {
			_selectionListModel.addElement(mp3)
		}
	}

	/* closure: userComboBox action */
	def action_userComboBox = { actionEvent ->
		String user = _swing.userComboBox.getSelectedItem()
		Mp3File song = _swing.songList.getSelectedValue()
		if (user != null && song != null) {
			String points = _propModel.getPoints(user, song.getFilename())
			if (points != null) {
				_swing.pointSlider.setValue(Integer.valueOf(points).intValue())
			}
		}
	}

	/* closure: mouse (double) clicked events */
	def mouse_doubleClicked  = { mouseEvent ->
		if ((mouseEvent.getClickCount() == 2) &&
		((mouseEvent.getSource().equals(_swing.songList) && (_swing.songList.getSelectedIndex() > -1))
		|| (mouseEvent.getSource().equals(_swing.selectionList)) && (_swing.selectionList.getSelectedIndex() > -1))) {

			def clickedList = mouseEvent.getSource()
			def song = clickedList.getSelectedValue()
			def songIndex = clickedList.getSelectedIndex()
			def songArray = ((DefaultListModel) clickedList.getModel()).toArray()
			_propController.notifiySongsToPlay(song, songArray, songIndex)
		}
	}

	/* closure: point slider action */
	def pointSlider_stateChanged = { changeEvent ->
		JSlider slider = changeEvent.getSource()
		if (!slider.getValueIsAdjusting()) {
			String user = _swing.userComboBox.getSelectedItem()
			Mp3File song = _swing.songList.getSelectedValue()
			String points = String.valueOf(slider.getValue())
			_propController.notifyPointsChanged(user, song, points)
		}
	}

	/* closure: artist list changed */
	def valueChanged_artistList = {changeEvent ->
		if (changeEvent.getValueIsAdjusting() == false) {
			if (_swing.artistList.getSelectedIndex() == -1) {
				// No selection
				_albumListModel.removeAllElements()
				_songListModel.removeAllElements()
			} else {
				String artist = _swing.artistList.getSelectedValue()
				Iterator albumIt = _mp3Collection.getAlbums(artist).iterator()
				_albumListModel.removeAllElements()
				while (albumIt.hasNext()) {
					_albumListModel.addElement(albumIt.next())
				}
				_swing.albumList.setSelectedIndex(0)
			}
		}
	}

	/* closure: album list changed */
	def valueChanged_albumList = {changeEvent ->
		if (changeEvent.getValueIsAdjusting() == false) {
			if (_swing.albumList.getSelectedIndex() == -1) {
				// No selection
				_songListModel.removeAllElements()
			} else {
				String artist = _swing.artistList.getSelectedValue()
				String album = _swing.albumList.getSelectedValue()
				Iterator songIt = _mp3Collection.getMp3List(artist, album).iterator()
				_songListModel.removeAllElements()
				while (songIt.hasNext()) {
					_songListModel.addElement(songIt.next())
				}
				_propController.notifyAlbumSelected(artist, album)
				_swing.rockButton.setSelected(false)
				_swing.progButton.setSelected(false)
				_swing.punkButton.setSelected(false)
				String genre = _propModel.getGenre()
				if (genre != null) {
					if (genre.equals(AlbumProperties.GENRE_ROCK)) {
						_swing.rockButton.setSelected(true)
					} else if (genre.equals(AlbumProperties.GENRE_PROG)) {
						_swing.progButton.setSelected(true)
					} else if (genre.equals(AlbumProperties.GENRE_PUNK)) {
						_swing.punkButton.setSelected(true)
					}
				}
				_swing.songList.setSelectedIndex(0)
			}
		}
	}

	/* closure: song list changed */
	def valueChanged_songList = {changeEvent ->
		if (changeEvent.getValueIsAdjusting() == false) {
			if (_swing.songList.getSelectedIndex() == -1) {
				// No selection
				_swing.artistField.setText("")
				_swing.albumField.setText("")
				_swing.filenameField.setText("")
				_swing.filesizeField.setText("")
			} else {
				Mp3File song = _swing.songList.getSelectedValue()
				_swing.artistField.setText(song.artist)
				_swing.albumField.setText(song.album)
				_swing.filenameField.setText(song.getFilename())
				_swing.filesizeField.setText(String.valueOf(song.fileSize))
				String user = _swing.userComboBox.getSelectedItem()
				if ((user != null) && _propModel.isAlbumSelected()) {
					String points = _propModel.getPoints(user, song.getFilename())
					if (points != null) {
						_swing.pointSlider.setValue(Integer.valueOf(points).intValue())
					}
				}
			}
		}
	}

	private void initValues() throws IOException {
		Mp3CollectionReader mp3Reader = new Mp3CollectionReader()
		_mp3Collection = mp3Reader.readMp3Collection(SelectorConfig.getMusicRootDir())

		Iterator userIt = _mp3Collection.getUsers().iterator()
		while (userIt.hasNext()) {
			_swing.userComboBox.addItem(userIt.next())
		}
		_swing.userComboBox.setMaximumSize(_swing.userComboBox.getPreferredSize())

		Iterator artistIt = _mp3Collection.getArtists().iterator()
		while (artistIt.hasNext()) {
			_artistListModel.addElement(artistIt.next())
		}

		ListModel m = _swing.artistList.getModel()
		_swing.artistList.setSelectedIndex(0)
		if (_swing.userComboBox.getItemCount() > 0) {
			_swing.userComboBox.setSelectedIndex(0)
		}
		info(_mp3Collection.getNumberOfFiles() + " MP3-Dateien eingelesen")
	}

	void info(String text) {
		_swing.statusLabel.setText("Hinweis: " + text)
	}

	void error(String text) {
		_swing.statusLabel.setText("Fehler: " + text)
	}


}
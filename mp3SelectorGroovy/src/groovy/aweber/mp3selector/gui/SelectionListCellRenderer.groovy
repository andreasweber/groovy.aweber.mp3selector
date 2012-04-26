package groovy.aweber.mp3selector.gui

import groovy.aweber.mp3selector.data.Mp3File;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

class SelectionListCellRenderer extends DefaultListCellRenderer {
	Component getListCellRendererComponent(JList list, Object value,
	int index, boolean isSelected, boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index,
				isSelected, cellHasFocus);
		if ((c instanceof JLabel) && (value instanceof Mp3File)) {
			Mp3File f = (Mp3File) value;
			((JLabel) c).setText(f.artist + " - " + f.album + " - " + f.song);
		}
		return c;
	}
}

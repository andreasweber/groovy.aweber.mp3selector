package groovy.aweber.mp3selector.gui

import groovy.aweber.mp3selector.data.Mp3File

import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.IOException

import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.JList
import javax.swing.TransferHandler

/** Drag'n'Drop transfer: Selected song from song list can be transfered to playlist. */ 
class DragAndDropHandler extends TransferHandler {
	DataFlavor _dataFlavorMp3File
	String _dataFlavorMp3FileType = DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Mp3File.class.getName()
	JList _source = null

	DragAndDropHandler() {
		try {
			_dataFlavorMp3File = new DataFlavor(_dataFlavorMp3FileType)
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Unable to create data flavor", e)
		}
	}

	boolean importData(JComponent c, Transferable t) {
		JList target = null
		Mp3File aMp3File = null
		if (!canImport(c, t.getTransferDataFlavors())) {
			return false
		}
		try {
			target = (JList) c
			if (hasFlavor(t.getTransferDataFlavors())) {
				aMp3File = (Mp3File) t.getTransferData(_dataFlavorMp3File)
			} else {
				return false
			}
		} catch (UnsupportedFlavorException ufe) {
			System.out.println("importData: unsupported data flavor")
			ufe.printStackTrace()
			return false
		} catch (IOException ioe) {
			System.out.println("importData: I/O exception")
			ioe.printStackTrace()
			return false
		}

		// We'll drop at the current selected index.
		int index = target.getSelectedIndex()

		// Prevent the user from dropping data back on itself.
		if (_source.equals(target)) {
			return true
		}

		DefaultListModel listModel = (DefaultListModel) target.getModel()
		int max = listModel.getSize()
		if (index < 0) {
			index = max
		} else {
			index++
			if (index > max) {
				index = max
			}
		}
		listModel.add(index, aMp3File)
		return true
	}

	private boolean hasFlavor(DataFlavor[] flavors) {
		if (_dataFlavorMp3File == null) {
			return false
		}

		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].equals(_dataFlavorMp3File)) {
				return true
			}
		}
		return false
	}

	boolean canImport(JComponent c, DataFlavor[] flavors) {
		if (hasFlavor(flavors)) {
			return true
		}
		return false
	}

	protected Transferable createTransferable(JComponent c) {
		if (c instanceof JList) {
			_source = (JList) c
			def transferObject = _source.getSelectedValue()
			if (transferObject != null) {
				return new Mp3FileTransferable(transferObject)
			}
		}
		return null
	}

	int getSourceActions(JComponent c) {
		return TransferHandler.COPY
	}

	class Mp3FileTransferable implements Transferable {
		Mp3File data

		public Mp3FileTransferable(Mp3File file) {
			data = file
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor)
			}
			return data
		}

		public DataFlavor[] getTransferDataFlavors() {
			return [_dataFlavorMp3File]
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (_dataFlavorMp3File.equals(flavor)) {
				return true
			}
			return false
		}
	}
}

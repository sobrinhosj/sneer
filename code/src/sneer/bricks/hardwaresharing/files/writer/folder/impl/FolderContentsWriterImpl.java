package sneer.bricks.hardwaresharing.files.writer.folder.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardwaresharing.files.map.visitors.FileMapGuide;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.hardwaresharing.files.writer.folder.FolderContentsWriter;

class FolderContentsWriterImpl implements FolderContentsWriter {

	@Override
	public void writeToFolder(File folder, FolderContents contents) throws IOException {
		my(FileMapGuide.class).guide(new FileWritingVisitor(folder), contents);
	}

	@Override
	public void mergeOver(File existingFolder, FolderContents contents) throws IOException {
		check(existingFolder);
		writeToFolder(existingFolder, contents);
	}

	private void check(final File existingFolder) {
		if (!existingFolder.isDirectory()) throw new IllegalArgumentException("existingFolder must be a folder: " + existingFolder);
		if (!existingFolder.exists()) throw new IllegalArgumentException("Folder does not exist: " + existingFolder);
	}

}
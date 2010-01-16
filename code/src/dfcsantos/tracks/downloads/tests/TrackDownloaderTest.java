package dfcsantos.tracks.downloads.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;
import sneer.bricks.pulp.keymanager.Seal;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.downloads.TrackDownloader;
import dfcsantos.tracks.endorsements.TrackEndorsement;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class TrackDownloaderTest extends BrickTest {

	@Bind private final FileClient _fileClient = mock(FileClient.class);
	@Bind private final Seals _seals = mock(Seals.class);

	@Test(timeout = 3000)
	public void trackDownload() throws Exception {
		final Sneer1024 hash1 = my(Crypto.class).digest(new byte[] { 1 });
		final Sneer1024 hash2 = my(Crypto.class).digest(new byte[] { 2 });
		final Sneer1024 hash3 = my(Crypto.class).digest(new byte[] { 3 });

		checking(new Expectations(){{
			oneOf(_seals).ownSeal(); will(returnValue(newSeal(1)));
			oneOf(_seals).ownSeal(); will(returnValue(newSeal(2)));
			exactly(1).of(_fileClient).startFileDownload(new File(peerTracksFolder(), "ok.mp3"), 41, hash1);
			allowing(_seals).ownSeal();
		}});

		my(Wusic.class).allowTracksDownload(true);
		my(Wusic.class).tracksDownloadAllowanceSetter().consume(1);

		my(TrackDownloader.class).setActive(true);

		aquireEndorsementTuple(hash1, 41, "songs/subfolder/ok.mp3");

		my(Wusic.class).allowTracksDownload(false);
		aquireEndorsementTuple(hash2, 42, "songs/subfolder/notOk1.mp3");

		my(Wusic.class).allowTracksDownload(true);
		useUpAllowance();
		aquireEndorsementTuple(hash3, 43, "songs/subfolder/notOk2.mp3");
	}

	private void useUpAllowance() throws IOException {
		final File fileWith1MB = createTmpFileWithRandomContent(1048576);
		my(IO.class).files().copyFileToFolder(fileWith1MB, peerTracksFolder());
	}

	private File peerTracksFolder() {
		return new File(my(FolderConfig.class).tmpFolderFor(TracksFolderKeeper.class), "peertracks");
	}

	private void aquireEndorsementTuple(final Sneer1024 hash1, int lastModified, String track) {
		my(TupleSpace.class).acquire(new TrackEndorsement(track, lastModified, hash1));
		my(TupleSpace.class).waitForAllDispatchingToFinish();
	}

	private Seal newSeal(int b) {
		return new Seal(new ImmutableByteArray(new byte[] { (byte) b }));
	}

}
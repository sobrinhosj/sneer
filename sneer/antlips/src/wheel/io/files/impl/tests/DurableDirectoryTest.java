package wheel.io.files.impl.tests;

import java.io.File;
import java.io.IOException;

import wheel.io.files.Directory;
import wheel.io.files.impl.DurableDirectory;
import wheel.io.files.tests.DirectoryTestBase;

public class DurableDirectoryTest extends DirectoryTestBase {

	@Override
	protected Directory subject() throws IOException {
		DurableDirectory result = new DurableDirectory(tmpDirectory().getAbsolutePath());
		result.mkdirs();
		return result;
	}

	@Override
	protected String absoluteFileName(String filename) {
		return new File(tmpDirectory(), filename).getAbsolutePath();
	}

}
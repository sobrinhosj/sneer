package sneer.boot;

import static sneer.boot.SneerDirectories.writeMainAppFile;

import java.io.IOException;

import sneer.server.Command;
import wheelexperiments.Log;

public class VersionUpdateCommand implements Command {

	private final int _version;
	private final byte[] _contents;

	public VersionUpdateCommand(int version, byte[] contents) {
		_version = version;
		_contents = contents;
	}

	public void execute() {
		Log.log("Salvando atualização para o Sneer...");

		try {
			writeMainAppFile(_contents, _version);
			Log.log("Atualização salva.");
		} catch (IOException e) {
			Log.log(e);
		}
	}



}

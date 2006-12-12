package sneer.server;

import wheelexperiments.Log;

public class LogMessage implements Command {

	private final String _message;

	public LogMessage(String message) {
		_message = message;
	}

	public void execute() {
		Log.log(_message);
		return;
	}

}

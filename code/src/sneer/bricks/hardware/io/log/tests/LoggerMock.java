package sneer.bricks.hardware.io.log.tests;

import basis.lang.Consumer;
import sneer.bricks.hardware.io.log.Logger;

class LoggerMock implements Logger {

	private static final String SPACES = "                                                                                                                                                                                                                                                                                             ";
	private final Consumer<String> _messageConsumer;

	
	LoggerMock(Consumer<String> messageConsumer) {
		_messageConsumer = messageConsumer;
	}


	@Override
	public void log(String message, Object... messageInsets) {
		String formatted = weaveInsets(message, messageInsets);
		if (formatted.contains("Heartbeat")) return; ///////////////////// Message to filter out.
		//if (formatted.contains("Tuple")) return; ///////////////////// Message to filter out.
		
		_messageConsumer.consume(appendCaller(formatted));
	}

	
	private String appendCaller(String message) {
		return message.length() > SPACES.length()
			? message + "     || " + caller()
			: message + SPACES.substring(message.length()) + " || " + caller();
	}

	
	private String caller() {
		//return "" + Thread.currentThread().getStackTrace()[4];
		//Refactor: With the unification of Logger and BlinkingLights, this below will no longer be necessary and the above can be used.
		int frame = 4;
		while (true) {
			String candidate = "" + Thread.currentThread().getStackTrace()[frame];
			if (candidate.indexOf("BlinkingLights") == -1)
				return candidate;
			frame++;
		}
	}


	String weaveInsets(String message, Object... messageInsets) {
		StringBuilder result = new StringBuilder();
		formatInsets(result, message, messageInsets);
		return result.toString();
	}


	static private void formatInsets(StringBuilder builder, String message, Object... messageInsets) {
		String[] parts = message.split("\\{\\}");
		int i = 0;
		while (true) {
			if (i == parts.length) break;
			builder.append(parts[i]);
		
			if (i == messageInsets.length) break;
			builder.append(messageInsets[i]);
			i++;
		}
	}
	
}

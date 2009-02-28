package sneer.brickness;

public class Environments {
	
	public interface Memento {}
	
	private final static ThreadLocal<Environment> _environment = new ThreadLocal<Environment>() { @Override	protected Environment initialValue() { return null;};};
	
	public static <T> void runWith(Environment environment, Runnable runnable) {
		final Environment previous = current();
		_environment.set(environment);
		try {
			runnable.run();
		} finally {
			_environment.set(previous);
		}
	}
	
	public static <T> T bind(Environment environment, Class<T> intrface) {
		return EnvironmentInvocationHandler.newInstance(environment, intrface);
	}
	
	public static Memento memento() {
		return new MementoImpl(current());
	}
	
	public static <T> void runWith(Memento memento, Runnable runnable) {
		runWith(((MementoImpl)memento)._environment, runnable);
	}

	public static <T> T my(Class<T> dependency) {
		final Environment environment = current();
		if (environment == null)
			throw new IllegalStateException("Unable to provide thread " + Thread.currentThread() + " with implementation for " + dependency);
		if (dependency == Environment.class)
			return (T) environment;
		return environment.provide(dependency);
	}

	public static Environment compose(final Environment... environments) {
		return new Environment() { @Override public <T> T provide(Class<T> intrface) {
			for (Environment e : environments) {
				final T result = e.provide(intrface);
				if (null != result)
					return result;
			}
			return null;
		}};
	}
	
	private static Environment current() {
		return _environment.get();
	}
}


class MementoImpl implements Environments.Memento {
	MementoImpl(Environment environment) {_environment = environment;}
	final Environment _environment;
}
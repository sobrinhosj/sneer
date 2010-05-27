package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import sneer.bricks.hardware.io.prevalence.flag.PrevalenceFlag;
import sneer.bricks.hardware.io.prevalence.map.PrevalenceMap;
import sneer.bricks.hardware.io.prevalence.nature.Transaction;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Immutable;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.ReadOnly;
import sneer.foundation.lang.types.Classes;

class Bubble implements InvocationHandler {
	
	static CacheMap<Object, Object> _proxiesByObject = CacheMap.newInstance();
	

	static <T> T wrap(T newObject) {
		T result = proxyFor(newObject);
		_proxiesByObject.put(newObject, result);
		return result;
	}


	private static <T> T proxyFor(T object) {
		return proxyFor(object, null, null, null, object);
	}

	
	private static <T> T proxyFor(Object startObject, BuildingTransaction previousBubble, Method query, Object[] queryArgs, T endObject) {
		InvocationHandler handler = new Bubble(startObject, previousBubble, query, queryArgs);
		return (T)Proxy.newProxyInstance(endObject.getClass().getClassLoader(), Classes.allInterfacesOf(endObject.getClass()), handler);
	}

	
	private Bubble(Object delegate, BuildingTransaction previousBubble, Method query, Object[] queryArgs) {
		_delegate = delegate;
		
		_previousBubble = previousBubble;
		_query = query;
		_queryArgs = queryArgs;
	}


	private final Object _delegate;
	
	private final BuildingTransaction _previousBubble;
	private final Method _query;
	private final Object[] _queryArgs;
	
	
	@Override
	public Object invoke(Object proxyImplied, Method method, Object[] args) throws Throwable {
		return isTransaction(method)
			? handleTransaction(method, args)
			: handleQuery(method, args);
	}


	private boolean isTransaction(Method method) {
		if (method.getReturnType() == Void.TYPE) return true;
		if (method.getAnnotation(Transaction.class) != null) return true;
		return false;
	}
	
	
	private Object handleTransaction(Method method, Object[] args) {
		BuildingTransaction transaction = new Invocation(tillHere(), method, args);
		Object result = my(PrevalenceFlag.class).isInsidePrevalence()
			? executeDirectly(transaction)
			: PrevaylerHolder._prevayler.execute(transaction);
		
		return wrapIfNecessary(result, method, null, true);
	}


	private Object executeDirectly(BuildingTransaction transaction) {
		System.out.println("bubble: " + tillHere().produce());
		return transaction.produce();
	}

	
	private BuildingTransaction tillHere() {
		return (_delegate != null)
			? new MapLookup(_delegate)
			: new Invocation(_previousBubble, _query, _queryArgs);
	}


	private Object handleQuery(Method query, Object[] args) throws Throwable {
		Object result = invokeOnDelegate(query, args);
		return wrapIfNecessary(result, query, args, false);
	}
	
	
	private Object invokeOnDelegate(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(navigateToReceiver(), args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}


	private Object navigateToReceiver() {
		return _delegate != null
			? _delegate
			: tillHere().produce();
	}

	
	private Object wrapIfNecessary(final Object returned, final Method method, final Object[] args, final boolean isTransaction) {
		if (returned == null) return null;

		Class<?> type = method.getReturnType();
		if (isReadOnly(type)) return returned;
		if (type.isArray()) return returned;
		
		return _proxiesByObject.get(returned, new Producer<Object>() { @Override public Object produce() {
			if (isRegistered(returned))
				return proxyFor(returned);

			if (isTransaction)
				throw new IllegalStateException();
			
			return proxyFor(null, tillHere(), method, args, returned);
		}});
	}

	
	private boolean isRegistered(Object object) {
		return my(PrevalenceMap.class).isRegistered(object);
	}

	
	static boolean isReadOnly(Class<?> type) {
		if (Immutable.isImmutable(type)) return true;
		if (ReadOnly.class.isAssignableFrom(type)) return true;

		return false;
	}
}

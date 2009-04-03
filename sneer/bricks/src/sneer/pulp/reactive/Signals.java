package sneer.pulp.reactive;

import sneer.brickness.OldBrick;
import sneer.commons.lang.Functor;
import wheel.lang.Consumer;

public interface Signals extends OldBrick {  
	
	<T> Signal<T> constant(T value);
	
	Consumer<Object> sink();
	
	<A, B> Signal<B> adapt(Signal<A> input, Functor<A, B> functor);
	
}

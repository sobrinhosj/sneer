package functional.adapters.freedom2;

import org.junit.Ignore;

import functional.SovereignCommunity;
import functional.adapters.SneerCommunity;
import functional.freedom2.Freedom2TestBase;

@Ignore
public class SneerFreedom2Test extends Freedom2TestBase {

	@Override
	protected SovereignCommunity createNewCommunity() {
	    return new SneerCommunity(tmpDirectory());
	}

}

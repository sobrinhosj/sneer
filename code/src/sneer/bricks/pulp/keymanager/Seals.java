package sneer.bricks.pulp.keymanager;

import sneer.bricks.network.social.Contact;
import sneer.foundation.brickness.Brick;

@Brick
public interface Seals {

	Seal ownSeal();

	Seal sealGiven(Contact contact);
	Contact contactGiven(Seal peersPublicKey);

	Seal unmarshall(byte[] publicKeyBytes);

}

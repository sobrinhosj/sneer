package sneer.kernel.pointofview.impl;

import sneer.kernel.business.contacts.ContactAttributes;
import sneer.kernel.communication.Channel;
import sneer.kernel.communication.Packet;
import sneer.kernel.pointofview.Contact;
import sneer.kernel.pointofview.Party;
import sneer.kernel.pointofview.PointOfViewPacket;
import wheel.graphics.JpgImage;
import wheel.lang.Omnivore;
import wheel.reactive.Signal;
import wheel.reactive.Source;
import wheel.reactive.impl.SourceImpl;
import wheel.reactive.lists.ListSignal;
import wheel.reactive.lists.ListSource;
import wheel.reactive.lists.impl.ListSourceImpl;

public class ThirdParty implements Party {

	public ThirdParty(ContactAttributes attributes, Signal<Boolean> isOnline, Channel channel) {
		
		if (attributes == null)
			throw new IllegalArgumentException();
			
		_attributes = attributes;
		_isOnline = isOnline;
		_fakeContacts = createFakeContacts();
		
		channel.input().addReceiver(packetReceiver());
	}

	private final ContactAttributes _attributes;
	private final Signal<Boolean> _isOnline;
	private final ListSource<Contact> _fakeContacts;
	private final Source<String> _name = new SourceImpl<String>("[Implement Name Cache]"); //Implement
	private final Source<String> _thoughtOfTheDay = new SourceImpl<String>("");
	private final Source<JpgImage> _picture = new SourceImpl<JpgImage>(null);
	private final Source<String> _profile = new SourceImpl<String>("");
	
	private Omnivore<Packet> packetReceiver() {
		return new Omnivore<Packet>() { @Override public void consume(Packet packet) {
			if (!packet._contactId.equals(_attributes.id())) return;
			PointOfViewPacket pointOfViewPacket = (PointOfViewPacket)packet._contents;
			if (!_name.output().currentValue().equals(pointOfViewPacket._name))
				_name.setter().consume(pointOfViewPacket._name);
			if (!_thoughtOfTheDay.output().currentValue().equals(pointOfViewPacket._thoughtOfTheDay))
				_thoughtOfTheDay.setter().consume(pointOfViewPacket._thoughtOfTheDay);
			if ((_picture.output().currentValue()==null)||(!_picture.output().currentValue().equals(pointOfViewPacket._picture)))
				_picture.setter().consume(pointOfViewPacket._picture);
			if (!_profile.output().currentValue().equals(pointOfViewPacket._profile))
				_profile.setter().consume(pointOfViewPacket._profile);
		}};
	}
	
	@Override
	public Signal<String> name() {
		return _name.output();
	}
	
	private ListSource<Contact> createFakeContacts() {
		ListSourceImpl<Contact> result = new ListSourceImpl<Contact>();
		result.add(new FakeContact(nick() + " 1"));
		result.add(new FakeContact(nick() + " 2"));
		result.add(new FakeContact(nick() + " 3"));
		return result;
	}

	private String nick() {
		return _attributes.nick().currentValue();
	}

	@Override
	public ListSignal<Contact> contacts() {
		return _fakeContacts.output();
	}


	@Override
	public Signal<Boolean> publicKeyConfirmed() {
		return _attributes.publicKeyConfirmed();
	}

	@Override
	public Signal<String> host() {
		return _attributes.host();
	}

	@Override
	public Signal<Integer> port() {
		return _attributes.port();
	}

	@Override
	public Signal<Boolean> isOnline() {
		return _isOnline;
	}
	
	@Override
	public String toString(){
    	return _attributes.nick().currentValue();
    }

	@Override
	public Contact currentContact(String nick) {
		// Implement Auto-generated method stub
		throw new wheel.lang.exceptions.NotImplementedYet();
	}

	@Override
	public Signal<String> publicKey() {
		return _attributes.publicKey();
	}

	public Signal<JpgImage> picture() {
		return _picture.output();
	}

	public Signal<String> profile() {
		return _profile.output();
	}

	public Signal<String> thoughtOfTheDay() {
		return _thoughtOfTheDay.output();
	}
	
}

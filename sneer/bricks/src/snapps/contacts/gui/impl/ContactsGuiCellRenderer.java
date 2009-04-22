package snapps.contacts.gui.impl;

import static sneer.commons.environments.Environments.my;

import java.awt.Component;
import java.awt.Image;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import snapps.contacts.gui.impl.ContactsGuiImpl.ContactLabelProvider;
import sneer.pulp.connection.ConnectionManager;
import sneer.pulp.contacts.Contact;
import sneer.pulp.reactive.Signal;

class ContactsGuiCellRenderer implements ListCellRenderer {

	private final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
	private final ContactLabelProvider _labelProvider;
	private final ConnectionManager _connections = my(ConnectionManager.class);

	ContactsGuiCellRenderer(ContactLabelProvider labelProvider) {
		_labelProvider = labelProvider;
	}
	
	@Override
	public Component getListCellRendererComponent(JList ignored, Object value, int ignored2, boolean isSelected, boolean cellHasFocus) {

		Contact contact = getElement(value);
		
		Signal<String> slabel = _labelProvider.labelFor(contact);
		Signal<Image> sicon = _labelProvider.imageFor(contact);

		ImageIcon icon = new ImageIcon(sicon.currentValue());
		JLabel label = (JLabel) renderer.getListCellRendererComponent(ignored, value, ignored2, isSelected, cellHasFocus);
		label.setIcon(icon);
		label.setText(slabel.currentValue());
		
		Signal<Boolean> isOnline = _connections.connectionFor(contact).isOnline();
		label.setEnabled(!isOnline.currentValue());

		return label;
	}
	protected Contact getElement(Object value) {
		return (Contact) value;
	}
}
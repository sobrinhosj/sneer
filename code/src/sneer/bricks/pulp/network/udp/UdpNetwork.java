package sneer.bricks.pulp.network.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import sneer.foundation.brickness.Brick;
import sneer.foundation.lang.Consumer;


@Brick
public interface UdpNetwork {

	static final int MAX_PACKET_PAYLOAD_SIZE = 1024 * 2; //2k max. 1k is ideal for UDP Packets

	UdpSocket openSocket(int portNumber) throws SocketException;

	interface UdpSocket {
		void initReceiver(Consumer<DatagramPacket> receiver);
		void send(DatagramPacket packet) throws IOException;
		void crash();
	}
	
}

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Network extends Thread {
	public void run() {
		try (
				ServerSocket receiveFromAtm = new ServerSocket(Globals.atmSendPort);
				Socket sendToAtm = new Socket("localhost", Globals.atmReceivePort);

				ServerSocket receiveFromPu = new ServerSocket(Globals.puSendPort);
				Socket sendToPu = new Socket("localhost", Globals.puReceivePort);

				ServerSocket receiveFromDb = new ServerSocket(Globals.dbSendPort);
				Socket sendToDb = new Socket("localhost", Globals.dbReceivePort)
		) {
			
		} catch (IOException e) {
		}
	}
}

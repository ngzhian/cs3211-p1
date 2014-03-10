import java.io.IOException;
import java.net.ServerSocket;


public class Network extends Thread {
	public void run() {
		try (
				ServerSocket receiveFromAtm = new ServerSocket(Globals.atmSendPort);
				ServerSocket sendToAtm = new ServerSocket(Globals.atmReceivePort);

				ServerSocket receiveFromPu = new ServerSocket(Globals.puSendPort);
				ServerSocket sendToPu = new ServerSocket(Globals.puReceivePort);

				ServerSocket receiveFromDb = new ServerSocket(Globals.dbSendPort);
				ServerSocket sendToDb = new ServerSocket(Globals.dbReceivePort)) {
			
		} catch (IOException e) {
		}
	}
}

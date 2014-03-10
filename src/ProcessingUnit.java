import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/*
 * What an Atm uses to access the accounts.
 * A processing unit communicates with the Database via Sockets
 * to fulfil the requests of the User (Atm).
 */
public class ProcessingUnit extends Thread {
	@Override
	public void run() {
		try (ServerSocket receive = new ServerSocket(Globals.networkToPu)) {
			while (true) {
				Socket session = receive.accept();
				forkWorker(session);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void forkWorker(Socket session) throws IOException {
		ProcessingWorker worker = new ProcessingWorker(session);
		worker.start();
	}
}


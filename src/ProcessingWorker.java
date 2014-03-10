import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProcessingWorker extends Thread {
	Socket socket;

	public ProcessingWorker(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		handleRequest();
	}

	private void handleRequest() {
		try (
				BufferedReader inFromAtm = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter outToAtm = new PrintWriter(socket.getOutputStream(), true);
				Socket dbConnection = new Socket("localhost", Globals.puToDbPort);
				BufferedReader inFromDb = new BufferedReader(new InputStreamReader(dbConnection.getInputStream()));
				PrintWriter outToDb = new PrintWriter(dbConnection.getOutputStream(), true)
				) {
			if (Globals.isTimeout()) {
				outToAtm.write("timeout");
				return;
			}
			
			String requestFromAtm = inFromAtm.readLine();
			outToDb.write(requestFromAtm);
			
			String responseFromDb = inFromDb.readLine();
			outToAtm.write(responseFromDb);
		} catch (IOException e) {
			
		}
	}
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
				) {
			String requestFromAtm = inFromAtm.readLine();

			if (Globals.isTimeout()) {
				outToAtm.println("timeout");
			} else {
				String responseFromDb = getResponseFromDb(requestFromAtm);
				outToAtm.println(responseFromDb);
			}
		} catch (IOException e) {
			
		}
	}

	private String getResponseFromDb(String requestFromAtm)
			throws UnknownHostException, IOException {
		Socket dbConnection = new Socket("localhost", Globals.puToDbPort);
		BufferedReader inFromDb = new BufferedReader(new InputStreamReader(dbConnection.getInputStream()));
		PrintWriter outToDb = new PrintWriter(dbConnection.getOutputStream(), true);

		outToDb.println(requestFromAtm);
		String responseFromDb = inFromDb.readLine();
		if (responseFromDb == "timeout") {
			dbConnection.close();
			inFromDb.close();
			outToDb.close();
			responseFromDb = getResponseFromDb(requestFromAtm);
		}
		dbConnection.close();
		inFromDb.close();
		outToDb.close();
		return responseFromDb;
	}
}

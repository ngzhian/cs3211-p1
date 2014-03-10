import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
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
		BufferedReader requestReader;
		try {
			requestReader = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
		} catch (IOException e) {
			System.out
					.println("CRITICAL ERROR: Couldn't get input stream in Processing Unit.");
			throw new RuntimeException();
		}

		try (Socket sendToDb = new Socket("localhost", Globals.puToNetwork);
				BufferedReader responseFromDb = new BufferedReader(
						new InputStreamReader(sendToDb.getInputStream()));
				BufferedWriter outToDb = new BufferedWriter(
						new OutputStreamWriter(sendToDb.getOutputStream()))) {
			String line;
			while ((line = requestReader.readLine()) != null) {
				outToDb.write(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

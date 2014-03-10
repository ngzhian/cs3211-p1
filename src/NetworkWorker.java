import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class NetworkWorker extends Thread {
	private Socket socket;
	private int sendPortNum;

	public NetworkWorker(Socket socket, int sendPort) {
		this.socket = socket;
		this.sendPortNum = sendPort;
	}

	@Override
	public void run() {
		System.out.println("Network worker started.");

		BufferedReader inFromSender;
		try {
			inFromSender = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
		} catch (IOException e1) {
			System.out.println("CRITICAL ERROR: Couldn't get input stream in worker. Halting.");
			throw new RuntimeException();
		}

		try (Socket sendTo = new Socket("localhost", this.sendPortNum)) {
			BufferedWriter outToReceiver = new BufferedWriter(
					new OutputStreamWriter(sendTo.getOutputStream()));

			String lineFromSender;
			while ((lineFromSender = inFromSender.readLine()) != null) {
				outToReceiver.write(lineFromSender);
			}
		} catch (IOException e) {
		}
	}
}

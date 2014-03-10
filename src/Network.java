import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class Network extends Thread {
	private int receivePortNum;
	private int sendPortNum;

	private int percentageSuccess;
	private Random randomGenerator;

	public Network(int receivePort, int sendPort, int percentageSuccess) {
		this.receivePortNum = receivePort;
		this.sendPortNum = sendPort;
		this.percentageSuccess = percentageSuccess;
		this.randomGenerator = new Random();
	}

	public void run() {
		try (ServerSocket receivePort = new ServerSocket(this.receivePortNum)) {
			while (true) {
				Socket receivedFrom = receivePort.accept();

				boolean isConnectionSuccessful = randomGenerator.nextInt(100) < this.percentageSuccess;
				if (!isConnectionSuccessful) {
					dropConnection(receivedFrom);
					continue;
				} else {
					forwardMessage(receivedFrom);
				}
			}
		} catch (IOException e) {

		}
	}
	
	private void dropConnection(Socket receivedFrom) throws IOException {
		receivedFrom.close();
	}

	private void forwardMessage(Socket receivedFrom) throws IOException,
			UnknownHostException {
		BufferedReader inFromSender = new BufferedReader(
				new InputStreamReader(receivedFrom.getInputStream()));

		try (Socket sendTo = new Socket("localhost",
				this.sendPortNum)) {
			BufferedWriter outToReceiver = new BufferedWriter(
					new OutputStreamWriter(sendTo.getOutputStream()));

			String lineFromSender;
			while ((lineFromSender = inFromSender.readLine()) != null) {
				outToReceiver.write(lineFromSender);
			}
		}
	}
}

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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

	@Override
	public void run() {
		try (ServerSocket receivePort = new ServerSocket(this.receivePortNum)) {
			while (true) {
				Socket receivedFrom = receivePort.accept();
				PrintWriter writer = new PrintWriter(receivedFrom.getOutputStream(), true);
				boolean isConnectionSuccessful = randomGenerator.nextInt(100) < this.percentageSuccess;
				if (!isConnectionSuccessful) {
					writer.println("timeout");
					continue;
				} else {
					writer.println("connected");
					System.out.println("Created Network worker for ports: " + this.receivePortNum + " " + this.sendPortNum);
					NetworkWorker worker = new NetworkWorker(receivedFrom,
							this.sendPortNum);
					worker.start();
				}
			}
		} catch (IOException e) {

		}
	}
}

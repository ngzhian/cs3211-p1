import java.io.IOException;
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

        // boolean isConnectionSuccessful = randomGenerator.nextInt(100) <
        // this.percentageSuccess;
        boolean isConnectionSuccessful = true;
        if (!isConnectionSuccessful) {
          dropConnection(receivedFrom);
          continue;
        } else {
          NetworkWorker worker = new NetworkWorker(receivedFrom,
              this.sendPortNum);
          worker.start();
        }
      }
    } catch (IOException e) {

    }
  }

  private void dropConnection(Socket receivedFrom) throws IOException {
    receivedFrom.close();
  }
}

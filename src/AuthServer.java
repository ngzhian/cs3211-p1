import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthServer extends Thread {

  @Override
  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(
        Globals.authNetworkToAuthServer)) {
      while (true) {
        // starts a new thread to handle each client connection
        Socket client = serverSocket.accept();
        AuthServerThread authServerThread = new AuthServerThread(client);
        authServerThread.start();
      }
    } catch (IOException e1) {
      System.out
          .println("ERROR: Couldn't get input stream in worker. Halting.");
      return;
    }
  }
}

class AuthServerThread extends Thread {
	private Socket receiveSocket;

	public AuthServerThread(Socket socket) {
		this.receiveSocket = socket;
	}

	@Override
	public void run() {
		try (BufferedReader inFromClient = new BufferedReader(new InputStreamReader(receiveSocket.getInputStream()));
			 Socket sendSocket = new Socket("localhost", Globals.authServerToAuthNetwork);
			 PrintWriter serverOut = new PrintWriter(sendSocket.getOutputStream(),true);) {
			
			boolean isValid = Integer.parseInt(inFromClient.readLine()) < Globals.accountNumber;
			String message = isValid ? "success" : "invalid account";
			serverOut.println(message);
			
			//Wait until the other side reads everything
			long wait = System.currentTimeMillis() + 1000;
			while(System.currentTimeMillis() < wait);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

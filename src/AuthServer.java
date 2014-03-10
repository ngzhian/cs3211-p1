import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class AuthServer extends Thread {

	@Override
	public void run() {
		try (ServerSocket serverSocket = new ServerSocket(Globals.atmToAuthPort)) {
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
	private Socket client;

	public AuthServerThread(Socket socket) {
		this.client = socket;
	}

	@Override
	public void run() {
		try (BufferedReader inFromAtm = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter outToAtm = new PrintWriter(client.getOutputStream(), true);) {
			if (Globals.isTimeout()) {
				outToAtm.write("timeout");
				return;
			}

			String message;
			boolean isValid = Integer.parseInt(inFromAtm.readLine()) < Globals.numberOfAccounts;
			message = isValid ? "success" : "invalid account";
			outToAtm.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

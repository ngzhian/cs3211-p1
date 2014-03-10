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
	private Socket socket;

	public AuthServerThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (PrintWriter serverOut = new PrintWriter(socket.getOutputStream(),true);
			 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			
			boolean isValid = inFromClient.read() < Globals.accountNumber;
			if(isValid){
				serverOut.println("success");
			} else{
				serverOut.println("invalid account");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

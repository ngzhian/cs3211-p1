import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Atm extends Thread {
	Integer account;
	Integer amount;
	
	public void login(Integer account) {
		System.out.println("Logging in...");
		try (Socket authConnection = new Socket("localhost",
				Globals.atmToAuthNetwork);
				PrintWriter writer = new PrintWriter(
						authConnection.getOutputStream(), true);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(authConnection.getInputStream()));
				ServerSocket serverSocket = new ServerSocket(Globals.authNetworkToAtm);) {
			writer.println(account);

			if(reader.readLine().equals("timeout")){
				throw new IOException();
			}
			
			Socket receivedFrom = serverSocket.accept();
	        BufferedReader reply = new BufferedReader(
					new InputStreamReader(receivedFrom.getInputStream()));
	        String message = reply.readLine();
			boolean isSuccess = message.equals("success");
			if(isSuccess){
				this.account = account;
			} else{
				login(account);
			}
			
		} catch (IOException e) {
			// Connection failed
			System.out.println("Login failed.. trying again");
			login(account);
		}
	}

	public void setWithdrawAmount(Integer amount) {
		this.amount = amount;
	}

	private void withdrawAmount(Integer amount) {
		try (Socket puConnection = new Socket("localhost",
				Globals.atmToPuNetwork)) {
			PrintWriter writer = new PrintWriter(
					puConnection.getOutputStream(), true);
			writer.println("withdraw " + this.account + " " + amount.toString());
		} catch (IOException e) {
			// The connection failed.
			System.out.println(e.getMessage());
			withdrawAmount(amount);
		}
	}

	@Override
	public void run() {
		if (account == null || amount == null) {
			return;
		}
		withdrawAmount(this.amount);
	}
}

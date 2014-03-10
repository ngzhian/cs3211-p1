import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Atm extends Thread {
	Integer account;
	Integer amount;

	public void login(Integer account) {
		System.out.println("Logging in...");
		try (Socket connection = new Socket("localhost", Globals.atmToAuthPort);
				PrintWriter outToAuth = new PrintWriter(connection.getOutputStream(), true);
				BufferedReader inFromAuth = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
			outToAuth.println(account);

			String message = inFromAuth.readLine();
			switch (message) {
			case "timeout":
				throw new IOException();
			case "success":
				System.out.println("Login successful.");
				this.account = account;
				break;
			default:
				System.out.println("Account does not exist!");
				break;
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
		try (Socket connection = new Socket("localhost", Globals.atmToPuPort);
				PrintWriter outToPu = new PrintWriter(connection.getOutputStream(), true);
				BufferedReader inFromPu = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

			String withdrawRequest = "withdraw " + this.account + " " + amount.toString();
			outToPu.println(withdrawRequest);
			
			String response = inFromPu.readLine();
			while (response == "timeout") {
				outToPu.write("ACK");
				outToPu.write(withdrawRequest);
				response = inFromPu.readLine();
			}
			System.out.println(response);
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

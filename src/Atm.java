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
		try (Socket authConnection = new Socket("localhost",
				Globals.atmToAuthPort);
				PrintWriter writer = new PrintWriter(
						authConnection.getOutputStream(), true);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(authConnection.getInputStream()));) {
			writer.println(account);

			String message = reader.readLine();
			switch (message) {
			case "timeout":
				throw new IOException();
			case "success":
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
		try (Socket puConnection = new Socket("localhost", Globals.atmToPuPort);
				PrintWriter outToPu = new PrintWriter(
						puConnection.getOutputStream(), true);
				BufferedReader inFromPu = new BufferedReader(
						new InputStreamReader(puConnection.getInputStream()))) {

			outToPu.println("withdraw " + this.account + " "
					+ amount.toString());
			
			String response = inFromPu.readLine();
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

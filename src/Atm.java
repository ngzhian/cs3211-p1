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
				Globals.atmToAuthNetwork);
				PrintWriter writer = new PrintWriter(
						authConnection.getOutputStream(), true);
				BufferedReader reply = new BufferedReader(
						new InputStreamReader(authConnection.getInputStream()));) {
			writer.println(account);
			writer.flush();
			String replyMessage = reply.readLine();
			boolean isSuccess = replyMessage.equals("success");

			if (isSuccess) {
				System.out.println("Login success.");
				this.account = account;
			} else {
				System.out.println("Bad login details...");
				return;
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
		try (Socket puConnection = new Socket("localhost", Globals.atmToNetwork)) {
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

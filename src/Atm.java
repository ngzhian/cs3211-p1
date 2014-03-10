import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Atm extends Thread {
	Integer account;
	Integer amount;

	public void login(Integer account) {
		this.account = account;
	}
	
	public void setWithdrawAmount(Integer amount) {
		this.amount = amount;
	}

	private void withdrawAmount(Integer amount) {
		try (Socket puConnection = new Socket("localhost", Globals.atmToNetwork)) {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(puConnection.getOutputStream()));
			writer.write(amount.toString());
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

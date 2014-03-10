import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Atm extends Thread {
	Integer account;
	Integer amount;

	public void login(Integer account) {
		System.out.println("Loging in...");
		try (Socket authConnection = new Socket("localhost", Globals.atmToAuthNetwork)){
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(authConnection.getOutputStream()));
			writer.write(account);
			BufferedReader reply = new BufferedReader(new InputStreamReader(authConnection.getInputStream()));
			boolean isSuccess = reply.readLine().equals("success");
			
			if(isSuccess){
				this.account = account;
			} else{
				System.out.println("Bad login details...");
				return;
			}
			
		} catch(IOException e){
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

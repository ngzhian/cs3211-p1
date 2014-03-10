import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/*
 * What an Atm uses to access the accounts.
 * A processing unit communicates with the Database via Sockets
 * to fulfil the requests of the User (Atm).
 */
public class ProcessingUnit extends Thread {
	private int account;
	int withdrawAmount;
	private boolean complete;
	private boolean success;

	public ProcessingUnit(int account) {
		this.account = account;
	}

	public void setWithdrawAmount(int amount) {
		this.withdrawAmount = amount;
	}

	@Override
	public void run() {
		complete = false;
		success = false;

		try (Socket socket = new Socket("localhost", Globals.port);
				BufferedReader inFromServer = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				PrintWriter outToServer = new PrintWriter(
						socket.getOutputStream(), true);) {
			String inputLine;

			String command = "withdraw " + account + " " + withdrawAmount;
			System.out.println("PU issues command:" + command);
			outToServer.println(command);
			while ((inputLine = inFromServer.readLine()) != null) {
				System.out.println("PU receives feedback:" + inputLine);
				switch (inputLine) {
				case "end":
					break;
				case "success":
					success = true;
					break;
				case "fail":
					success = false;
					break;
				}
				// notify the database that ProcessingUnit wishes to end
				// transaction
				outToServer.println("end");
				break;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		complete = true;
	}

	public boolean isCompleted() {
		return complete;
	}

	public boolean hasFailed() {
		return !success;
	}

}

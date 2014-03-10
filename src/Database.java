import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Database extends Thread {
	static Integer[] accounts;
	int port;

	public Database(int port, int n) {
		this.port = port;
		accounts = new Integer[n];
	}

	public static void setDefaultBalance() {
		Arrays.fill(accounts, 100);
	}

	public static int getBalance(int account) {
		return accounts[account];
	}

	public static void setBalance(int account, int amount) {
		accounts[account] = amount;
	}

	@Override
	public void run() {
		System.out.println("Database start : " + port);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket client = serverSocket.accept();
				forkWorker(client);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + port);
			System.exit(-1);
		}

		System.out.println("Database exit");
	}

	private void forkWorker(Socket client) {
		DatabaseThread dbthread = new DatabaseThread(client);
		dbthread.start();
	}

	/*
	 * Actually does a withdraw by modifying the accounts. input is in the form
	 * {COMMAND} {ACCOUNT} {AMOUNT}
	 * 
	 * @return true on successful withdraw, false on error, e.g. amount to
	 * withdraw more than balance
	 */
	public static synchronized boolean processWithdraw(String input) {
		String[] tokens = input.split(" ");
		Integer account = Integer.parseInt(tokens[1]);
		Integer amount = Integer.parseInt(tokens[2]);

		int currentBalance = Database.getBalance(account);
		System.out
				.printf("DBThread %d gets current: %d has %d\n", Thread
						.currentThread().getId(), account, Database
						.getBalance(account));
		if (amount > currentBalance) {
			return false;
		}
		int newBalance = currentBalance - amount;
		Database.setBalance(account, newBalance);
		System.out
				.printf("DBThread %d gets set: %d has %d\n", Thread
						.currentThread().getId(), account, Database
						.getBalance(account));
		return true;
	}

	public static boolean processUnsafeWithdraw(String input) {
		String[] tokens = input.split(" ");
		Integer account = Integer.parseInt(tokens[1]);
		Integer amount = Integer.parseInt(tokens[2]);

		int currentBalance = Database.getBalance(account);
		System.out
				.printf("DBThread %d gets current: %d has %d\n", Thread
						.currentThread().getId(), account, Database
						.getBalance(account));
		if (amount > currentBalance) {
			return false;
		}
		int newBalance = currentBalance - amount;
		Database.setBalance(account, newBalance);
		System.out
				.printf("DBThread %d gets set: %d has %d\n", Thread
						.currentThread().getId(), account, Database
						.getBalance(account));
		return true;
	}
}

/*
 * Handles a connection with a particular client, i.e. the ProcessingUnit.
 */
class DatabaseThread extends Thread {
	private Socket socket;

	public DatabaseThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (PrintWriter outToPu = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader inFromPu = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			if (Globals.isTimeout()) {
				inFromPu.readLine();
				outToPu.println("timeout");
			} else {
				String inputLine = inFromPu.readLine();
				if (inputLine.contains("withdraw")) {
					boolean success;
					if (Globals.isUnsafe) {
						success = Database.processUnsafeWithdraw(inputLine);
					} else {
						success = Database.processWithdraw(inputLine);
					}
					outToPu.println(success ? "success" : "fail");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.printf("After all %d has %d\n", 0, Database.getBalance(0));
		System.out.printf("After all %d has %d\n", 1, Database.getBalance(1));
	}

}
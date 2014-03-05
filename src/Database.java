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
        // starts a new thread to handle each client connection
        Socket client = serverSocket.accept();
        DatabaseThread dbthread = new DatabaseThread(client);
        dbthread.start();
      }
    } catch (IOException e) {
      System.err.println("Could not listen on port " + port);
      System.exit(-1);
    }

    System.out.println("Database exit");
  }
}

/*
 * Handles a connection with a particular client,
 * i.e. the ProcessingUnit.
 */
class DatabaseThread extends Thread {
  private Socket socket;

  public DatabaseThread(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    System.out.println("Database thread start");
    try (PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));) {
      String inputLine;
      while ((inputLine = inFromClient.readLine()) != null) {
        System.out.println("DB read: " + inputLine);
        if (inputLine.equals("end")) {
          // informs client to terminate session
          serverOut.println("end");
          break;
        } else if (inputLine.contains("withdraw")) {
          boolean success = processWithdraw(inputLine);
          // informs the client of the success message
          serverOut.println(success ? "success" : "fail");
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.printf("After all %d has %d\n", 0, Database.getBalance(0));
    System.out.printf("After all %d has %d\n", 1, Database.getBalance(1));
  }

  /*
   * Actually does a withdraw by modifying the accounts.
   * input is in the form {COMMAND} {ACCOUNT} {AMOUNT}
   * @return true on successful withdraw, false on error, e.g.
   * amount to withdraw more than balance
   */
  public boolean processWithdraw(String input) {
    String[] tokens = input.split(" ");
    Integer account = Integer.parseInt(tokens[1]);
    Integer amount = Integer.parseInt(tokens[2]);

    int currentBalance = Database.getBalance(account);
    System.out.printf("DBThread %d gets current: %d has %d\n", Thread
        .currentThread().getId(), account, Database.getBalance(account));
    // Thread.yield(); // can put this for more obvious concurrency issues
    if (amount > currentBalance) {
      return false;
    }
    int newBalance = currentBalance - amount;
    Database.setBalance(account, newBalance);
    System.out.printf("DBThread %d gets set: %d has %d\n", Thread
        .currentThread().getId(), account, Database.getBalance(account));
    return true;
  }

}
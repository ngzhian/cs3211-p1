import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    System.out.println("Main start");
    
    if (args.length > 0) {
      try {
        Globals.port = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        System.out.println("Using port " + Globals.port);
      }
    } else {
    	Globals.port = 3342;
    }

    // interactive();

    /* starts a Database thread that constantly listens 
     * for incoming client connections
     */

    testOne();
  }

  private static void testOne() {
    Integer expected = 30;
    Integer withdrawA = 30;
    Integer withdrawB = 40;
    Database db = new Database(Globals.port, 2);
    Database.setDefaultBalance();
    db.start();
    System.out.println("Starting test 1");
    System.out.println("Database has 2 accounts, with 100 each");
    System.out.println("ATM 1 will withdraw " + withdrawA + " from account 1");
    System.out.println("ATM 2 will withdraw " + withdrawB + " from account 1");
    Atm a = new Atm().login(0);
    a.setWithdrawAmount(30);
    Atm b = new Atm().login(0);
    b.setWithdrawAmount(40);
    a.start();
    b.start();
    try {
      a.join();
      b.join();
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    System.out.println("Final balance should be " + expected);
    Integer actual = Database.getBalance(0);
    if (actual == expected) {
      System.out.println("And is " + expected
          + ". This may happen sometimes, run the program again");
    } else {
      System.out.println("But is " + actual);
    }
    System.out
        .println("Main exit. If running from eclipse remember to manually stop, else Database will continue running and hog the socket port");
  }

  @SuppressWarnings("unused")
  private static void interactive() {
    Scanner sc = new Scanner(System.in);
    System.out.print("No. of accounts the database should support: ");
    Integer numAccounts = sc.nextInt();

    Database db = new Database(Globals.port, numAccounts);
    System.out.println("Want to specify account balance for each account? Y/N");
    System.out.println("If N, balance is set to 100 for all");
    String specify = sc.next();
    if (specify.equalsIgnoreCase("y")) {
      Integer balance;
      for (int i = 0; i < numAccounts; i++) {
        System.out.printf("Account %d balance? : ", i + 1);
        balance = sc.nextInt();
        Database.accounts[i] = balance;
      }
    } else {
      Database.setDefaultBalance();
    }
    db.start();
    sc.close();
  }
}

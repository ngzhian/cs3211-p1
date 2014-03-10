public class Main {
	public static void main(String[] args) {
		int reliability = 100;

		setUpNetwork(reliability);
		setUpDatabase();

		ProcessingUnit pu = new ProcessingUnit();
		pu.start();

		AuthServer auth = new AuthServer();
		auth.start();

		runTest();
	}

	private static void runTest() {
		Atm first = new Atm();
		Atm second = new Atm();

//		first.login(0);
//		second.login(0);
		first.account = 0;
		second.account = 0;

		Integer withdrawA = 30;
		Integer withdrawB = 40;
		int expected = 30;

		System.out.println("Database has 2 accounts, with 100 each");
		System.out.println("ATM 1 will withdraw " + withdrawA
				+ " from account 1");
		System.out.println("ATM 2 will withdraw " + withdrawB
				+ " from account 1");
		first.setWithdrawAmount(withdrawA);
		second.setWithdrawAmount(withdrawB);

		first.start();
		second.start();
		
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

	private static void setUpDatabase() {
		Database db = new Database(Globals.networkToDb, 2);
		Database.setDefaultBalance();
		db.start();
	}

	private static void setUpNetwork(int reliability) {
		Network atmToPu = new Network(Globals.atmToNetwork,
				Globals.networkToPu, reliability);
		Network puToDb = new Network(Globals.puToNetwork, Globals.networkToDb,
				reliability);
		Network atmToAuth = new Network(Globals.atmToAuthNetwork,
				Globals.authNetworkToAuthServer, reliability);
		atmToPu.start();
		puToDb.start();
		atmToAuth.start();
	}
}

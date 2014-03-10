public class Main {
	public static void main(String[] args) {
		if (args.length > 0) {
			Globals.reliability = Integer.parseInt(args[0]);
			Globals.isUnsafe = Integer.parseInt(args[1]) == 1;
			Globals.willDeadlock = Integer.parseInt(args[2]) == 1;
		}
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

		first.login(0);
		second.login(0);

		Integer withdrawA = 30;
		Integer withdrawB = 40;

		first.setWithdrawAmount(withdrawA);
		second.setWithdrawAmount(withdrawB);

		first.start();
		second.start();
	}

	private static void setUpDatabase() {
		Database db = new Database(Globals.puToDbPort, 2);
		Database.setDefaultBalance();
		db.start();
	}
}

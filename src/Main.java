public class Main {
	public static void main(String[] args) {
		boolean interactive = false;
		if (args.length > 0) {
			switch(args[0]){
			case "i":
				interactive = true;
				break;
			case "t":
				interactive = false;
				break;
			default:
				System.out.println("Invalid arguments. Exiting...");
				return;
			}
			Globals.reliability = Integer.parseInt(args[1]);
			Globals.isUnsafe = Integer.parseInt(args[2]) == 1;
			Globals.willDeadlock = Integer.parseInt(args[3]) == 1;
		}
		setUpDatabase();

		ProcessingUnit pu = new ProcessingUnit();
		pu.start();

		AuthServer auth = new AuthServer();
		auth.start();
		
		if(interactive){
			System.out.println("You are in Interactive mode, you may now start multiple ATM processes...");
			System.out.println("Press Ctrl + C to quit");
		} else{
			runTest();
		}
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
		Database db = new Database(Globals.puToDbPort, Globals.numberOfAccounts);
		Database.setDefaultBalance();
		db.start();
	}
}

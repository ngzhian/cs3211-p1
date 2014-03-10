import java.util.Random;

public class Globals {
	static int reliability = 50;
	static int numberOfAccounts = 10;

	static int atmToAuthPort = 1000;
	static int atmToPuPort = 1001;
	static int puToDbPort = 1002;
	
	static Random randomGenerator = new Random();
	
	static boolean isTimeout() {
		int nextInt = randomGenerator.nextInt(100);
		return nextInt > reliability;
	}
}
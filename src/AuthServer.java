import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class AuthServer extends Thread{
	
	private Socket socket;
	private int sendPortNum;
	
	public AuthServer(Socket socket, int sendPort) {
		this.socket = socket;
		this.sendPortNum = sendPort;
	}

	@Override
	public void run(){
		BufferedReader inFromSender;
		boolean isValid;
		try {
			inFromSender = new BufferedReader(new InputStreamReader(
					this.socket.getInputStream()));
			isValid = inFromSender.read() < Globals.accountNumber;
		} catch (IOException e1) {
			System.out.println("ERROR: Couldn't get input stream in worker. Halting.");
			return;
		}

		try (Socket sendTo = new Socket("localhost", this.sendPortNum)) {
			BufferedWriter outToReceiver = new BufferedWriter(
					new OutputStreamWriter(sendTo.getOutputStream()));
			
			if(isValid){
				outToReceiver.write("success");
			} else{
				outToReceiver.write("failed");
			}
		} catch (IOException e) {
		}
	}
}

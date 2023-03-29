package dev.slimevr;

import java.util.Scanner;


public class CommandServer extends Thread {
	private VRServer vrServer;

	public CommandServer(VRServer vrServer) {
		this.vrServer = vrServer;
	}

	public void run() {
		Scanner scan = new Scanner(System.in);
		while (true) {
			if (scan.hasNext()) {
				String command = scan.nextLine();
				if (command.equals("exit")) {
					this.vrServer.interrupt();
					break;
				}
			}
		}
		scan.close();
	}

}

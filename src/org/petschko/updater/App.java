package org.petschko.updater;

import org.petschko.lib.Const;

/**
 * Author: Peter Dragicevic [peter@petschko.org]
 * Authors-Website: http://petschko.org/
 * Date: 05.10.2017
 * Time: 22:28
 * Update: -
 * Version: 0.0.1
 *
 * Notes: App Class
 */
public class App {
	static String targetJar = null;
	static String downloadUrl = null;
	static boolean useGUI = false;
	static boolean relaunch = false;
	static String[] relaunchArgs = null;
	static GUI gui = null;

	/**
	 * Main-Class
	 *
	 * @param args - Optional Arguments from Command-Line
	 */
	public static void main(String[] args) {
		// Check if just use CMD
		if(args.length > 0) {
			App.processArgs(args);

			if(useGUI)
				gui = new GUI();

			new Updater();
		} else {
			System.out.println("No Parameters given...");
			printHelp();
			exitCMD(Const.STATUS_ERROR);
		}

		if(gui != null)
			gui.main.dispose();
	}

	/**
	 * Process Command-Line Arguments
	 *
	 * @param args - Optional Arguments from Command-Line
	 */
	private static void processArgs(String[] args) {
		// Show Welcome-Message
		System.out.println(Config.programName + " - " + Config.version + " by " + Const.creator);
		System.out.println();

		if(args.length >= 1) {
			// Check if its help
			if(isHelpCmd(args[0])) {
				printHelp();
				exitCMD(Const.STATUS_OK);
				return;
			}

			targetJar = args[0].trim();
		}
		if(args.length >= 2)
			downloadUrl = args[1].trim();
		if(args.length >= 3)
			useGUI = Boolean.parseBoolean(args[2].trim().toLowerCase());
		if(args.length >= 4)
			relaunch = Boolean.parseBoolean(args[3].trim().toLowerCase());
		if(args.length >= 5) {
			relaunchArgs = new String[args.length - 4];

			for(int i = 4; i < args.length; i++)
				relaunchArgs[i - 4] = args[i].trim();
		}

		if(targetJar == null || downloadUrl == null) {
			System.out.println("Missing Parameters!");
			printHelp();
			exitCMD(Const.STATUS_ERROR);
			return;
		}

		if(targetJar.equals("") || downloadUrl.equals("")) {
			System.out.println("Required Parameters are empty!");
			printHelp();
			exitCMD(Const.STATUS_ERROR);
		}
	}

	/**
	 * Prints help for Command-Line usage
	 */
	private static void printHelp() {
		System.out.println("Usage: java -jar updater.jar [target jar] [download url] [(Optional) use GUI - true|false] [(Optional) relaunch - true|false] [(optional|multi) relaunch args...]");
		System.out.println();
	}

	/**
	 * Shows if the current command is a help command
	 *
	 * @return - Is the command a help command
	 */
	private static boolean isHelpCmd(String command) {
		if(command == null)
			return false;

		command = command.toLowerCase().trim();

		return command.equals("help") || command.equals("-help") || command.equals("--help") || command.equals("/?");
	}

	/**
	 * Exit the Program with a Message
	 *
	 * @param status - Exit-Status-Code
	 */
	static void exitCMD(int status) {
		System.out.println("Done.");
		System.exit(status);
	}
}

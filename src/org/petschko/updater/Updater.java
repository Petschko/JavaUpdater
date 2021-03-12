package org.petschko.updater;

import org.petschko.lib.Const;
import org.petschko.lib.notification.ErrorWindow;
import org.petschko.lib.notification.InfoWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.Random;

/**
 * Author: Peter Dragicevic [peter@petschko.org]
 * Authors-Website: http://petschko.org/
 * Date: 23.12.2017
 * Time: 21:40
 * Update: -
 * Version: 0.0.1
 *
 * Notes: Class Updater
 */
class Updater {
	private boolean hasErrors = false;
	private URL downloadUrl;
	private File targetJarFile;
	private File tmpNewFile;
	private File backupFile;

	/**
	 * Updater constructor
	 */
	Updater() {
		try {
			this.downloadUrl = new URL(App.downloadUrl);
		} catch(MalformedURLException e) {
			this.showMessage("Download-URL is in an invalid Format!...", Const.STATUS_ERROR, true, e);
			return;
		}

		this.targetJarFile = new File(App.targetJar);
		if(! this.ensureJarThisDir())
			return;

		if(! this.targetJarFile.exists() || this.targetJarFile.isDirectory()) {
			this.showMessage("Target-Jar doesn't exists!...", Const.STATUS_ERROR, true);
			return;
		}

		if(! targetJarFile.canWrite()) {
			this.showMessage("Target-Jar isn't writeable!...", Const.STATUS_ERROR, true);
			return;
		}

		// Do the Update
		this.downloadFile();
		this.replaceFile();

		// Ends the Update routine
		this.clear();
		this.relaunch();
	}

	/**
	 * Ensures its only a JAR in this dir
	 *
	 * @return - Its just a JAR from this dir
	 */
	private boolean ensureJarThisDir() {
		String targetFile = this.targetJarFile.toString();

		int fileStrLen = targetFile.length();
		int lastDSCharPos = targetFile.lastIndexOf(Const.ds);
		int lastDotPos = targetFile.lastIndexOf(".");

		if(lastDSCharPos != -1) {
			this.showMessage("Only Jar Files from this Directory are allowed!", Const.STATUS_ERROR, true);
			return false;
		}

		if(lastDotPos == -1 || lastDotPos == 0 || lastDotPos == fileStrLen - 1) {
			this.showMessage("Target File has no Extension!", Const.STATUS_ERROR, true);
			return false;
		} else {
			String extension = targetFile.substring(lastDotPos + 1).toLowerCase();

			if(! extension.equals("jar")) {
				this.showMessage("Target File is no JAR!", Const.STATUS_ERROR, true);
				return false;
			}
		}

		return true;
	}

	/**
	 * Downloads the new File
	 */
	private void downloadFile() {
		// Set Random name for new tmp File
		Random random = new Random();
		String tmpNewFileName;
		do {
			tmpNewFileName = "new_" + random.nextInt(1000) + "_" + App.targetJar;
			this.tmpNewFile = new File(tmpNewFileName);
		} while(this.tmpNewFile.exists());

		System.out.println(cmdPrefix(Const.STATUS_INFO) + "Downloading Update-File...");
		try {
			ReadableByteChannel readableByteChannel = Channels.newChannel(this.downloadUrl.openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(tmpNewFileName);
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			fileOutputStream.close();
		} catch(IOException e) {
			this.showMessage("Could not download the Update-File...", Const.STATUS_ERROR, true, e);
			this.hasErrors = true;
		}
		System.out.println(cmdPrefix(Const.STATUS_OK) + "Update-File saved to \"" + tmpNewFileName + "\"!");
	}

	/**
	 * Replaces the Original JAR File with the new one
	 */
	private void replaceFile() {
		if(this.hasErrors)
			return;

		System.out.println(cmdPrefix(Const.STATUS_INFO) + "Replacing Jar-File...");

		// Check if tmp File exists
		if(! this.tmpNewFile.exists() || this.tmpNewFile.isDirectory()) {
			this.showMessage("Update-File does not exists...", Const.STATUS_ERROR, true);
			this.hasErrors = true;
			return;
		}

		// Backup original
		String backupName = App.targetJar + ".bkp";
		this.backupFile = new File(backupName);
		if(this.backupFile.exists() && ! this.backupFile.isDirectory()) {
			if(! this.backupFile.delete()) {
				this.showMessage("Could not remove the old Backup-File...", Const.STATUS_ERROR, true);
				this.hasErrors = true;
				return;
			}
		}

		try {
			Files.copy(this.targetJarFile.toPath(), this.backupFile.toPath());
		} catch(IOException ioException) {
			this.showMessage("Could not backup the Original Jar-File...", Const.STATUS_ERROR, true);
			this.hasErrors = true;
			return;
		}

		// Delete original
		if(! this.targetJarFile.delete()) {
			this.showMessage("Could not remove the Original Jar-File...", Const.STATUS_ERROR, true);
			this.hasErrors = true;
			return;
		}

		// Copy the File
		try {
			Files.copy(this.tmpNewFile.toPath(), this.targetJarFile.toPath());
		} catch(IOException ioException) {
			this.showMessage("Could copy the new File to the Original Jar-File...", Const.STATUS_ERROR, true);
			this.hasErrors = true;
			return;
		}

		if(! targetJarFile.exists()) {
			// Try to restore file if not exists
			try {
				Files.copy(this.backupFile.toPath(), this.targetJarFile.toPath());
			} catch(IOException ioException) {
				this.showMessage("Could not restore Original-File, please manually rename " + backupName + " to " + this.targetJarFile + "!", Const.STATUS_ERROR, true, ioException);
				this.hasErrors = true;
				return;
			}

			this.showMessage("Restored the Original Jar-File, since it could not copy the Update-File...", Const.STATUS_ERROR, true);
			this.hasErrors = true;
			return;
		}

		System.out.println(cmdPrefix(Const.STATUS_OK) + "Successfully replaced old Jar-File with new Jar!");
	}

	/**
	 * Launches the original Program again if requested
	 */
	private void relaunch() {
		if(this.hasErrors)
			return;

		System.out.println(cmdPrefix(Const.STATUS_OK) + "Update done!");
		if(! App.relaunch)
			return;

		System.out.println(cmdPrefix(Const.STATUS_INFO) + "Relaunching \"" + App.targetJar + "\"...");

		String[] run = {
			"java",
			"-jar",
			"\"" + App.targetJar + "\""
		};

		// Add relaunch args
		if(App.relaunchArgs != null) {
			String[] tmp = new String[run.length + App.relaunchArgs.length];
			int i;
			for(i = 0; i < run.length; i++)
				tmp[i] = run[i];

			int n = i;
			for(; i < tmp.length; i++)
				tmp[i] = App.relaunchArgs[i - n];

			run = tmp;
		}

		try {
			Runtime.getRuntime().exec(run);
		} catch (Exception e) {
			this.showMessage("Could not restart the Program, please start it yourself!", Const.STATUS_WARNING, true, e);
			this.hasErrors = true;
			return;
		}

		System.out.println(cmdPrefix(Const.STATUS_OK) + "Done!");
		System.exit(Const.STATUS_OK);
	}

	/**
	 * Clears TMP Files etc
	 */
	private void clear() {
		System.out.println(cmdPrefix(Const.STATUS_INFO) + "Clearing tmp Files...");

		if(this.tmpNewFile != null) {
			if(this.tmpNewFile.exists() && ! this.tmpNewFile.isDirectory()) {
				if(! this.tmpNewFile.delete())
					System.out.println(cmdPrefix(Const.STATUS_WARNING) + "Could not delete TMP-Update-File \"" + this.tmpNewFile.toString() + "\"");
				else
					System.out.println(cmdPrefix(Const.STATUS_OK) + "Deleted Update-File \"" + this.tmpNewFile.toString() + "\"!");
			}
		}
		if(this.backupFile != null) {
			if(this.backupFile.exists() && ! this.backupFile.isDirectory()) {
				if(! this.backupFile.delete())
					System.out.println(cmdPrefix(Const.STATUS_WARNING) + "Could not delete Backup Jar-File \"" + this.backupFile.toString() + "\"");
				else
					System.out.println(cmdPrefix(Const.STATUS_OK) + "Deleted Backup-File \"" + this.backupFile.toString() + "\"!");
			}
		}
	}

	/**
	 * Shows a message to the user
	 *
	 * @param message - Message to show
	 */
	private void showMessage(String message) {
		showMessage(message, Const.STATUS_NONE);
	}

	/**
	 * Shows a message to the user
	 *
	 * @param message - Message to show
	 * @param type - Type of the message
	 */
	private void showMessage(String message, int type) {
		showMessage(message, type, false);
	}

	/**
	 * Shows a message to the user
	 *
	 * @param message - Message to show
	 * @param type - Type of the message
	 * @param exit - Should the program stop
	 */
	private void showMessage(String message, int type, boolean exit) {
		showMessage(message, type, exit, null);
	}

	/**
	 * Shows a message to the user
	 *
	 * @param message - Message to show
	 * @param type - Type of the message
	 * @param exit - Should the program stop
	 * @param exception - Exception which was thrown or null for none
	 */
	private void showMessage(String message, int type, boolean exit, Exception exception) {
		if(App.useGUI) {
			if(type == Const.STATUS_ERROR || type == Const.STATUS_WARNING || exception != null) {
				ErrorWindow errorWindow = new ErrorWindow(message, typeToNotificationType(type), false, exception);
				errorWindow.show(App.gui.main);
			} else {
				InfoWindow infoWindow = new InfoWindow(message);
				infoWindow.show(App.gui.main);
			}

			if(exception != null)
				exception.printStackTrace();
		}

		System.out.println(cmdPrefix(type) + message);

		if(exit) {
			if(App.useGUI)
				App.gui.main.dispose();

			App.exitCMD(type);
		}
	}

	/**
	 * Returns the CMD-Prefix depend on the Status
	 *
	 * @param type - Message Type
	 * @return - CMD-Prefix
	 */
	private String cmdPrefix(int type) {
		switch(type) {
			case Const.STATUS_ERROR:
				return "[Error]: ";
			case Const.STATUS_WARNING:
				return "[Warn]: ";
			case Const.STATUS_INFO:
				return "[Info]: ";
			case Const.STATUS_OK:
				return "[OK]: ";
			case Const.STATUS_NONE:
			default:
				return "";
		}
	}

	/**
	 * Converts the Message Type to a NotificationWindow-Type
	 *
	 * @param type - Message Type
	 * @return - NotificationWindow-Type
	 */
	private int typeToNotificationType(int type) {
		switch(type) {
			case Const.STATUS_ERROR:
				return ErrorWindow.ERROR_LEVEL_ERROR;
			case Const.STATUS_WARNING:
				return ErrorWindow.ERROR_LEVEL_WARNING;
			case Const.STATUS_OK:
				return ErrorWindow.ERROR_LEVEL_NOTICE;
			case Const.STATUS_INFO:
			case Const.STATUS_NONE:
			default:
				return ErrorWindow.ERROR_LEVEL_INFO;
		}
	}
}

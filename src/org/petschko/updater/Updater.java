package org.petschko.updater;

import org.jetbrains.annotations.Nullable;
import org.petschko.lib.Const;
import org.petschko.lib.notification.ErrorWindow;
import org.petschko.lib.notification.InfoWindow;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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
	private URL downloadUrl;
	private File targetJarFile;

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

		this.downloadFile();
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
	void downloadFile() {
		// todo
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
	private void showMessage(String message, int type, boolean exit, @Nullable Exception exception) {
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

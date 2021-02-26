package org.petschko.lib;

/**
 * Author: Peter Dragicevic [peter@petschko.org]
 * Authors-Website: http://petschko.org/
 * Date: 26.12.2016
 * Time: 14:46
 * Update: -
 * Version: 0.0.1
 *
 * Notes: Const Class
 */
public class Const {
	public static final String creator = "Petschko";
	public static final String creatorURL = "http://petschko.org/";

	// System Constance's
	public static final String ds = System.getProperty("file.separator");
	public static final String newLine = System.getProperty("line.separator");

	// Error-Levels
	public static final int STATUS_OK = 0;
	public static final int STATUS_INFO = 2;
	public static final int STATUS_WARNING = 1;
	public static final int STATUS_ERROR = -1;
	public static final int STATUS_NONE = 9999;

	/**
	 * Constructor
	 */
	private Const() {
		// VOID - This is a Static-Class
	}
}

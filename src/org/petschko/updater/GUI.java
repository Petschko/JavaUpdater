package org.petschko.updater;

import org.petschko.lib.Const;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Author: Peter Dragicevic
 * Authors-Website: https://petschko.org/
 * Date: 26.02.2021
 * Time: 00:45
 *
 * Notes: -
 */
class GUI {
	JFrame main;

	public GUI() {
		this.main = new JFrame("Updating...");
		JLabel text = new JLabel();
		text.setText("Updating the Program... This may take a short while.");

		this.main.add(text);

		// Change close Action
		this.main.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.main.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("Update canceled by User!");
				App.exitCMD(Const.STATUS_WARNING);
			}
		});

		// Center Window and Display it
		this.main.setLocationRelativeTo(null);
		this.main.setVisible(true);
		this.main.pack();
	}
}

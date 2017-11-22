package de.uternity.myroute.visualizer;

import oracle.jrockit.jfr.JFR;

import javax.swing.*;
import java.awt.*;

public class Main
{
	public static void main(String[] args) throws Exception
	{
		OpenGL openGL = new OpenGL();
		openGL.init();

		/*JFrame frame = new JFrame();
		frame.setSize(2000, 1500);
		frame.setTitle("Navigation");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.add(new Navigation());
		frame.setVisible(true);
		frame.setBackground(Color.BLACK);*/
	}
}
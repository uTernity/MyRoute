package de.uternity.myroute.visualizer;

import de.uternity.myroute.creator.classes.Highway;
import de.uternity.myroute.creator.classes.Node;
import de.uternity.myroute.creator.streams.HighwayReader;
import oracle.jrockit.jfr.JFR;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Navigation extends JPanel
{
	List<Highway> highwayList = new ArrayList<>();

	double factorX, factorY, factor;

	int lastX, lastY;

	double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE, minLon = Double.MAX_VALUE, maxLon = Double.MIN_VALUE;

	public Navigation()
	{
		try
		{
			HighwayReader highways = new HighwayReader("D:/Dateien/OSM Daten/final/51;7.utr");

			for (Highway highway : highways)
			{
				highwayList.add(highway);

				for (Node n : highway.getWayNodes())
				{
					if (n.getLat() < minLat)
						minLat = n.getLat();
					if (n.getLat() > maxLat)
						maxLat = n.getLat();
					if (n.getLon() < minLon)
						minLon = n.getLon();
					if (n.getLon() > maxLon)
						maxLon = n.getLon();
				}
			}

			System.out.println(minLat + "  " + maxLat + "  " + minLon + "  " + maxLon);

			/*minLon = 7.5;
			maxLon = 7.6;
			maxLat = 51.04;*/
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}

		addMouseWheelListener(new MouseAdapter()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				super.mouseWheelMoved(e);

				if (e.getWheelRotation() == -1)
				{
					minLat += 0.02;
					maxLat -= 0.02;
					minLon += 0.02;
					maxLon -= 0.02;
				}

				else
				{
					minLat -= 0.02;
					maxLat += 0.02;
					minLon -= 0.02;
					maxLon += 0.02;
				}

				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				super.mouseDragged(e);

				if (e.getX() < lastX || lastX == 0)
				{
					minLon += 0.004;
					maxLon += 0.004;
				}
				if (e.getX() > lastX || lastX == 0)
				{
					minLon -= 0.004;
					maxLon -= 0.004;
				}
				if (e.getY() < lastY || lastY == 0)
				{
					minLat += 0.004;
					maxLat += 0.004;
				}
				if (e.getY() > lastY || lastY == 0)
				{
					minLat -= 0.004;
					maxLat -= 0.004;
				}

				lastX = e.getX();
				lastY = e.getY();


				repaint();

				//System.out.println(minLat + " " + maxLat);
 			}
		});
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D graphics2D = (Graphics2D) g;

		setBackground(Color.BLACK);

		factorX = (double) getSize().width / (maxLon - minLon);
		factorY = (double) getSize().height / (maxLat - minLat);
		factor = Math.min(factorX, factorY);

		graphics2D.setPaint(Color.WHITE);

		HashSet<String> names = new HashSet<>();

		for (Highway highway : highwayList)
		{
			for (int i = 0; i < highway.getWayNodes().size() - 1; i++)
			{
				try
				{
					String tags = new String(highway.getTags(), "ISO-8859-1");

					graphics2D.drawLine(
							(int) ((highway.getWayNodes().get(i).getLon() - minLon) * factorX),
							(int) (getHeight() - (highway.getWayNodes().get(i).getLat() - minLat) * factorX),
							(int) ((highway.getWayNodes().get(i + 1).getLon() - minLon) * factorX),
							(int) (getHeight() - (highway.getWayNodes().get(i + 1).getLat() - minLat) * factorX));

				}

				catch (UnsupportedEncodingException e)
				{
					e.printStackTrace();
				}

			}

			String tags = null;
			try
			{
				tags = new String(highway.getTags(), "ISO-8859-1");
			}

			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}

			if (tags.contains("name="))
			{

				String name = tags.substring(tags.indexOf("name=") + 5, tags.length());
				name = name.substring(0, name.indexOf("¶"));

				if (!names.contains(name)){
				graphics2D.drawString(name, (int) ((highway.getWayNodes().get(0).getLon() - minLon) * factorX), (int) (getHeight() - (highway.getWayNodes().get(0).getLat() - minLat) * factorX));
					names.add(name);}
			}
		}
	}
}
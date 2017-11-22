package de.uternity.myroute.creator.filehandling;

import de.uternity.myroute.creator.Tools;
import de.uternity.myroute.creator.classes.Highway;
import de.uternity.myroute.creator.classes.Node;
import de.uternity.myroute.creator.streams.HighwayReader;
import de.uternity.myroute.creator.streams.HighwayWriter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Splitter
{
	private String suffix, oldSuffix;
	private HighwayWriter highwayWriter;
	private File input;

	public void splitHighways(File input) throws IOException
	{
		this.input = input;

		List<Highway> highways = new ArrayList<>();

		HashMap<Long, Integer> usedNodes = new HashMap<>();

		for (Highway highway : new HighwayReader(input.getAbsolutePath()))
		{
			for (Node node : highway.getWayNodes())
			{
				if (usedNodes.containsKey(node.getId()))
					usedNodes.put(node.getId(), usedNodes.get(node.getId()) + 1);
				else
					usedNodes.put(node.getId(), 1);
			}
		}

		usedNodes.entrySet().removeIf(entry -> entry.getValue() == 1);

		long id = 1;

		for (Highway highway : new HighwayReader(input.getAbsolutePath()))
		{
			boolean used = false;

			Highway newHighway = null;

			for (Node node : highway.getWayNodes().subList(1, highway.getWayNodes().size() - 1))
			{
				if (usedNodes.containsKey(node.getId()))
				{
					used = true;
					break;
				}
			}

			if (used)
			{
				while (used)
				{
					Node start = null;

					List<Node> wayNodes = highway.getWayNodes();

					for (Node node : wayNodes)
					{
						if (usedNodes.containsKey(node.getId()))
						{
							if (start == null)
								start = node;

							else
							{
								newHighway = new Highway();

								newHighway.setId(id++);
								newHighway.setTags(highway.getTags());
								newHighway.setWayNodes(wayNodes.subList(0, wayNodes.indexOf(node) + 1));
								newHighway.setLength(getDistance(newHighway.getWayNodes()));

								highways.add(Tools.optimizeHighway(newHighway));

								wayNodes = wayNodes.subList(wayNodes.indexOf(node), wayNodes.size());
							}
						}
					}

					if (start == null || wayNodes.size() == 1)
						used = false;

					if (start != null)
					{
						used = false;
						newHighway = new Highway();

						newHighway.setId(id++);
						newHighway.setTags(highway.getTags());
						newHighway.setWayNodes(wayNodes.subList(0, wayNodes.size()));
						newHighway.setLength(getDistance(newHighway.getWayNodes()));
						highways.add(Tools.optimizeHighway(newHighway));
					}
				}
			}

			else
			{
				highway.setId(id++);
				highway.setLength(getDistance(highway.getWayNodes()));
				highways.add(Tools.optimizeHighway(highway));
			}
		}

		highways.sort((p1, p2) ->
		{
			if (Integer.compare((int) p1.getWayNodes().get(0).getLat(), (int) p2.getWayNodes().get(0).getLat()) == 0)
				return Integer.compare((int) p1.getWayNodes().get(0).getLon(), (int) p2.getWayNodes().get(0).getLon());

			else
				return Integer.compare((int) p1.getWayNodes().get(0).getLat(), (int) p2.getWayNodes().get(0).getLat());
		});

		write(highways);
	}

	private void write(List<Highway> highways) throws IOException
	{
		new File(input.getParent() + "/final").mkdirs();

		for (Highway entry : highways)
		{
			suffix = (int) entry.getWayNodes().get(0).getLat() + ";" + (int) entry.getWayNodes().get(0).getLon() + ".utr";

			if (suffix.equals(oldSuffix) && highwayWriter != null)
				highwayWriter.writeHighway(entry);

			else
			{
				if (highwayWriter != null)
				{
					highwayWriter.flush();
					highwayWriter.close();
				}

				highwayWriter = new HighwayWriter(input.getParent() + "/final/" + suffix, true);
				highwayWriter.writeHighway(entry);
			}

			oldSuffix = suffix;
		}

		if (highwayWriter != null)
		{
			highwayWriter.flush();
			highwayWriter.close();
		}
	}

	private double getDistance(List<Node> nodes)
	{
		double length = 0, latitude1, latitude2, longitude1, longitude2;
		double R = 6372.8, dLat, dLon, a, c;

		for (int i = 0; i < nodes.size() - 1; i++)
		{
			latitude1 = nodes.get(i).getLat();
			latitude2 = nodes.get(i + 1).getLat();
			longitude1 = nodes.get(i).getLon();
			longitude2 = nodes.get(i + 1).getLon();

			dLat = Math.toRadians(latitude2 - latitude1);
			dLon = Math.toRadians(longitude2 - longitude1);
			latitude1 = Math.toRadians(latitude1);
			latitude2 = Math.toRadians(latitude2);

			a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(latitude1) * Math.cos(latitude2);
			c = 2 * Math.asin(Math.sqrt(a));

			length += R * c;
		}

		return length;
	}
}
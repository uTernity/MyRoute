package de.uternity.myroute.creator.filehandling;

import crosby.binary.BinaryParser;
import crosby.binary.Osmformat;
import crosby.binary.file.BlockInputStream;
import de.uternity.myroute.creator.classes.Highway;
import de.uternity.myroute.creator.classes.Node;
import de.uternity.myroute.creator.streams.HighwayWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Extractor extends BinaryParser
{
	private HashMap<Long, Node> nodes = new HashMap<>();
	private HashSet<Highway> highways = new HashSet<>();
	private String key, val;
	private StringBuilder builder;
	private File inputFile;
	private long count;

	public Extractor(File inputFile) throws IOException
	{
		this.inputFile = inputFile;

		new BlockInputStream(new FileInputStream(inputFile.getAbsolutePath()), this).process();
	}

	@Override
	protected void parseDense(Osmformat.DenseNodes nodes)
	{
		long id = 0, lat = 0, lon = 0;
		int j = 0;

		for (int i = 0; i < nodes.getIdCount(); i++)
		{
			id += nodes.getId(i);
			lat += nodes.getLat(i);
			lon += nodes.getLon(i);

			builder = new StringBuilder();

			while (nodes.getKeysVals(j) != 0)
			{
				key = getStringById(nodes.getKeysVals(j++));
				val = getStringById(nodes.getKeysVals(j++));

				if (val.contains("\n") || val.contains("\r"))
					val = val.replace("\n", "").replace("\r", "");

				builder.append(key).append("=").append(val).append("¶");
			}

			j++;

			try
			{
				this.nodes.put(id, new Node(id, lat * 0.0000001, lon * 0.0000001, builder.toString().getBytes("ISO-8859-1")));
			}

			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void parseWays(List<Osmformat.Way> ways)
	{
		long wayId;
		Highway highway;

		for (Osmformat.Way way : ways)
		{
			wayId = 0;
			highway = new Highway();
			builder = new StringBuilder();

			highway.setId(way.getId());

			if (way.getRefsCount() < 2 || way.getRefsCount() > 2000)
				continue;

			for (int i = 0; i < way.getRefsCount(); i++)
			{
				wayId += way.getRefs(i);
				highway.getWayNodes().add(this.nodes.get(wayId));
			}

			for (int i = 0; i < way.getKeysCount(); i++)
			{
				key = getStringById(way.getKeys(i));
				val = getStringById(way.getVals(i));

				if (val.contains("\n") || val.contains("\r"))
					val = val.replace("\n", "").replace("\r", "");

				builder.append(key).append("=").append(val).append("¶");
			}

			try
			{
				highway.setTags(builder.toString().getBytes("ISO-8859-1"));
				//highway.length = getDistance(highway.coordinates);
				highways.add(highway);

				if (++count % 100000 == 0)
					write();
			}

			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void parseRelations(List<Osmformat.Relation> relations)
	{}

	@Override
	public void complete()
	{
		try
		{
			write();
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void write() throws IOException
	{
		HighwayWriter highwayWriter = new HighwayWriter(inputFile.getParent() + "/" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf(".")) + "_new.utr", true);

		for (Highway highway : highways)
			highwayWriter.writeHighway(highway);

		highwayWriter.flush();
		highwayWriter.close();

		highways.clear();
	}
}
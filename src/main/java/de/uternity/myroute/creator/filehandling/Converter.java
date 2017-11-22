package de.uternity.myroute.creator.filehandling;

import de.uternity.myroute.creator.Consts;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Converter
{
	public static File o5mToPbf(File input) throws IOException
	{
		String output = input.getParent() + "/" + input.getName().substring(0, input.getName().indexOf("_")) + "_new.pbf";

		execCommands("cmd.exe", "/c", Consts.PATH_OSMCONVERT, input.getAbsolutePath(), "-v", "--statistics", "--drop-author", "--drop-version", "-o=" + output);

		return new File(output);
	}

	public static File pbfToO5m(File input) throws IOException
	{
		String output = input.getParent() + "/" + input.getName().substring(0, input.getName().indexOf(".")) + "_new.o5m";

		execCommands("cmd.exe", "/c", Consts.PATH_OSMCONVERT, input.getAbsolutePath(), "-v", "--statistics", "--drop-author", "--drop-version", "-o=" + output);

		return new File(output);
	}

	public static File extractHighways(File input) throws IOException
	{
		String output = input.getParent() + "/" + input.getName().substring(0, input.getName().lastIndexOf(".")) + "_extracted_highways.o5m";

		execCommands("cmd.exe", "/c", Consts.PATH_OSMFILTER, input.getAbsolutePath(), "-v", "--parameter-file=" + new File("res/properties/parameters"), "-o=" + output);

		return new File(output);
	}

	private static void execCommands(String... cmd) throws IOException
	{
		ProcessBuilder builder = new ProcessBuilder(cmd);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line;
		while ((line = bufferedReader.readLine()) != null)
			System.out.println(line);
	}
}
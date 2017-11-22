package de.uternity.myroute.creator;

import de.uternity.myroute.creator.filehandling.Splitter;

import java.io.File;
import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		File input = new File("D:/Dateien/OSM Daten/koeln.pbf");
		//File convertedFile = Converter.pbfToO5m(input);
		//File extractedHighways = Converter.extractHighways(convertedFile);
		//File outputFile = Converter.o5mToPbf(extractedHighways);

		//Extractor extractor = new Extractor(outputFile);
		//Extractor extractor = new Extractor(new File("D:/Dateien/OSM Daten/koeln_new.pbf"));

		Splitter splitter = new Splitter();
		splitter.splitHighways(new File("D:/Dateien/OSM Daten/koeln_new_new.utr"));

		System.out.println("Finished");
	}
}
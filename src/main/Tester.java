package main;

import java.awt.geom.Dimension2D;
import java.util.regex.Pattern;

import Extract.Extractor;

public class Tester {
	public static void main(String args[]) {
		String test = "abc abc defjasdh abc okabcabcasd abc";
		System.out.println("abc can be found " + Extractor.prefixOccurences("abc", test) + " times in 'test' string.");
		String test2 = "--------------------asd-----asdbhasdoiajs------asdaosijdas--asd";
		System.out.println("Forward hyphens = " + Extractor.countHyphensInARow(test2, true));
		System.out.println("Backward hyphens = " + Extractor.countHyphensInARow(test2, false));
		Pattern commandPatter = Pattern.compile("-\\W");
		if (commandPatter.matcher("-p").matches()) {
			System.out.println("Matched");
		} else {
			System.out.println("Did not match");
		}
		
		
		for (int i = 0; i < 1; i++) {
			System.out.println("Loop");
			System.out.println(" i = " + i);
			i += 1;
		}
	}
}

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
		
		
		String testMatcher = "MRT Headern\nTimestamp: 1491904800(2017-04-11 12:00:00)\nType: 13(TABLE_DUMP_V2)\nSubtype: 2(RIB_IPV4_UNICAST)\nLength: 172\n"
				+ "RIB_IPV4_UNICAST\nSequence Number: 0\nPrefix Length: 0\nPrefix: 0.0.0.0\n";
		Pattern pattern = Pattern.compile("(\\w|\\W)*((Prefix: 1.0.0.0)|(Sequence Number: 2)|(Type: 13))(\\w|\\W)*");
		if (pattern.matcher(testMatcher).matches()) {
			System.out.println("Matched");
		} else {
			System.out.println("Did not match");
		}
		StringBuilder stringBuilder = new StringBuilder().append("asdasdasd").append("|").append("Hello").append("|");
		System.out.println(stringBuilder.toString());
		stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
		System.out.println(stringBuilder.toString());
	}
}

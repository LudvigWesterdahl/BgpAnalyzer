package main;

import Extract.Extractor;

public class Tester {
	public static void main(String args[]) {
		String test = "abc abc defjasdh abc okabcabcasd abc";
		System.out.println("abc can be found " + Extractor.prefixOccurences("abc", test) + " times in 'test' string.");
		String test2 = "--------------------asd-----asdbhasdoiajs------asdaosijdas--asd";
		System.out.println("Forward hyphens = " + Extractor.countHyphensInARow(test2, true));
		System.out.println("Backward hyphens = " + Extractor.countHyphensInARow(test2, false));
		
	}
}

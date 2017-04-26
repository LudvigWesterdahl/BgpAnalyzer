package main;

import java.util.regex.Pattern;

public class SecondTester {

	public static void main(String[] args) {
		System.out.println("Second Tester executed.");
		String regex = "(\\d*\\s)*19283";
		Pattern pattern = Pattern.compile(regex);
		if (pattern.matcher("18273 12912387 1983").matches()) {
			System.out.println("Matched");
		} else {
			System.out.println("Did not matchx");
		}
		

	}

}

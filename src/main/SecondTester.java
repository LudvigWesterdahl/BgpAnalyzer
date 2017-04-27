package main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
		
		new File("TestDirs/").mkdir();
		
		if (Files.exists(Paths.get("TestDirs/"))) {
			System.out.println("Dir exist");
		} else {
			System.out.println("Does not exist.");
		}
		
		System.out.println("hello world");
		System.out.println(String.join("", Collections.nCopies(5, "\b")));
		Set<String> set = new HashSet<>();
		set.add("Hello");
		set.add("Hello");
		set.add("World");
		for (String string : set) {
			System.out.println(string);
		}
	}

}

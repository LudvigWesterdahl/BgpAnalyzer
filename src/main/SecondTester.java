package main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.chrono.MinguoChronology;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SecondTester {

	public static void main(String[] args) {
		Map<Integer, Integer> nodes = new HashMap();
		nodes.put(1, 1);
		nodes.put(2, 3);
		nodes.put(3, 5);
		nodes.put(4, 999);
		for (Map.Entry<Integer, Integer> entry : nodes.entrySet()) {
			System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
		}
		
		
		System.out.println(min(3, 5));
		System.out.println(min(76, 76));
		System.out.println(min(43, 5));
		
		int[] a = new int[2];
		a[0] = 34;
		a[1] = 95;
		int[] b = Arrays.copyOf(a, a.length);
		System.out.println("a[0] = " + a [0]);
		System.out.println("a[1] = " + a [1]);
		System.out.println("b[0] = " + b [0]);
		System.out.println("b[1] = " + b [1]);
		a[0] = 1337;
		a[1] = 9;
		System.out.println("a[0] = " + a [0]);
		System.out.println("a[1] = " + a [1]);
		System.out.println("b[0] = " + b [0]);
		System.out.println("b[1] = " + b [1]);
		

	}
	public static int min(int a, int b) {
		  return (a <= b) ? a : b;
	}

}

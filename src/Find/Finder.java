package Find;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class <tt>finds</tt> MRT headers = files which are of interest.<br>
 * This class is the second stage in the BGP Analyzer pipeline.
 * 
 * <h1>BGP Analyzer pipeline</h1>
 * <i>Extractor</i> <tt>-></tt>
 * <i>Finder</i> <tt>-></tt>
 * <i>Provider</i> <tt>-></tt>
 * <i>Joiner</i> <tt>-></tt> 
 * <i>Result_File.txt</i>
 * 
 * @author Ludvig Westerdahl
 * 
 * @see		Extract.Extractor
 * @see     Provide.Provider
 * @see     Join.Joiner
 */
public class Finder {
	/*
	 * >>ARGUMENTS
	 * 1. IDENTIFIERS:
	 * 
	 * Prefix: -p, 1..n (String:IP)
	 * Sequence Number: -s, 1..n (int)
	 * Peer Index: -i, 1..n (int)
	 * Path Segment Value(AS): -v, 1..n (int)
	 * NEXT_HOP: -n, 1..n (String:IP)
	 * Peer AS: -a, 1..n (int)
	 * -p -s -i -v -n -a -d
	 * 
	 * 2. FILES & DIRECTORIES
	 * 
	 * Range of directories example: 1:10: -d 1..n-1..n
	 * 
	 * 
	 * */
	
	/*
	 * 1. Save the MRT-Header file in a String.
	 * 2. Run the Regex on it to see if its interesting.
	 * 3. Add it to the F_FILE if it matches.
	 * 4. Do again.
	 * 
	 * */
	public static void main(String args[]) {
		List<String> prefixes = new ArrayList<>();
		List<Integer> sequenceNumbers = new ArrayList<>();
		List<Integer> peerIndexes = new ArrayList<>();
		List<Integer> pathSegmentValues = new ArrayList<>();
		List<String> nextHops = new ArrayList<>();
		List<Integer> peerASs = new ArrayList<>();
		int[] dirRange = new int[2]; /* dirRange[0] = Start, dirRange[1] = End. */
		
		/* Parsing arguments. */
		Pattern commandPatter = Pattern.compile("-\\w");
		for (int i = 0; i<args.length; i++) {
			System.out.println("i = " + i);
			switch (args[i]) {
				case "-p":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						prefixes.add(args[i]);
						i++;
					}
					i--;
					break;
				case "-s":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						sequenceNumbers.add(Integer.parseInt(args[i]));
						i++;
					}
					i--;
					break;
				case "-i":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						peerIndexes.add(Integer.parseInt(args[i]));
						i++;
					}
					i--;
					break;
				case "-v":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						pathSegmentValues.add(Integer.parseInt(args[i]));
						i++;
					}
					i--;
					break;
				case "-n":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						nextHops.add(args[i]);
						i++;
					}
					i--;
					break;
				case "-a":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						peerASs.add(Integer.parseInt(args[i]));
						i++;
					}
					i--;
					break;
				case "-d":
					i++;
					dirRange[0] = Integer.parseInt(args[i].substring(0, args[i].indexOf(':')));
					dirRange[1] = Integer.parseInt(args[i].substring(args[i].indexOf(':')+1, args[i].length()));
					break;
				default:
					break;
			}
		}

		System.out.println("PREFIXES");
		for (String prefix : prefixes) {
			System.out.println(prefix);
		}
		System.out.println("---------");
		System.out.println("DIR RANGE");
		System.out.println("Start - " + dirRange[0]);
		System.out.println("End - " + dirRange[1]);
		System.out.println("---------");
		
		/* Generating regex. */
		
		/* LOOP */
		
		/* Reading files specified from argument '-d' in DIRRANGE.*/
		
		/* Matching the regex. */
		
		/* Add file name to F_FILES */
		
		/* LOOP */
		
		
		
		
	}
}

package Find;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

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
		boolean argsLeftToParse = true;
		int argIndex = 0; 
		while(argsLeftToParse) {
			for (int i = 0; i<args.length; i++) {
				
			}
			
			
			
		}
		
		
		for (String arg : args) {
			switch (arg) {
			case "-p":
				System.out.println("-p");
				prefixes.add(arg);
				break;
			case "-s":
				sequenceNumbers.add(Integer.parseInt(arg));
				System.out.println("-s");
				break;
			case "-i":
				peerIndexes.add(Integer.parseInt(arg));
				System.out.println("-i");
				break;
			case "-v":
				pathSegmentValues.add(Integer.parseInt(arg));
				System.out.println("-v");
				break;
			case "-n":
				nextHops.add(arg);
				System.out.println("-n");
				break;
			case "-a":
				peerASs.add(Integer.parseInt(arg));
				System.out.println("-a");
				break;
			case "-d":
				dirRange[0] = Integer.parseInt(arg.substring(0, arg.indexOf(':')));
				dirRange[1] = Integer.parseInt(arg.substring(arg.indexOf(':'), arg.length()));
				System.out.println("-d");
				break;
			default:
				System.out.println("default");
				break;
					
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
			
			
		}
	}
	
	
	

}

package Find;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import Extract.Extractor;

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
	public static String CHARS = "(\\w|\\W)*";
	public static String OR = "|";
	public static String FILE_BASE_NAME = "F_FILE";
	
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
		int foldersFileSize = 0;
		
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
				case "-f":
					i++;
					foldersFileSize = Integer.parseInt(args[i]);
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
		String testMatcher = "MRT Headern\nTimestamp: 1491904800(2017-04-11 12:00:00)\nType: 13(TABLE_DUMP_V2)\nSubtype: 2(RIB_IPV4_UNICAST)\nLength: 172\n"
				+ "RIB_IPV4_UNICAST\nSequence Number: 0\nPrefix Length: 0\nPrefix: 0.0.0.0\n";
		StringBuilder regexBuilder = new StringBuilder()
				.append(CHARS)
				.append("(");
		/*
		List<String> prefixes = new ArrayList<>();
		List<Integer> sequenceNumbers = new ArrayList<>();
		List<Integer> peerIndexes = new ArrayList<>();
		List<Integer> pathSegmentValues = new ArrayList<>();
		List<String> nextHops = new ArrayList<>();
		List<Integer> peerASs = new ArrayList<>();
		int[] dirRange = new int[2];
		*/
		for (String prefix : prefixes) {
			regexBuilder.append(Finder.generateRegex("Prefix: " + prefix)).append("|");
		}
		for (Integer seqNumber : sequenceNumbers) {
			regexBuilder.append(Finder.generateRegex("Sequence Number: " + seqNumber)).append("|");
		}
		for (Integer peerIndex : peerIndexes) {
			regexBuilder.append(Finder.generateRegex("Peer Index: " + peerIndex)).append("|");
		}
		for (Integer pathSegmentValue : pathSegmentValues) {
			regexBuilder.append(Finder.generateRegex("Path Segment Value: " + "\\d*\\s" + pathSegmentValue)).append("|");
		}
		for (String nextHop : nextHops) {
			regexBuilder.append(Finder.generateRegex("NEXT_HOP: " + nextHop)).append("|");
		}
		for (Integer peerAs : peerASs) {
			regexBuilder.append(Finder.generateRegex("Peer AS: " + peerAs)).append("|");
		}
		regexBuilder.delete(regexBuilder.length() - 1, regexBuilder.length()); /* Remove last '|'. */
		regexBuilder.append(")").append(CHARS);
		
		Pattern pattern = Pattern.compile(regexBuilder.toString());
		if (pattern.matcher(testMatcher).matches()) {
			System.out.println("Matched");
		} else {
			System.out.println("Did not match");
		}
		
		/* LOOP */
		
		/* Reading files specified from argument '-d' in DIRRANGE.*/
		try (FileOutputStream out = new FileOutputStream(Finder.FILE_BASE_NAME)){
			/*
			public final static String FILE_BASE_NAME = "E_FILE";
			public final static String DIR_BASE_NAME = "E_DIR";
			public final static String DIR_ROOT_NAME = "E_ROOT_DIR/";
			*/
			System.out.println("REGEX=");
			System.out.println(regexBuilder.toString());
			System.out.println("---");
			/*
			 * String is to big when checking. ( StackOverFlowError ).
			 * What to do:
			 * 1. Take like 1KB, check it with the regex.
			 * 2. Take another 1KB, check both 2KB with the regex.
			 * 3. Start over, but step nr 1 1KB should be the one from Step 2. So we dont miss any data.
			 * 
			 * Basically:
			 * Read kb1.
			 * Check regex on kb1.
			 * 
			 * Read kb2.
			 * Check regex on kb1 + kb2.
			 * Discard kb1.
			 * 
			 * Read kb3.
			 * Check regex on kb2 + kb3.
			 * Discard kb2.
			 * 
			 * Read kb4.
			 * Check regex on kb3 + kb4.
			 * Discard kb3.
			 * 
			 * Stop at any check and add file if necessary. 
			 * 
			 * 
			 * 
			 * */
			
			for (int i = dirRange[0]; i <= dirRange[1]; i++) {
				System.out.println("<1>");
				for (int y = 1; y <= foldersFileSize; y++) {
					ByteArrayOutputStream fileAsBytes = new ByteArrayOutputStream();
					String fileName = Extractor.DIR_ROOT_NAME + Extractor.DIR_BASE_NAME + "_" + i + "/" + Extractor.FILE_BASE_NAME + "_" + y;
					FileInputStream in = new FileInputStream(fileName);
					int bytesRead;
					byte[] buffer = new byte[Extractor.KB];
					System.out.println("<2>");
					while((bytesRead = in.read(buffer)) != -1) {
						fileAsBytes.write(buffer, 0, bytesRead);
					}
					String fileAsString = new String(fileAsBytes.toByteArray());
					if (pattern.matcher(testMatcher).matches()) {
						/* Found match in that file, add it to the out file.*/
						System.out.println("Found file.");
						out.write(fileName.getBytes());
						out.write("\n".getBytes());
						out.flush();
						
						System.out.println(fileName);
					}
					
					in.close();
				}
				
			}
			System.out.println("Done.");
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* Matching the regex. */
		
		/* Add file name to F_FILES */
		
		/* LOOP */
		
		
		
		
	}
	public static String generateRegex(String string) {
		return new StringBuilder().append("(").append(string).append(")").toString();
	}
}

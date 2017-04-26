package Provide;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.stream.events.StartDocument;

import com.sun.jndi.ldap.LdapClient;
import com.sun.org.apache.bcel.internal.generic.NEW;

import Extract.Extractor;
import Find.Finder;

/**
 * This class <tt>gets</tt> the information from the files from <tt>Finder</tt> stage
 * and puts that information in new files.<br>
 * This class is the third stage in the BGP Analyzer pipeline.
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
 * @see     Find.Finder
 * @see     Join.Joiner
 */
public class Provider {
	/*
	 * ---1. Sending prefix---
	 * 	You get the prefix, prefix length, AS-path, next-hop. since there might be different AS on different prefix length.
	 * 	All must be included.
	 * 	If there is two announcements for two prefixes, then we should put them under same prefix but add another AS-PAth.
	 * 	1.0.4.0/22
	 * 	AS-Path: 5837 1928 2837
	 * 	NEXT_HOP: 192.168.1.1
	 * 
	 * 	1.0.4.0/24
	 * 	AS-Path: 5837 1928 2837
	 * 	NEXT_HOP: 192.168.1.1
	 * 
	 * 
	 * ---2. Sending sequence number---
	 * 	Prefix, prefix length, peer index.
	 * 
	 * 
	 * ---3. Sending peer Index---
	 * 	prefix, prefix length.
	 * 
	 * 
	 * ---4. Sending a path segment value (an AS number)---
	 * 	Prefix with the length.
	 * 
	 * 	Peer-index
	 * 	The whole PATH. All numbers, AS-PATH.
	 * 	Next_HOP
	 *
	 * 
	 * ---5. Sending NEXT_HOP---
	 * 	You want Prefix of the MRT header and prefix length.
	 * Also the Path segment value.
	 * 
	 * ---6. Sending a peer AS---
	 * 	Collector:
	 * 	Peer Type:
	 * 	Peer BGP ID:
	 * 	Peer IP Address:
	 * 
	 * */
	public final static int NUMBER_OF_IDENTIFIERS = 7;
	public static String[] IDENTIFIERS = {"Prefix", "Sequence Number", "Peer Index", "Path Segment Value", "NEXT_HOP", "Peer AS"};
	
	public static void main(String[] args) {
		List<String> finderFileLines = new ArrayList<>();
		String finderFileName = "";
		String metaFileName = "";
		String regex = "";
		List<String>[] identifierDataLists = new ArrayList[NUMBER_OF_IDENTIFIERS];
		
		
		//Pattern commandPatter = Pattern.compile("-\\w");
		/**
		 * Parsing arguments.
		 * START
		 */
		
		for (int i = 0; i<args.length; i++) {
			switch (args[i]) {
				case "-f": /* Prefix */
					finderFileName = args[++i];
					break;
				case "-m":
					metaFileName = args[++i];
					break;
				default:
					break;
			}
		}
		/**
		 * Parsing arguments.
		 * END
		 */
		
		/**
		 * Read from F_FILE_X
		 * START
		 */
		int bytesRead;
		StringBuilder lineBuilder = new StringBuilder();
		byte[] buffer = new byte[Extractor.KB];
		try (FileInputStream in = new FileInputStream(finderFileName)){
			while((bytesRead = in.read(buffer)) != -1) {
				lineBuilder.append(new String(buffer, 0, bytesRead));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finderFileLines.addAll(Arrays.asList(lineBuilder.toString().split("\n")));
		regex = finderFileLines.remove(0); /* Get the regex to use. */
		finderFileLines.remove(0); /* Remove the line dividor. */
		for (String line : finderFileLines) {
			System.out.println(line);
		}
		System.out.println(metaFileName);
		/**
		 * Read from F_FILE_X
		 * END
		 */
		
		/**
		 * Read from F_META_FILE_X
		 * START
		 */
		try (FileInputStream in = new FileInputStream(metaFileName)){
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			for (int i = 0; i < IDENTIFIERS.length; i++) {
				identifierDataLists[i] = getIdentifierLines(IDENTIFIERS[i], bufferedReader);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < IDENTIFIERS.length; i++) {
			for (String line: identifierDataLists[i]) {
				System.out.println(line);
			}
		}
		/**
		 * Read from F_META_FILE_X
		 * END
		 */
		
		/* Loop START */
		for (String fileName : finderFileLines) {
			/**
			 * Search in E_FILE_X
			 * START
			 */
			
			try (FileInputStream in = new FileInputStream(fileName)){
				StringBuilder fileBuilder = new StringBuilder();
				byte[] fileBuffer = new byte[Extractor.KB];
				while((bytesRead = in.read(fileBuffer)) != -1) {
					fileBuilder.append(new String(fileBuffer, 0, bytesRead));
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				for (String nextHop : identifierDataLists[4]) {
					int nextHopIndex = fileBuilder.toString().indexOf(nextHop);
					if (nextHopIndex == -1) {
						continue;
					}
					String pathSegmentValue = "Path Segment Value: ";
					String tillPathSegmentString = fileBuilder.toString().substring(0, nextHopIndex);
					int pathSegmentValueIndex = tillPathSegmentString.lastIndexOf(pathSegmentValue);
					pathSegmentValueIndex += pathSegmentValue.length();
					int pathSegmentValueEndIndex = tillPathSegmentString.indexOf('\n', pathSegmentValueIndex);
					String segmentValues = tillPathSegmentString.substring(pathSegmentValueIndex, pathSegmentValueEndIndex);
					String[] values = segmentValues.split(" ");
					List<String> valuesList = new ArrayList<>(Arrays.asList(values));
					System.out.println("File: " + fileName);
					System.out.println("AS PATH:");
					valuesList.stream().forEach((v)->System.out.println(v));
					FileOutputStream out = new FileOutputStream("P_FILE_" + fileName.substring(fileName.lastIndexOf('_') + 1, fileName.length()));
					out.write(("NEXT_HOP: " + nextHop + "\n").getBytes());
					valuesList.stream().forEach((v)->{
						try {
							if (valuesList.indexOf(v) == valuesList.size() - 1) {
								out.write((v).getBytes());	
							} else {
								out.write((v + " -> ").getBytes());
							}
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					out.write(("\n").getBytes());
				}
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			
			
			
			/**
			 * Search in E_FILE_X
			 * END
			 */
			
			/**
			 * Write result to P_FILE_X
			 * START
			 */
			
			/**
			 * Write result to P_FILE_X
			 * END
			 */
		}
		
		
		
		/* Loop END*/
		
	}
	public static List<String> getIdentifierLines(String identifier, BufferedReader in) {
		BufferedReader bufferedReader = in;
		List<String> list = new ArrayList<>();
		try {
			String line;
			boolean foundIdentifier = false;
			while((line = bufferedReader.readLine()) != null) {
				if (foundIdentifier || Finder.getHeadPattern(identifier).matcher(line).matches()) {
					foundIdentifier = true;
					list.add(line);
					if (Pattern.compile(Finder.LINE_DIVIDER).matcher(line).matches()) {
						break;
					}
				}
			}
			list.remove(0); /* Remove first header line. */
			list.remove(list.size() - 1); /* Remove line divider. */
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
}

package Provide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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
	public final static int NUMBER_OF_IDENTIFIERS = 7;
	public final static String FILE_BASE_NAME = "P_FILE";
	public final static String DIR_ROOT_NAME = "P_ROOT_DIR/";
	public final static String FILE_META_BASE_NAME = "P_META_FILE";
	public static String[] IDENTIFIERS = {"Prefix", "Sequence Number", "Peer Index", "Path Segment Value", "NEXT_HOP", "Peer AS"};
	
	public static void main(String[] args) {
		List<String> finderFileLines = new ArrayList<>();
		String finderFileName = "";
		String metaFileName = "";
		List<String>[] identifierDataLists = new ArrayList[NUMBER_OF_IDENTIFIERS];
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
		try (FileInputStream in = new FileInputStream(Finder.DIR_ROOT_NAME + finderFileName)){
			while((bytesRead = in.read(buffer)) != -1) {
				lineBuilder.append(new String(buffer, 0, bytesRead));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finderFileLines.addAll(Arrays.asList(lineBuilder.toString().split("\n")));
		//regex = finderFileLines.remove(0); /* Get the regex to use. */
		//finderFileLines.remove(0); /* Remove the line dividor. */
		/**
		 * Read from F_FILE_X
		 * END
		 */

		/**
		 * Create Provider meta file.
		 * START
		 */
		try {
			FileOutputStream pMetaOut = new FileOutputStream(Provider.FILE_META_BASE_NAME);
			Integer size = finderFileLines.size();
			pMetaOut.write(size.toString().getBytes());
			pMetaOut.write("\n".getBytes());
			pMetaOut.flush();
			pMetaOut.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * Create Provider meta file.
		 * END
		 */
			

		/**
		 * Read from F_META_FILE_X
		 * START
		 */
		try (FileInputStream in = new FileInputStream(Finder.DIR_ROOT_NAME + metaFileName)){
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

		/**
		 * Read from F_META_FILE_X
		 * END
		 */
		
		/* Loop START */
		//System.out.println(finderFileLines.size());
		new File(Provider.DIR_ROOT_NAME).mkdir();
		/**
		 * Search in all E_FILE_X matched in FINDER.
		 * START
		 */
		for (String fileName : finderFileLines) {

			/**
			 * Search in E_FILE_X
			 * START
			 */
			try (FileInputStream in = new FileInputStream(fileName)){
				/**
				 * Read E_FILE_X to String.
				 */
				StringBuilder fileBuilder = new StringBuilder();
				byte[] fileBuffer = new byte[Extractor.KB];
				while((bytesRead = in.read(fileBuffer)) != -1) {
					fileBuilder.append(new String(fileBuffer, 0, bytesRead));
				}
				
				/* Create P_FILE_X */
				String pFile = DIR_ROOT_NAME + FILE_BASE_NAME + "_" + fileName.substring(fileName.lastIndexOf('_') + 1, fileName.length());
				FileOutputStream out = new FileOutputStream(pFile);
				String fileAsString = fileBuilder.toString();
				/* Write two header values. */
				out.write((getMrtInitialDataHeader(fileAsString, "Prefix: ") + "\n").getBytes());
				out.write((getMrtInitialDataHeader(fileAsString, "Prefix Length: ") + "\n").getBytes());
				out.flush();
				
				/* Write searched ASs to P_FILE_X. */
				StringBuilder asListBuilder = new StringBuilder();
				identifierDataLists[3].stream().forEach((v)->{
					if (fileAsString.contains(v)) {
						asListBuilder.append(v + ":");	
					}
				});
				
				/* Delete the last ':' from P_FILE_X. */
				if (asListBuilder.length() > 0) {
					asListBuilder.deleteCharAt(asListBuilder.length() - 1);	
				}
				out.write(asListBuilder.toString().getBytes());
				out.write("\n".getBytes());
				out.write("\n".getBytes());
				/* Loop over ASN (AS Numbers) in IDENTIFIERDATALISTS[3]. */
				for (String asn : identifierDataLists[3]) {
					/* Get an AS path containing ASN. */
					List<String> paths = getAsPaths(fileAsString, asn);
					/* Write all AS paths in PATHS to P_FILE_X. */
					paths.stream().forEach((v)->{
						try {
							out.write((v + "\n").getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
				out.close();
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
		}
		/**
		 * Search in all E_FILE_X matched in FINDER.
		 * END
		 */
	}
	/**
	 * This function takes a identifier, often a 'F_META_FILE_X' and returns the data following that identifier.
	 * 
	 * @param identifier		a MRT identifier, see Provider.IDENTIFIERS. 
	 * @param in				reader to file containing the data.
	 * @return					returns the data following IDENTIFIER until a line divider, Finder.LINE_DIVIDER occurs.
	 * 
	 * @see Find.Finder
	 */
	public static List<String> getIdentifierLines(String identifier, BufferedReader in) {
		List<String> list = new ArrayList<>();
		try {
			String line;
			boolean foundIdentifier = false;
			while((line = in.readLine()) != null) {
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
	
	/**
	 * This function returns header data from the beginning of a MRT file.
	 * @param mrtFile		a MRT file as a String.
	 * @param header		header that you are searching for.
	 * @return				the header value following the header.
	 */
	public static String getMrtInitialDataHeader(String mrtFile, String header) {
		int prefixStartIndex = mrtFile.indexOf(header) + header.length();
		int prefixEndIndex = mrtFile.indexOf('\n', prefixStartIndex);
		String prefixString = mrtFile.substring(prefixStartIndex, prefixEndIndex);
		return prefixString;

	}
	
	/**
	 * This function takes as arguments a MRT file (MRTFILE) and an AS number (ASN) and returns
	 * all AS paths which contains ASN in MRTFILE.
	 * @param mrtHeader		a MRT file as a String.
	 * @param asn			a AS number.
	 * @return				a list of all AS paths in MRTFILE containing ASN.
	 */
	public static List<String> getAsPaths(String mrtFile, String asn) {
		String psv = "Path Segment Value: ";
		List<String> asPathsList = new ArrayList<>();
		int currentAsnIndex = 0;
		for (int currPsvIndex = 0; currPsvIndex != -1; currPsvIndex = mrtFile.indexOf(psv, currPsvIndex + 1)) {
			int endOfLineIndex = mrtFile.indexOf('\n', currPsvIndex);
			String line = mrtFile.substring(currPsvIndex + psv.length(), endOfLineIndex);
			if (line.contains(asn)) {
				asPathsList.add(line);
			}
		}
		return asPathsList;
	}
	
}

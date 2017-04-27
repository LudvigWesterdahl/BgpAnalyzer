package Find;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import Extract.Extractor;

/**
 * This class <tt>finds</tt> MRT headers = files which are of interest.<br>
 * This class is the second stage in the BGP Analyzer pipeline.
 * 
 * <h1>Arguments</h1>
 * <tt>IDENTIFIERS</tt><br><br>
 * 
 * <i>Prefix:</i> <tt>-p</tt>, 1..n (String:IP)<br>
 * <i>Sequence Number:</i> <tt>-s</tt>, 1..n (int)<br>
 * <i>Peer Index:</i> <tt>-i</tt>, 1..n (int)<br>
 * <i>Path Segment Value(AS):</i> <tt>-v</tt>, 1..n (int)<br>
 * <i>NEXT_HOP:</i> <tt>-n</tt>, 1..n (String:IP)<br>
 * <i>Peer AS:</i> <tt>-a</tt>, 1..n (int)<br><br>
 * 
 * <tt>FILES & DIRECTORIES</tt><br><br>
 * 
 * <i>Range of directories:</i> <tt>-d</tt>, 1..n (int) : 1..n (int)<br>
 * 	example: -d 1:10, "Directory range 1 to and including 10.<br>
 * <i>Files in each folder:</i> <tt>-f</tt>, 1..n (int)<br>
 * example: -f 1000, "There are 1000 files in each folder."<br>
 * 
 * <h1>Results</h1>
 * <tt>F_META_FILE_X</tt><br>
 * File containing meta information about the <i>Finder</i> result file F_FILE_X
 * where 'X' is the <i>unique</i> identifier for each result.<br>
 * <tt>F_FILE_X</tt><br>
 * File containing the matched files from the <i>Finder</i> result.<br>
 * 
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
	public static String CHARS_NO_PARANTHESIS = "[[(\\w|\\W)]&&[^(]]*";
	public static String OR = "|";
	public static String FILE_BASE_NAME = "F_FILE";
	public static String FILE_META_BASE_NAME = "F_META_FILE";
	public static String DIR_ROOT_NAME = "F_ROOT_DIR/";
	public final static String LINE_DIVIDER = "-----------------------";

	
	public static void main(String args[]) {
		List<String> prefixes = new ArrayList<>();
		List<Integer> sequenceNumbers = new ArrayList<>();
		List<Integer> peerIndexes = new ArrayList<>();
		List<Integer> pathSegmentValues = new ArrayList<>();
		List<String> nextHops = new ArrayList<>();
		List<Integer> peerASs = new ArrayList<>();
		/* Directories to include in the search. From and including DIRRANGE[0] to and including DIRRANGE[1]. */
		Integer[] dirRange = new Integer[2];
		
		int foldersFileSize = 0;
		new File(Finder.DIR_ROOT_NAME).mkdir();

		/**
		 * Parsing arguments.
		 * START
		 */
		Pattern commandPatter = Pattern.compile("-\\w");
		for (int i = 0; i<args.length; i++) {
			switch (args[i]) {
				case "-p": /* Prefix */
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						prefixes.add(args[i]);
						i++;
					}
					i--;
					break;
				case "-s": /* Sequence number */
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						sequenceNumbers.add(Integer.parseInt(args[i]));
						i++;
					}
					i--;
					break;
				case "-i": /* Peer index */
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						peerIndexes.add(Integer.parseInt(args[i]));
						i++;
					}
					i--;
					break;
				case "-v": /* Path segment value */
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						pathSegmentValues.add(Integer.parseInt(args[i]));
						i++;
					}
					i--;
					break;
				case "-n": /* Next-hop */
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						nextHops.add(args[i]);
						i++;
					}
					i--;
					break;
				case "-a": /* Peer AS */
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						peerASs.add(Integer.parseInt(args[i]));
						i++;
					}
					i--;
					break;
				case "-d": /* Directory range */
					i++;
					dirRange[0] = Integer.parseInt(args[i].substring(0, args[i].indexOf(':')));
					dirRange[1] = Integer.parseInt(args[i].substring(args[i].indexOf(':')+1, args[i].length()));
					break;
				case "-f": /* Folder file size */
					i++;
					foldersFileSize = Integer.parseInt(args[i]);
				default:
					break;
			}
		}
		/**
		 * Parsing arguments.
		 * END
		 */
		
		/**
		 * Generate regex from arguments.
		 * START
		 */
		StringBuilder regexBuilder = new StringBuilder()
				.append(CHARS)
				.append("(");
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
			regexBuilder.append(Finder.generateRegex("Path Segment Value: " + "(\\d*\\s)*" + pathSegmentValue)).append("|");
		}
		for (String nextHop : nextHops) {
			regexBuilder.append(Finder.generateRegex("NEXT_HOP: " + nextHop)).append("|");
		}
		for (Integer peerAs : peerASs) {
			regexBuilder.append(Finder.generateRegex("Peer AS: " + peerAs)).append("|");
		}
		regexBuilder.delete(regexBuilder.length() - 1, regexBuilder.length()); /* Remove last '|'. */
		regexBuilder.append(")").append(CHARS);
		/* Regex patter to use when searching the file. */
		Pattern pattern = Pattern.compile(regexBuilder.toString());
		/**
		 * Generate regex from arguments.
		 * END
		 */
		
		/**
		 * Search for matching files.
		 * START
		 */
		
		String lineDivider = Finder.LINE_DIVIDER + "\n"; 
		/* Generate a F_FILE_X name where X is a unique integer. */
		String currentFinderFileName = Finder.generateUniqueFileName(Finder.FILE_BASE_NAME);
		/* Create F_FILE_X. */
		try (FileOutputStream out = new FileOutputStream(currentFinderFileName)){
			/* Loop over all E_DIR_X. */
			for (int i = 1; Files.exists(Paths.get(Extractor.DIR_ROOT_NAME + Extractor.DIR_BASE_NAME + "_" + i)); i++) {
				/*
				 * Loop over all E_FILE_X.
				 * 
				 * E_DIR_1 contains files 'E_FILE_1' to 'E_FILE_(FOLDERFILESIZE)'
				 * E_DIR_2 contains files 'E_FILE_(1 + FOLDERFILESIZE)' to 'E_FILE_(2 * FOLDERFILESIZE)'
				 * E_DIR_3 contains files 'E_FILE_(1 + 2 * FOLDERFILESIZE)' to 'E_FILE_(3 * FOLDERFILESIZE)'
				 * ...
				 * E_DIR_X contains files 'E_FILE(1 + (X - 1) * FOLDERFILESIZE)' to 'E_FILE(X * FOLDERFILESIZE)'
				 * */
				for (int y = 1 + foldersFileSize * (i - 1) ; y <= foldersFileSize * i; y++) {
					ByteArrayOutputStream partialFileAsBytes = new ByteArrayOutputStream();
					String fileName = Extractor.DIR_ROOT_NAME + Extractor.DIR_BASE_NAME + "_" + i + "/" + Extractor.FILE_BASE_NAME + "_" + y;
					/* End of the directory, break the loops. */
					if (!Files.exists(Paths.get(fileName))) {
						y = foldersFileSize*i;
						break;
					}
					
					FileInputStream in = new FileInputStream(fileName); /* E_FILE_X*/
					
					int bytesRead1 = 0;
					int bytesRead2 = 0;
					byte[] buffer1 = new byte[Extractor.KB / 4]; /* First step buffer.*/
					byte[] buffer2 = new byte[Extractor.KB / 4]; /* Second step buffer. */
					
					/* Stepwise read to avoid StackOverFlow error with regex matching. */
					while(bytesRead1 != -1 && bytesRead2 != -1) {
						/* Read first step.*/
						bytesRead1 = in.read(buffer1);
						if (bytesRead1 != -1) {
							partialFileAsBytes.write(buffer1, 0, bytesRead1); 
						} else {
							break;
						}
						
						/* Match steps. */
						if (pattern.matcher(new String(partialFileAsBytes.toByteArray())).matches()) {
							out.write(fileName.getBytes());
							out.write("\n".getBytes());
							out.flush();
							break;
						}
						
						/* Keep first step and discard second step.*/
						partialFileAsBytes.reset();
						partialFileAsBytes.write(buffer1, 0, bytesRead1);
						
						/* Read second step.*/
						bytesRead2 = in.read(buffer2);
						if (bytesRead2 != -1) {
							partialFileAsBytes.write(buffer2, 0, bytesRead2);
						} else {
							break;
							
						}
						
						/* Match steps.*/
						if (pattern.matcher(new String(partialFileAsBytes.toByteArray())).matches()) {
							out.write(fileName.getBytes());
							out.write("\n".getBytes());
							out.flush();
							break;
						}
						
						/* Keep second step and discard first step. */
						partialFileAsBytes.reset();
						partialFileAsBytes.write(buffer2, 0, bytesRead2);
					}		
					in.close();
				}
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * Search for matching files.
		 * END
		 */
		
		/**
		 * Write FINDER meta data.
		 * START
		 */
		try (FileOutputStream out = new FileOutputStream(Finder.generateUniqueFileName(FILE_META_BASE_NAME))){
			out.write(("Meta data for '" + currentFinderFileName + "'\n").getBytes());
			out.write(lineDivider.getBytes());
			Finder.writeListToStream(prefixes, out, "> Prefixes (Prefix)\n", lineDivider, "\n");
			Finder.writeListToStream(sequenceNumbers, out, "> Sequence numbers (Sequence Number)\n", lineDivider, "\n");
			Finder.writeListToStream(peerIndexes, out, "> Peer indexes (Peer Index)\n", lineDivider, "\n");
			Finder.writeListToStream(pathSegmentValues, out, "> AS Paths (Path Segment Value)\n", lineDivider, "\n");
			Finder.writeListToStream(nextHops, out, "> Next hops (NEXT_HOP)\n", lineDivider, "\n");
			Finder.writeListToStream(peerASs, out, "> Peer ASs (Peer AS)\n", lineDivider, "\n");
			//Finder.writeListToStream(new ArrayList<>(Arrays.asList(dirRange[0])), out, "> Directory Search\nFrom (", "", "");
			//Finder.writeListToStream(new ArrayList<>(Arrays.asList(dirRange[1])), out, ") to and including (", ")\n" + lineDivider, "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * Write FINDER meta data.
		 * START
		 */
	}

	/**
	 * Helper functions.
	 * START
	 */
	public static <E> void writeListToStream(List<E> list, OutputStream out, String head, String tail, String itemDivider) {
		try {
			out.write(head.getBytes());
			list.forEach((v)->{
				try {
					out.write(v.toString().getBytes());
					out.write(itemDivider.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			out.write(tail.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static <E> void writeListToStream(List<E> list, OutputStream out, String head, String tail, String itemDivider, boolean include) {
		try {
			out.write(head.getBytes());
			list.forEach((v)->{
				try {
					out.write(v.toString().getBytes());
					out.write(itemDivider.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			out.write(tail.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String generateRegex(String string) {
		return new StringBuilder().append("(").append(string).append(")").toString();
	}
	
	public static String generateUniqueFileName(String name) {
		for (int i = 1;; i++) {
			if (!Files.exists(Paths.get(name + "_" + i))) {
				return Finder.DIR_ROOT_NAME + name + "_" + i;
			}
		}
	}
	public static Pattern getHeadPattern(String headIdentifier) {
		return Pattern.compile(new StringBuilder()
				.append("[> ]")
				.append(Finder.CHARS_NO_PARANTHESIS)
				.append("[(]")
				.append(headIdentifier)
				.append("[)]").toString());
	}
	/**
	 * Helper functions.
	 * END
	 */

}

package Extract;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.NonWritableChannelException;
import java.security.KeyStore.TrustedCertificateEntry;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.crypto.Data;

/**
 * This class <tt>extracts</tt> MRT headers from an <tt>ASCII</tt> encoded MRT dump file.<br>
 * This class is the first stage in the BGP Analyzer pipeline.
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
 * @see		Find.Finder
 * @see     Provide.Provider
 * @see     Join.Joiner
 */
public class Extractor {
	public final static int KB = 1024;
	public final static int MB = KB*KB;
	public final static int LINE_SEP_LENGTH = 63;
	public final static Pattern LINE_PATTERN = Pattern.compile("[[(\\w|\\W)]&&[^-]]*-{32,63}[(\\w|\\W)]*");
	public final static Pattern HYPHEN_PATTER = Pattern.compile("(-{1,63})");
	public final static Pattern FULL_LINE_SEP_PATTERN = Pattern.compile("[[(\\w|\\W)]&&[^-]]*-{63}[(\\w|\\W)]*");
	public final static String FILE_BASE_NAME = "E_FILE";
	public final static String DIR_BASE_NAME = "E_DIR";
	public final static String DIR_ROOT_NAME = "E_ROOT_DIR/";
	public final static String FULL_LINE_SEP = "---------------------------------------------------------------"; // 63 '-'.
	private static int FILE_ID_COUNTER = 1;
	private static int DIR_ID_COUNTER = 1;
	private static String ARGUMENTS_ERROR_TEXT = "Bad arguments.\n" + "Argument 1. Filename of the data file.\n"
			+ "Argument 2. Number of files for each folder.";
	
	public static void main(String[] args) {
		String dataFileName = "";
		int folderFilesSize = 0;
		
		if(args.length > 2 || args.length < 2) {
			System.err.println(ARGUMENTS_ERROR_TEXT);
			System.exit(1);
		} else {
			dataFileName = args[0];
			try {
				folderFilesSize = Integer.parseInt(args[1]);	
			} catch (NumberFormatException e) {
				System.err.println(ARGUMENTS_ERROR_TEXT);
				System.exit(1);
			}
			
		}
		
		new File(DIR_ROOT_NAME).mkdir();
		new File(DIR_ROOT_NAME + DIR_BASE_NAME + "_" + DIR_ID_COUNTER).mkdir();
		
		try (FileInputStream from = new FileInputStream(new File(dataFileName));) {
			ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[LINE_SEP_LENGTH];
			int bytesRead;
			/* If the matching worked, find that index in buffer, save the rest to a new file
			 * reset the stream and the rest of the data in buffer needs to be added to the resetted bufferStream.
			 * */
			int numberOfFiles = 0;
			while((bytesRead = from.read(buffer)) != -1){
				String bufferAsString = new String(buffer, 0, bytesRead);
				
				if (LINE_PATTERN.matcher(bufferAsString).matches()) {
					/* There is at least 32 hyphens in BUFFER. */
					int forwardHyphens = Extractor.countHyphensInARow(bufferAsString, true);
					int backwardHyphens = Extractor.countHyphensInARow(bufferAsString, false);
					byte[] bufferToWrite;
					byte[] bufferToNextFile;
					/* One of these will be true. */
					if (forwardHyphens > 31) {
						/* fowardHyphens > 31 */
						int hyphensToTakeFromStream = 63 - forwardHyphens;
						int newLength = bufferStream.toByteArray().length - hyphensToTakeFromStream;
						/* BUFFERSTREAM = 101010010101---------------*/
						/* BUFFER = -----01010 */

						bufferToWrite = Arrays.copyOf(bufferStream.toByteArray(), newLength);
						FileOutputStream out = new FileOutputStream(new File(Extractor.generateFileName()));
						numberOfFiles++; /* Created new file. */
						out.write(bufferToWrite);
						out.flush();
						bufferToNextFile = Arrays.copyOfRange(bufferStream.toByteArray(), newLength, bufferStream.toByteArray().length);
						bufferStream.reset();
						bufferStream.write(bufferToNextFile);
						bufferStream.write(buffer);
						/* There are hyphens in the stream which should be written to the next file. */
					} else {
						/* backwardHyphens > 31 */
						int hyphensToTakeToNextFile = backwardHyphens;
						/* BUFFERSTREAM = 101010010101102039*/
						/* BUFFER = 0101010----- */
						/* */
						bufferToNextFile = Arrays.copyOfRange(buffer, buffer.length - hyphensToTakeToNextFile, buffer.length);
						bufferStream.write(buffer, 0, buffer.length - hyphensToTakeToNextFile);
						bufferToWrite = bufferStream.toByteArray();
						FileOutputStream out = new FileOutputStream(new File(Extractor.generateFileName()));
						byte[] arr = Arrays.copyOfRange(buffer, 0, buffer.length - hyphensToTakeToNextFile);
						out.write(bufferToWrite);
						out.flush();
						bufferStream.reset();
						bufferStream.write(bufferToNextFile);
						numberOfFiles++;
						
					}
					if (numberOfFiles % folderFilesSize == 0) {
						DIR_ID_COUNTER++;
						new File(DIR_ROOT_NAME + DIR_BASE_NAME + "_" + DIR_ID_COUNTER).mkdir();
					}
					
				} else {
					bufferStream.write(buffer, 0, bytesRead);
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String generateFileName(){
		return new StringBuilder().append(DIR_ROOT_NAME).append(DIR_BASE_NAME).append("_").append(DIR_ID_COUNTER).append("/")
				.append(FILE_BASE_NAME).append("_").append(FILE_ID_COUNTER++).toString();
	}
	// Counts the occurrences of a prefix in string.
	public static int prefixOccurences(String prefix, String string) {
		int occurences = 0;
		int index = 0;
		while((index = string.indexOf(prefix, index)) != -1) {
			occurences++;
			index++;
			
		}
		return occurences;
	}
	// direction = true -> forward.
	// direction = false -> backwards
	// We can easily add fromIndex to the function and maybe what character to search for aswell.
	// just change the pattern and call matcher.find(startIndex).
	public static int countHyphensInARow(String string, boolean direction) {
		if (!direction) {
			string = new StringBuilder(string).reverse().toString();
		}
		Matcher matcher = HYPHEN_PATTER.matcher(string);
		if(matcher.find()) {
			if(string.indexOf(matcher.group(1)) != 0) {
				return 0;
			} else {
				return matcher.group(1).length();	
			}
		} else {
			return 0;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}

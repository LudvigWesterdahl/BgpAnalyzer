package Wrap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;



import Extract.Extractor;
import Find.Finder;
import Join.Joiner;
import Provide.Provider;
/*TODO: 
 * 1. Add argument to take in how many threads to use when finding and providing.
 * 2. Takes F_META_FILE_X from arguments, how should I do that? Because if u run it second time,
 * it will take from the old files not from the new command line arguments somehow, the file
 * is basically static.
 * 
 * */
/**
 * TODO:
 * 1. Make the buffer bigger than 63, to read more and make it more efficient.
 * 
 * 
 */

/**
 * TODO Project:
 * 1. Document the code.
 * 2. Clean up functions.
 * 3. final static variables, can some be private?
 * 4. 
 * 
 * TODO Extractor:
 * 1. Make the buffer bigger than 63 to read more and make it more efficient.
 * 
 * TODO Finder:
 * 1. (BUG) When choosing other directories, the file name gets a bit messed up, fix that ASAP.
 *  
 * TODO Provider:
 * 1. Maybe not take the Meta file in the arguments, get it from the file instead. 
 * 2. Search in the file for the data and extract it to P_ROOT_DIR/P_FILE_X or some kind.
 * 3. Clean up the code when searching for file in provider.
 * 
 * TODO Joiner:
 * 
 */

public class Wrapper {
	public static void main(String[] args) {
		List<String> extractorArgs = new ArrayList<>();
		List<String> finderArgs = new ArrayList<>();
		List<String> providerArgs = new ArrayList<>();
		List<String>  joinerArgs = new ArrayList<>();
		Pattern commandPatter = Pattern.compile("--\\w");
		for (int i = 0; i<args.length; i++) {
			switch (args[i]) {
				case "--e":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						extractorArgs.add(args[i]);
						i++;
					}
					i--;
					break;
				case "--f":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						finderArgs.add(args[i]);
						i++;
					}
					i--;
					break;
				case "--p":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						providerArgs.add(args[i]);
						i++;
					}
					i--;
					break;
				case "--j":
					i++;
					while(i < args.length && !commandPatter.matcher(args[i]).matches()) {
						joinerArgs.add(args[i]);
						i++;
					}
					i--;
					break;
				default:
					break;
			}
		}
		
		System.out.println("Extracting...");
		Extractor.main(extractorArgs.stream().toArray(String[]::new));
		System.out.println("Done");
		System.out.println("Finding...");
		Finder.main(finderArgs.toArray(new String[0]));
		System.out.println("Done");
		System.out.println("Providing...");
		Provider.main(providerArgs.toArray(new String[0]));
		System.out.println("Done");
		System.out.println("Joining...");
		Joiner.main(joinerArgs.toArray(new String[0]));
		System.out.println("Done");
	}
}

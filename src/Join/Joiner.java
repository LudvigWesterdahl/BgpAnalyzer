package Join;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omg.PortableServer.ServantActivator;

import Provide.Provider;

/**
 * This class <tt>puts</tt> together in one final file.<br>
 * This class is the fourth and final stage in the BGP Analyzer pipeline.
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
 * @see     Provide.Provider
 */
public class Joiner {
	List<String> asList = new ArrayList<>();
	public final static String DIR_ROOT_NAME = "J_ROOT_DIR/";
	public final static String FILE_BASE_NAME = "J_FILE";
	public static int fileCounter = 1;
	
	public static void main(String[] args) {
		try {
			FileInputStream pMetaIn = new FileInputStream(Provider.FILE_META_BASE_NAME);
			BufferedReader pMetaReader = new BufferedReader(new InputStreamReader(pMetaIn));
			String number = pMetaReader.readLine();
			pMetaReader.close();
			int numberOfFiles = Integer.parseInt(number);
			
			new File(DIR_ROOT_NAME).mkdir();
			BufferedReader fileReader;
			//System.out.println("fileCounter - 1 = " + (fileCounter - 1));
			//System.out.println("number of files = " + numberOfFiles);
			Set<String> allInterestingPaths = new HashSet<>();
			for (int i = 1; fileCounter <= numberOfFiles; i++) {
				if (Files.exists(Paths.get(Provider.DIR_ROOT_NAME + Provider.FILE_BASE_NAME + "_" + i))) {
					String fileName = Provider.DIR_ROOT_NAME + Provider.FILE_BASE_NAME + "_" + i;
					FileInputStream in = new FileInputStream(fileName);
					fileReader = new BufferedReader(new InputStreamReader(in));
					String prefix = fileReader.readLine();
					String prefixLength = fileReader.readLine();
					List<String> searchedASs = Arrays.asList(fileReader.readLine().split(":"));
					/* No other connection to searched ASs.*/
					/*
					if (searchedASs.size() < 2) {
						numberOfFiles--;
						continue; 
					}
					*/
					String asPath;
					
					Set<String> interestingPaths = new HashSet<>(); /* Only unique paths. */
					while((asPath = fileReader.readLine()) != null) {
						int occurances = 0;
						for (String asn : searchedASs) {
							/* Only if the ASN is the first ASN in ASPATH. */
							
							StringBuilder reversePath = new StringBuilder(asPath);
							reversePath.reverse();
							StringBuilder asnReverse = new StringBuilder(asn);
							asnReverse.reverse();
							if (asPath.contains(asn)) {
								interestingPaths.add(asPath);
								allInterestingPaths.add(asPath);
							}
							
							/*
							if (reversePath.toString().startsWith(asnReverse.toString(), 0)) {
								System.out.println("Added path: " + asPath + " for asn '" + asn + "'.");
								interestingPaths.add(asPath);
								allInterestingPaths.add(asPath);
							}
							*/
							/*
							if (asPath.contains(asn)) {
								occurances++;
								if (occurances > 1) {
									interestingPaths.add(asPath);
									allInterestingPaths.add(asPath);
									break;
								}
							}
							*/
						}
					}
					if (interestingPaths.size() < 1) {
						numberOfFiles--;
						break;
					}
					FileOutputStream out = new FileOutputStream(DIR_ROOT_NAME + FILE_BASE_NAME + "_" + fileCounter);
					out.write((prefix + "\n").getBytes());
					out.write((prefixLength + "\n").getBytes());
					interestingPaths.stream().forEach((v)->{
						try {
							out.write((v + "\n").getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					fileCounter++;
					out.close();
				} else {
					continue;
				}
			}
			
			FileOutputStream out = new FileOutputStream(DIR_ROOT_NAME + FILE_BASE_NAME + "_" + "JOINED");
			allInterestingPaths.stream().forEach((v)->{
				try {
					out.write((v + "\n").getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeDuplicateLines(String fileName) {
		Set<String> set = new HashSet<>();
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			String line;
			while ((line = in.readLine()) != null) {
				set.add(line);
			}
			
			FileOutputStream out = new FileOutputStream(fileName);
			set.stream().forEach((v)->{
				try {
					out.write(v.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	
	
}

package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import Extract.Extractor;

public class Splitter {

	public static void main(String[] args) {
		int bytesRead = 0;
		String line = "";
		try (FileInputStream file = new FileInputStream(args[0])){
			BufferedReader inReader = new BufferedReader(new InputStreamReader(file));
			for (int i = 1; i <= 16; i++) {
				FileOutputStream out = new FileOutputStream(args[0] + "_" + i);
				out.write((line + "\n").getBytes());
				/*
				int bytes;
				byte[] buffer = new byte[Extractor.MB * 10];
				while((bytes = file.read(buffer)) != -1) {
					bytesRead += bytes;
					out.write(buffer, 0, bytes);
					if (bytesRead > Extractor.MB * Extractor.KB) {
						break;
					}
				}
				*/
				
				/* Write line to the output file. */
				boolean goodSize = false;
				while((line = inReader.readLine()) != null) {
					if (goodSize || bytesRead > Extractor.MB * Extractor.KB){
						goodSize = true;
						if (line.matches(Extractor.FULL_LINE_SEP)) {
							bytesRead = 0;
							break;							
						} else {
							bytesRead += line.getBytes().length;
							out.write((line + "\n").getBytes());
							out.flush();
						}
						
					} else {
						bytesRead += line.getBytes().length;
						out.write((line + "\n").getBytes());
						out.flush();
					}
				}
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

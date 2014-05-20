package is.bthj.itu.datamining.flicksters.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The CSVFileReader class is used to load a csv file
 * @author andershh and jang
 *
 */
public class CSVFileReader {
	/**
	 * The read method reads in a csv file as a two dimensional string array.
	 * Please note it is assumed that ';' is used as separation character.
	 * @param csvFile Path to file
	 * @param useNullForBlank Use empty string for missing values?
	 * @return Two dimensional string array containing the data from the csv file
	 * @throws IOException
	 */
	public static String[][] read(String csvFile, String separator, boolean useNullForBlank)
			throws IOException {
		List<String[]> lines = new ArrayList<String[]>();

		BufferedReader bufRdr = new BufferedReader(new FileReader(new File(
				csvFile)));
		// read the header
		String line = bufRdr.readLine();
		StringTokenizer tok = new StringTokenizer(line, separator);
		final int numberOfColumns = tok.countTokens();

		// read each line of text file
		while ((line = bufRdr.readLine()) != null) {
			int col = 0;
			StringTokenizer st = new StringTokenizer(line, separator);
			String[] lineTokens = new String[numberOfColumns];
			while (st.hasMoreTokens()) {
				// get next token and store it in the array
				lineTokens[col] = st.nextToken().replaceAll("^\"|\"$", "");
				if (!useNullForBlank && lineTokens[col] == null)
					lineTokens[col] = "";
				col++;
			}
			// If last column was null
			if (!useNullForBlank) {
				while (col < numberOfColumns) {
					if (lineTokens[col] == null)
						lineTokens[col] = "";
					col++;
				}
			}

			lines.add(lineTokens);
		}
		String[][] ret = new String[lines.size()][];
		bufRdr.close();
		return lines.toArray(ret);
	}
}

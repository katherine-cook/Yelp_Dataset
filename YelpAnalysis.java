package hw6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;

public class YelpAnalysis {
	static FileReader fileReader = null;
	static BufferedReader bufferedReader = null;

	public static void main(String[] args) {
		Map<String, Integer> corpusDFCount = new HashMap<String, Integer>(); // creates a map of the number of documents
																				// each word appears in
		List<Business> businessList = new ArrayList<Business>();// creates a list of business objects
		final long startTime = System.currentTimeMillis();

		try {// try to open file
			fileReader = new FileReader("yelpDatasetParsed_full.txt");
																											
			bufferedReader = new BufferedReader(fileReader);
			while (true) {
				Business b = readBusiness(bufferedReader); // create a Business object with ID, Name, Address, review
				if (b == null) // end of file and processed all businesses
					break;
				businessList.add(b); // add each business to businessList
			}
		} catch (IOException ex) { // if file is not found, throw an exception
			System.out.println("Error reading file name");
		} finally {
			try {
				if(fileReader==null)//check for null pointers
					fileReader.close();// close streams
				if(bufferedReader==null)
					bufferedReader.close();
			} catch (Exception ex) {
				System.out.println("Error closing stream");
			}
		}

		for (Business b : businessList) // add words from each business to corpus count
			addDocumentCount(corpusDFCount, b);

		Comparator<Business> comp = new Comparator<Business>() { // comparator to compare character count of each
																	// business review
			public int compare(Business b1, Business b2) {
				return ((Integer) b2.charCount()).compareTo((Integer) b1.charCount());// compare the character count of
																						// each business from biggest to
																						// smallest
			}
		};

		Collections.sort(businessList, comp);// sort businesses by character count from highest number of characters to
												// lowest number of characters

		// for the top 10 businesses with most review characters
		for (int i = 0; i < 10; i++) {
			if (i >= businessList.size()) {
				break;
			}
			Map<String, Double> tfidfScoreMap = getTfidfScore(corpusDFCount, businessList.get(i), 5);
			// Entry is a static nested interface of class Map
			List<Map.Entry<String, Double>> tfidfScoreList = new ArrayList<>(tfidfScoreMap.entrySet()); // create a list
																										// of maps of
																										// tfidf scores
			sortByTfidf(tfidfScoreList);
			System.out.println(businessList.get(i));// print Businesses in format given by toString method in Business
													// class
			printTopWords(tfidfScoreList, 30);// print (word, tfidf score) for number of words specified by 2nd
												// parameter
		}
		final long endTime = System.currentTimeMillis();
		System.out.print("\ntotal time: " + (endTime - startTime));// how many milliseconds the program took to run,
																	// usually about 1.1 minutes for full dataset
	}

	public static Business readBusiness(BufferedReader bufferedReader) throws IOException {
		String line = null;
		line = bufferedReader.readLine();// continue reading each line until end of document
		if (line == null) {
			bufferedReader.close();
			return null;
		}

		String[] arrOfBuisElems = line.substring(1, line.length() - 1).split(",");// use substring to take off the
																					// opening and closing brace of the
																					// input
		return new Business(arrOfBuisElems[0], arrOfBuisElems[1], arrOfBuisElems[2], arrOfBuisElems[3]);
	}

	public static void addDocumentCount(Map<String, Integer> corpusDFCount, Business b) {
		Map<String, Integer> repeatedWordsMap = b.repeatedWordCount();// gets a map of all words in business b's review
		for (Map.Entry<String, Integer> entry : repeatedWordsMap.entrySet()) {// repeated words are all non-unique words
																				// from the document
			String key = entry.getKey();
			Integer value = entry.getValue();
			// System.out.println("repeated word count: "+ b.repeatedWordCount().size());
			if (value > 0) {
				Integer oldCount = corpusDFCount.get(key); // number of document word currently occurs in
				if (oldCount == null) { // if word doesn't already appear in the corpus, set the count to 0
					oldCount = 0;
				}
				corpusDFCount.put(key, oldCount + 1);// if the word is already in the corpusDF, increment the corpus
														// count by 1
			}
		}

	}

	public static void sortByTfidf(List<Map.Entry<String, Double>> tfidfScoreList) {
		// sorts list by highest tfidf score
		// list of maps and each map contains the words in the document and their tfidf
		// score
		Comparator<Map.Entry<String, Double>> comparatorScore = new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> m2, Map.Entry<String, Double> m1) {

				return m1.getValue().compareTo(m2.getValue());
			}
		};
		Collections.sort(tfidfScoreList, comparatorScore); // sorts list of words in a document by their tfidf score,
															// highest score first

	}

	private static void printTopWords(List<Entry<String, Double>> tfidfScoreList, int numWordsInOutput) {
		for (int i = 0; i < numWordsInOutput; i++) { // numWordsInOutput is how many words to output for each business,
														// in this case the 30 words with the highest tfidf score for
														// each business
			String word = (String) tfidfScoreList.get(i).getKey();// get word
			Double score = (Double) tfidfScoreList.get(i).getValue();// get tdfidf score of that word
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2); // output with 2 numbers after the decimal place
			System.out.print("(" + word + "," + df.format(score) + ") ");
			// System.out.print("(" + (Map.Entry<String,Double>)tfidfScoreList.get(i) +
			// ")");
		}
		System.out.println(" ");
	}

	public static Map<String, Double> getTfidfScore(Map<String, Integer> corpusDFCount, Business b, int minOccurence) {
		Map<String, Double> tfidfScoreMap = new HashMap<String, Double>(); //
		// frequency of the word in the document
		// number of documents the word occurs in

		Map<String, Integer> repeatWordMap = b.repeatedWordCount();
		for (Map.Entry<String, Integer> entry : repeatWordMap.entrySet()) {
			String word = entry.getKey();
			Integer count = entry.getValue();// frequency in current document
			// System.out.println("repeated word count: "+ b.repeatedWordCount().size());

			Double corpCount = Double.valueOf(corpusDFCount.get(word));
			Double score = Double.valueOf(count) / corpCount;
			if (corpCount < (double) minOccurence) { // set words with tfidf score less than minOccurences to 0.0
				score = 0.0;// takes care of slang words/mispellings
			}
			tfidfScoreMap.put(word, score);

		}
		// System.out.println(b.businessName + " \n" + tfidfScoreMap);
		return tfidfScoreMap; // returns map of all words in this document that occur more than 5 times and
								// their tfidf score (removes slang words)
	}

}

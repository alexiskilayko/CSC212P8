package edu.smith.cs.csc212.p8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class CheckSpelling {
	/**
	 * Read all lines from the UNIX dictionary.
	 * @return a list of words!
	 */
	public static List<String> loadDictionary() {
		long start = System.nanoTime();
		List<String> words;
		try {
			// Read from a file:
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time +" seconds.");
		return words;
	}
	
	/**
	 * This method looks for all the words in a dictionary.
	 * @param words - the "queries"
	 * @param dictionary - the data structure.
	 */
	public static void timeLookup(List<String> words, Collection<String> dictionary) {
		long startLookup = System.nanoTime();
		
		int found = 0;
		for (String w : words) {
			if (dictionary.contains(w)) {
				found++;
			}
		}
		
		
		long endLookup = System.nanoTime();
		double fractionFound = found / (double) words.size();
		double timeSpentPerItem = (endLookup - startLookup) / ((double) words.size());
		int nsPerItem = (int) timeSpentPerItem;
		System.out.println(dictionary.getClass().getSimpleName()+": Lookup of items found="+fractionFound+" time="+nsPerItem+" ns/item");
	}
	
	public static List<String> createMixedDataset(List<String> yesWords, int numSamples, double fractionYes) {
		// Hint to the ArrayList that it will need to grow to numSamples size:
		List<String> output = new ArrayList<>();
		// TODO: select numSamples * fractionYes words from yesWords; create the rest as no words.
		int select = (int) (numSamples * fractionYes);
		for (int i = 0; i < select; i++) {
			output.add(yesWords.get(i));
		}
		List<String> noWords = new ArrayList<>();
		Random r = new Random();
		for (int i = select; i < numSamples; i++) {
			int random = r.nextInt(0 + yesWords.size());
			String newWord = yesWords.get(random) + "z";
			noWords.add(newWord);
		}
		for (String w : noWords) {
			output.add(w);
		}
		return output;
	}
	
	/**
	 * Load project Gutenberg book to words.
	 * @param filePath try something like "PrideAndPrejudice.txt"
	 * @return a list of words in the book, in order.
	 */
	public static List<String> loadBook(String filePath) {
		long start = System.nanoTime();
		List<String> words = new ArrayList<>();
		try {
			// Read from a file:
			for (String line : Files.readAllLines(new File(filePath).toPath())) {
				words.addAll(WordSplitter.splitTextToWords(line));
			}
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " from book in " + time +" seconds.");
		return words;
	}
	
	public static void main(String[] args) {
		// --- Load the dictionary.
		List<String> listOfWords = loadDictionary();
		
		// --- Create a bunch of data structures for testing:
		
		// TreeSet: input data
		long treeStart = System.nanoTime();
		TreeSet<String> treeOfWords = new TreeSet<>(listOfWords);
		long treeEnd = System.nanoTime();
		double treeTime = (treeEnd - treeStart) / 1e9;
		System.out.println("TreeSet INPUT DATA Insertion Time: "+ treeTime +" seconds");
		
		// TreeSet: for loop
		long treeStart2 = System.nanoTime();
		TreeSet<String> treeOfWords2 = new TreeSet<>();
		for (String w : listOfWords) {
			treeOfWords2.add(w);
		}
		long treeEnd2 = System.nanoTime();
		double treeTime2 = (treeEnd2 - treeStart2) / 1e9;
		System.out.println("TreeSet FOR LOOP Insertion Time: "+ treeTime2 +" seconds");
		
		// HashSet: input data
		long hashStart = System.nanoTime();
		HashSet<String> hashOfWords = new HashSet<>(listOfWords);
		long hashEnd = System.nanoTime();
		double hashTime = (hashEnd - hashStart) / 1e9;
		System.out.println("HashSet INPUT DATA Insertion Time: "+ hashTime + " seconds");
		
		// HashSet: for loop
		long hashStart2 = System.nanoTime();
		HashSet<String> hashOfWords2 = new HashSet<>();
		for (String w : listOfWords) {
			hashOfWords2.add(w);
		}
		long hashEnd2 = System.nanoTime();
		double hashTime2 = (hashEnd2 - hashStart2) / 1e9;
		System.out.println("HashSet FOR LOOP Insertion Time: "+ hashTime2 +" seconds");
		
		// SortedStringListSet
		long stringListStart = System.nanoTime();
		SortedStringListSet bsl = new SortedStringListSet(listOfWords);
		long stringListEnd = System.nanoTime();
		double stringListTime = (stringListEnd - stringListStart) / 1e9;
		System.out.println("SortedStringListSet Insertion Time: "+ stringListTime + " seconds");
		
		// CharTrie
		long trieStart = System.nanoTime();
		CharTrie trie = new CharTrie();
		for (String w : listOfWords) {
			trie.insert(w);
		}
		long trieEnd = System.nanoTime();
		double trieTime = (trieEnd - trieStart) / 1e9;
		System.out.println("CharTrie Insertion Time: "+ trieTime + " seconds");
		
		// LLHash
		long llhashStart = System.nanoTime();
		LLHash hm100k = new LLHash(100000);
		for (String w : listOfWords) {
			hm100k.add(w);
		}
		long llhashEnd = System.nanoTime();
		double llhashTime = (llhashEnd - llhashStart) / 1e9;
		System.out.println("LLHash Insertion Time: "+ llhashTime +" seconds");
		
		// --- Make sure that every word in the dictionary is in the dictionary:
		timeLookup(listOfWords, treeOfWords);
		timeLookup(listOfWords, hashOfWords);
		timeLookup(listOfWords, bsl);
		timeLookup(listOfWords, trie);
		timeLookup(listOfWords, hm100k);
		
		for (int i=0; i<11; i++) {
			// --- Create a dataset of mixed hits and misses with p=i/10.0
			List<String> hitsAndMisses = createMixedDataset(listOfWords, 10_000, i/10.0);
			
			// --- Time the data structures.
			System.out.println("TIMING HITS AND MISSES: "+i+"0%");
			timeLookup(hitsAndMisses, treeOfWords);
			timeLookup(hitsAndMisses, hashOfWords);
			timeLookup(hitsAndMisses, bsl);
			timeLookup(hitsAndMisses, trie);
			timeLookup(hitsAndMisses, hm100k);
		}
		
		List<String> prideAndPrejudice = loadBook("/Users/alexiskilayko/git/CSC212P8/src/main/java/edu/smith/cs/csc212/p8/prideandprejudice.txt");
		System.out.println("PRIDE AND PREJUDICE Misspell Ratio");
		timeLookup(prideAndPrejudice, treeOfWords);
		timeLookup(prideAndPrejudice, hashOfWords);
		timeLookup(prideAndPrejudice, bsl);
		timeLookup(prideAndPrejudice, trie);
		timeLookup(prideAndPrejudice, hm100k);
		
		System.out.println("PRIDE AND PREJUDICE Misspelled Words");
		int stop = 0;
		for (String w : prideAndPrejudice) {
			if (!hashOfWords.contains(w)) {
				System.out.println(w);
				stop++;
			}
			if (stop == 10) {
				break;
			}
		}

		
		// --- linear list timing:
		// Looking up in a list is so slow, we need to sample:
		System.out.println("Start of list: ");
		timeLookup(listOfWords.subList(0, 1000), listOfWords);
		System.out.println("End of list: ");
		timeLookup(listOfWords.subList(listOfWords.size()-100, listOfWords.size()), listOfWords);
		
	
		// --- print statistics about the data structures:
		System.out.println("Count-Nodes: "+trie.countNodes());
		System.out.println("Count-Items: "+hm100k.size());

		System.out.println("Count-Collisions[100k]: "+hm100k.countCollisions());
		System.out.println("Count-Used-Buckets[100k]: "+hm100k.countUsedBuckets());
		System.out.println("Load-Factor[100k]: "+hm100k.countUsedBuckets() / 100000.0);

		
		System.out.println("log_2 of listOfWords.size(): "+listOfWords.size());
		
		System.out.println("Done!");
		
		long start = System.nanoTime();
		List<String> words;
		try {
			// Read from a file:
			words = Files.readAllLines(new File("src/main/resources/words").toPath());
		} catch (IOException e) {
			throw new RuntimeException("Couldn't find dictionary.", e);
		}
		long end = System.nanoTime();
		double time = (end - start) / 1e9;
		System.out.println("Loaded " + words.size() + " entries in " + time +" seconds.");

	}
}

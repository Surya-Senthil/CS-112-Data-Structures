package lse;

import java.io.*;
import java.util.*;
/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 *@author Surya Senthil
 *
 * Structure made by RU-NB-CS112, Methods and Algorithms Created by Surya Senthil
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(docFile))) 
        {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) 
            {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        String file = contentBuilder.toString();
        ArrayList<String> word = new ArrayList<String>();
        Collections.addAll(word, file.split("\n"));
		ArrayList<String> words = new ArrayList<String>();
		for(String a : word) 
			if(!a.isBlank())
				Collections.addAll(words, a.split(" "));	
		HashMap<String,Occurrence> ans = new HashMap<String, Occurrence>();
		for(String curr : words) {
			if(!(getKeyword(curr) == null)) {
				if(ans.containsKey(getKeyword(curr))) {
					Occurrence temp = ans.get(getKeyword(curr));
					temp.frequency += 1;
					ans.put(getKeyword(curr), temp);
				}
				else {
					Occurrence temp = new Occurrence(docFile, 1);
					ans.put(getKeyword(curr), temp);
				}
			}
		}
		return ans;
	}
	

	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {	
		word = word.toLowerCase();
		while(!word.isEmpty() && !Character.isLetter(word.charAt(word.length()-1))) {
			word = word.substring(0,word.length()-1);
		}
		if(word == null || word.isEmpty())
			return null;
		else if(word.matches("^[a-zA-Z]*$") && !noiseWords.contains(word))
			return word;
		else
			return null;	
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		Iterator kwsIterator = kws.entrySet().iterator(); 
		ArrayList<Integer> dum = new ArrayList<Integer>();
        while (kwsIterator.hasNext()) { 									
            Map.Entry mapElement = (Map.Entry)kwsIterator.next(); 
            if(keywordsIndex.containsKey((String)mapElement.getKey())) {
            	keywordsIndex.get((String)mapElement.getKey()).add((Occurrence)mapElement.getValue());
            	dum = insertLastOccurrence(keywordsIndex.get((String)mapElement.getKey()));
			}
			else {
				ArrayList<Occurrence> arr = new ArrayList<>();
				arr.add((Occurrence)mapElement.getValue());
				keywordsIndex.put((String)mapElement.getKey(), arr);
			}
        } 
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		Occurrence target = occs.remove(occs.size()-1);
		int hi = 0, low  = occs.size()-1, mid;
		ArrayList<Integer> dum = new ArrayList<Integer>();
		while(hi <= low) {
			mid = (hi + low) / 2;
			if(occs.get(mid).frequency > target.frequency) 
				hi = mid + 1;		
			else if(occs.get(mid).frequency < target.frequency) 
				low = mid - 1;
			else {
				hi = mid;
				break;
			}
			dum.add(mid);
		}	
		occs.add(hi, target);
		if(dum.isEmpty()) 
			return null;	
		return dum;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<Occurrence> w1 = keywordsIndex.get(kw1), w2 = keywordsIndex.get(kw2);
		ArrayList<String> empty = new ArrayList<String>();
		if(w1 == null && w2 == null)
			return empty;
		else if(w1 == null)
			return returnString(w2);
		else if(w2 == null)
			return returnString(w1);
		for(int i = 0; i < w1.size(); i++) {
			for(int j = 0; j < w2.size(); j++) {
				if(w1.get(i).document.equals(w2.get(j).document)) {
					if(w1.get(i).frequency < w2.get(j).frequency) {
						w1.remove(i);	
						i--;
					}
					else {
						w2.remove(j);
						j--;
					}
					break;
				}
			}
		}
		if(w1.isEmpty())
			return returnString(w2);
		else if(w2.isEmpty())
			return returnString(w1);
		ArrayList<String> ans = new ArrayList<String>();
		int i = 0, j = 0;
		for(int k = 0; k < 5; k++){
			if(i == w1.size() && j == w2.size())
				break;
			else if(i == w1.size()) {
				ans.add(w2.get(j).document);
				j++;
			}
			else if(j == w2.size()) {
				ans.add(w1.get(i).document);
				i++;
			}
			else if(w1.get(i).frequency < w2.get(j).frequency){
				ans.add(w2.get(j).document);
				j++;
			}
			else{
				ans.add(w1.get(i).document);
				i++;
			}
		}
		return ans;
	
	}
	/**
	 * Returns the List of Words without change or the first 5
	 * 
	 * @param ArrayList of Occurrences to Max at 5
	 * @return ArrayList of Strings for Max Occurrences
	 */
	private ArrayList<String> returnString(ArrayList<Occurrence> tar){
		ArrayList<String> ans = new ArrayList<String>();
		for(int i = 0; i < Math.min(5, tar.size()); i++) 
			ans.add(tar.get(i).document);
		return ans;
	}
}

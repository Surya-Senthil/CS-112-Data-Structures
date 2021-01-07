package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Surya Senthil
 *
 * Outline made by Sesh Venugopal, Methods and Algorithms made by Surya Senthil
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		Indexes f = new Indexes(0, (short)0, (short)(allWords[0].length()-1));
		TrieNode first = new TrieNode(f, null, null);
		TrieNode root = new TrieNode(null, first, null);
		for(int i = 1; i < allWords.length; i++) {
			insertion(first, allWords, i, 0);
		}
		return root;
	}
	
	private static void insertion(TrieNode first, String[] allWords, int index, int common) {
		int pref = findPrefix(allWords[index].substring(common), allWords[first.substr.wordIndex].substring(first.substr.startIndex, first.substr.endIndex+1));
		if(pref == 0) {
			if(first.sibling != null)	
				insertion(first.sibling, allWords, index, common);
			else {
				Indexes tempi = new Indexes(index, (short)(common+pref), (short)(allWords[index].length()-1));
				TrieNode tempt = new TrieNode(tempi, null, null);
				first.sibling = tempt;
			}
		} 	
		else if(first.firstChild != null) {
			if(pref <= first.substr.endIndex - first.substr.startIndex) {
				Indexes tempi = new Indexes(first.substr.wordIndex, (short)(common+pref), (short)(first.substr.endIndex));
				TrieNode tempt = new TrieNode(tempi, first.firstChild, null);
				first.substr.startIndex = (short)common;	
				first.substr.endIndex = (short)(pref-1);
				first.firstChild = tempt;					
				insertion(first.firstChild, allWords, index, pref + common);
			}
			else {
				insertion(first.firstChild, allWords, index, pref + common);
			}
		}
		else {
			Indexes tempi = new Indexes(index, (short)(common+pref), (short)(allWords[index].length()-1));
			TrieNode tempt = new TrieNode(tempi, null, null);
			Indexes tempf = new Indexes(first.substr.wordIndex, (short)(pref+common), (short)(allWords[first.substr.wordIndex].length()-1));
			first.firstChild = new TrieNode(tempf, null, tempt);
			first.substr.startIndex = (short)common;
			first.substr.endIndex = (short)(common + pref - 1);
		}
		
	}
	
	private static int findPrefix(String a, String b) {
		int count = a.length() > b.length() ? b.length() : a.length();
		int c = 0;
		for(int i = 0; i < count; i++) { 
			if(a.charAt(i) == b.charAt(i))
				c++;
			else
				break;
		}
		return c;
	}
	
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
		TrieNode ans = findNode(root.firstChild, allWords, prefix);
		ArrayList<TrieNode> list = new ArrayList<TrieNode>();
		if(ans == null)
			return null;
		else if(ans.firstChild == null) {
			list.add(ans);
			return list;
		}
		else
			return findList(ans.firstChild, list, allWords);
	}
	
	private static ArrayList<TrieNode> findList(TrieNode base, ArrayList<TrieNode> list, String[] allWords){	
		if(base.firstChild == null) 
			list.add(base);
		
		if(base.sibling != null) 
			list = findList(base.sibling, list, allWords);
		
		if(base.firstChild != null) 
			list = findList(base.firstChild, list, allWords);
		
		return list;
	}
	
	private static TrieNode findNode(TrieNode first, String[] allWords, String tar){
		int pref = findPrefix(tar, allWords[first.substr.wordIndex].substring(first.substr.startIndex, first.substr.endIndex+1));
		if(pref == 0) 
			return first.sibling != null ? findNode(first.sibling, allWords, tar) : null;
		else if(pref == tar.length()) 
			return first;
		else if(first.firstChild != null)
			return findNode(first.firstChild, allWords, tar.substring(pref));
		else
			return null;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }

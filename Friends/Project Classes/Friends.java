package friends;

import java.util.ArrayList;

import structures.Queue;
import structures.Stack;
/**
 * 
 * @author Surya Senthil
 * 
 * Outline created by RU-NB-CS112, methods and algorithms created by Surya Senthil
 *
 */
public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * Utilizes runChains method implementing the algorithm (Created in 
	 * Separate Method for use in Connectors Method)
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		p1 = p1.toLowerCase();	p2 = p2.toLowerCase();
		if(g == null || !g.map.containsKey(p1) || !g.map.containsKey(p2) || p1.equals(p2))	return null;
		ArrayList<Person> list = new ArrayList<Person>();
		for(Person a : g.members)
			list.add(a);
		ArrayList<ArrayList<String>> table = expand(list, g);	
		return runChains(p1, p2, table);
	}
	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * Utilizes runChains method implementing the algorithm (Created in 
	 * Separate Method for use in Connectors Method)
	 * 
	 * 
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @param people 2D ArrayList of Strings filled with the names of the people and their friends
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	private static ArrayList<String> runChains(String p1, String p2, ArrayList<ArrayList<String>> people){
		int[] visited = new int[people.size()];
		for(int i = 0; i < visited.length; i++)
			visited[i] = -1;
		Queue<Integer> pathways = new Queue<Integer>();
		int start = -1, end = -1;
		for(int i = 0; i < visited.length; i++) {
			if(people.get(i).get(0).equals(p1))
				start = i;
			if(people.get(i).get(0).equals(p2))
				end = i;
		}
		pathways.enqueue(start);
		outerloop:
		while(!pathways.isEmpty()) {
			int curr = pathways.dequeue();
			for(int i = 1; i < people.get(curr).size(); i++) {
				int num = getIndex(people.get(curr).get(i), people);
				if(num == end) { 
					visited[end] = curr;
					break outerloop;
				}
				else if(visited[num] == -1) {
					visited[num] = curr;
					pathways.enqueue(num);
				}
			}
		}

		if(visited[end] == -1) 
			return null;
		
		ArrayList<String> ans = new ArrayList<String>();
		
		while(end != start) {
			ans.add(0, people.get(end).get(0));
			end = visited[end];
		}
		ans.add(0, people.get(start).get(0));
		
		return ans;
	}
	/**
	 * Used to find the index number of any Person from the 2D ArrayList Table
	 * Private Helper Method for the runChains method's algorithm
	 * 
	 * @param tar Name of the person who's position in the members array we want to find
	 * @param list 2D ArrayList of Strings filled with the names of the people and their friends
	 * @return The Index of the target, -1 if not found
	 */
	private static int getIndex(String tar, ArrayList<ArrayList<String>> list) {
		for(int i = 0; i < list.size(); i++) 
			if(list.get(i).get(0).equals(tar))
				return i;
		
		return -1;
	}
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		if(g == null || g.members.length == 0)	return null;
		
		school = school.toLowerCase();
		
		ArrayList<Person> list = new ArrayList<Person>();		
		for(Person a : g.members)
			list.add(a);
				
		for(int i = 0; i < list.size(); i++)
			if(!list.get(i).student || !list.get(i).school.equals(school)) {
				list.remove(i);
				i--;
			}
		
		ArrayList<ArrayList<String>> master = new ArrayList<ArrayList<String>>();
		
		for(Person dude : list) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(dude.name);
			Friend bois = dude.first;
			while(bois != null) {
				if(valid(bois.fnum, list, g)) 
					temp.add(g.members[bois.fnum].name);
				bois = bois.next;
			}
			master.add(temp);
		}
		
		return group(master);		
	}
	/**
	 * Checks if 2 ArrayLists share any values.
	 * 
	 * Private helper method to the Group Method
	 * 
	 * @param arr1 First ArrayList
	 * @param arr2 Second ArrayList
	 * @return Boolean True if the 2 ArrayLists do share one value
	 */
	private static boolean check(ArrayList<String> arr1, ArrayList<String> arr2) {
		for(String a : arr1)
			for(String b : arr2)
				if(a.equals(b))
					return true;
		return false;
	}
	/**
	 * Checks the fnum of the Friend is valid, essentially 
	 * checking if the Friend is part of the Clique
	 * 
	 * Private helper method to the Group Method
	 * 
	 * @param fnum Friend's index in the table
	 * @param list ArrayList of the People in the Clique
	 * @param g Graph, used to convert fnum to the name of the Friend it represents
	 * @return Boolean True if the Friend is part of the Clique
	 */
	private static boolean valid(int fnum, ArrayList<Person> list, Graph g) {
		for(Person a : list) 
			if(a.equals(g.members[fnum]))
				return true; 
		return false;
	}
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		ArrayList<ArrayList<Person>> groups = cut(g);
		ArrayList<ArrayList<ArrayList<String>>> total = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<String> ans = new ArrayList<String>();
		
		for(ArrayList<Person> temp : groups) 
			total.add(expand(temp, g));
		
		for(ArrayList<ArrayList<String>> master : total)
			for(int k = 0; k < master.size(); k++) {
				ArrayList<String> curr = master.remove(k);
				ArrayList<ArrayList<String>> modified = removeCurr(master, curr.get(0));
				outerloop:
				for(int i = 0; i < master.size(); i++)
					for(int j = i+1; j < master.size(); j++) 
						if(runChains(master.get(i).get(0), master.get(j).get(0), modified) == null || runChains(master.get(i).get(0), master.get(j).get(0), modified).isEmpty()) {
							ans.add(curr.get(0));
							break outerloop;
						}
				master.add(k, curr);
			}
		
		
		return ans;
		
	}
	/**
	 * Removes a given name from the 2D ArrayList
	 * 
	 * Private helper method to the Connectors Method
	 * 
	 * @param master 2D array of the names
	 * @param tar Name to remove from the 2D ArrayList
	 * @return 2D ArrayList without the given name
	 */
	private static ArrayList<ArrayList<String>> removeCurr(ArrayList<ArrayList<String>> master, String tar){
		ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < master.size(); i++) {
			ArrayList<String> temp = new ArrayList<String>();
			for(int j = 0; j < master.get(i).size(); j++) 
				if(!master.get(i).get(j).equals(tar)) 
					temp.add(master.get(i).get(j));
			ans.add(temp);
		}
		return ans;
	}
	/**
	 * Expands the ArrayList of People into 2D table of names
	 * 
	 * Private Helper used by Connectors Method
	 * 
	 * @param list The ArrayList of People we need to turn into the 2D ArrayList Table
	 * @param g Graph which provides members array of names, used to convert fnum to names
	 * @return 2D ArrayList Table of Names
	 */
	private static ArrayList<ArrayList<String>> expand(ArrayList<Person> list, Graph g){
		ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();
		for(Person dude : list) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(dude.name);
			Friend curr = dude.first;
			while(curr != null) {
				temp.add(g.members[curr.fnum].name);
				curr = curr.next;
			}
			ans.add(temp);
		}
		
		return ans;
	}
	/**
	 * Groups Rows together if they have similar values
	 * 
	 * Private Helper used by Cliques and Connectors Methods, as well as the private 
	 * cut method
	 * 
	 * @param master 2D ArrayList before removing duplicates
	 * @return Collapsed 2D ArrayList
	 */
	private static ArrayList<ArrayList<String>> group(ArrayList<ArrayList<String>> master){
		boolean done;
		if(master.size() == 1)
			return master;
		do {
			done = true;
			for(int i = 0; i < master.size(); i++) {
				for(int j = i + 1; j < master.size(); j++) {
					if(check(master.get(i), master.get(j))) {
						ArrayList<String> temp = master.remove(i);
						for(String a : master.remove(j-1))
							if(!temp.contains(a))
								temp.add(a);
						master.add(temp);
						i = Math.max(i-=2, 0);
						done = false;
					}
				}
			}
			
		}while(!done);
		return master;
	}
	/**
	 * Turns the Information i n a given Graph into a 2D ArrayList of Persons, A list 
	 * of the lists of people in each island
	 * 
	 * Private helper method used in Connectors Method
	 * 
	 * @param g Graph to create the 2D ArrayList from
	 * @return 2D ArrayList of Persons
	 */
	private static ArrayList<ArrayList<Person>> cut(Graph g){
		ArrayList<ArrayList<String>> master = new ArrayList<ArrayList<String>>();
		
		for(Person dude : g.members) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(dude.name);
			Friend bois = dude.first;
			while(bois != null) {
				temp.add(g.members[bois.fnum].name);
				bois = bois.next;
			}
			master.add(temp);
		}
		
		master = group(master);
		ArrayList<ArrayList<Person>> ans = new ArrayList<ArrayList<Person>>();
		for(ArrayList<String> group : master) {
			ArrayList<Person> temp = new ArrayList<Person>();
			for(String curr : group) {
				for(int i = 0; i < g.members.length; i++) {
					if(g.members[i].name.equals(curr)) {
						temp.add(g.members[i]);
						break;
					}
				}
			}
			ans.add(temp);
		}
		
		return ans;
	}
}


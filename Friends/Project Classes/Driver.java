package friends;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * 
 * @author Surya Senthil
 *
 * Driver to Test the Friends.java Class, fully developed by Surya Senthil
 */
public class Driver {
	/*
	 * Iterates through method options to test Friends
	 */
	public static void main(String[] args) throws FileNotFoundException{
		Scanner enter = new Scanner(System.in);
		System.out.print("Enter File Name: ");
		String file = enter.nextLine();		
		
		System.out.println("\nTable:\n");
		Scanner sc = new Scanner(new File(file));
		Graph graph = new Graph(sc);		
		
		table(graph);
		
		System.out.println("\na. shortestChain	b. cliques	c. connectors	d. QUIT");
		System.out.print("Which method to test: ");
		String ans = enter.nextLine();
		
		while(!ans.toLowerCase().equals("d")) {
			if(ans.toLowerCase().equals("a")) {
				System.out.print("\nEnter Starting Name: ");
				String first = enter.nextLine();
				System.out.print("Enter Ending Name: ");
				String last = enter.nextLine();
				
				ArrayList<String> shortest = Friends.shortestChain(graph, first, last);
				if(shortest == null) 
					System.out.println("Found no link");
				else {
					System.out.println("Shortest Path: ");
					for(int i = 0; i < shortest.size()-1; i++)
						System.out.print(shortest.get(i) + "---");
					System.out.print(shortest.get(shortest.size()-1));
				}
				System.out.println("\n");
			}
			
			if(ans.toLowerCase().equals("b")) {
				System.out.print("\nEnter School: ");
				String school = enter.nextLine();
				
				ArrayList<ArrayList<String>> cliques = Friends.cliques(graph, school);
				if(cliques == null) 
					System.out.println("none");
				else
					System.out.println(cliques.toString());
				System.out.println("\n\n");
			}
			
			if(ans.toLowerCase().equals("c")) {
				System.out.println();
				ArrayList<String> connectors = Friends.connectors(graph);
				if(connectors == null) 
					System.out.println("none");
				else
					System.out.println(connectors.toString());
				System.out.println("\n\n");
			}
			if(ans.toLowerCase().equals("e")) {	
				if(graph == null) 
					System.out.println("Table Empty");
				else
					table(graph);
					
				System.out.println();
			}
			
			System.out.println("a. shortestChain	b. cliques	c. connectors	d. QUIT	e. Print Table");
			System.out.print("Which method to test: ");
			ans = enter.nextLine();
		}
		enter.close();
	}
	/*
	 * Prints the Current Graph
	 */
	private static void table(Graph graph) {
		for(Person p : graph.members) {
			System.out.print(p.name + "	");
			if(p.student)
				System.out.print(" at " + p.school + "	");
			else
				System.out.print("		");
			System.out.print(" with Friends:	");
			Friend first = p.first;
			while(first != null) {
				System.out.print(graph.members[first.fnum].name + "  ");
				first = first.next;
			}
			System.out.println();
		}
	}

}

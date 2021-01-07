package poly;

import java.io.IOException;
import java.util.Scanner;

import javax.naming.ldap.StartTlsRequest;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author Surya Senthil
 *
 * Outline made by RU-NB-CS112, Methods and Algorithms Implemented by Surya Senthil
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		Node start = null, end = null;
		while(poly1 != null && poly2 != null) {
			if(poly1.term.degree < poly2.term.degree) {
				if(end == null) {
					start = new Node(poly1.term.coeff, poly1.term.degree, null);
					end = start; 
					poly1 = poly1.next; 
				}
				else {
					end.next = new Node(poly1.term.coeff, poly1.term.degree, null);
					end = end.next;
					poly1 = poly1.next;
				}
			}
			else if(poly1.term.degree > poly2.term.degree) {
				if(end == null) {
					start = new Node(poly2.term.coeff, poly2.term.degree, null);
					end = start; 
					poly2 = poly2.next; 
				}
				else {
					end.next = new Node(poly2.term.coeff, poly2.term.degree, null);
					end = end.next;
					poly2 = poly2.next;
				}
			}
			else {
				if(end == null && poly1.term.coeff + poly2.term.coeff != 0) {
					start = new Node(poly1.term.coeff + poly2.term.coeff, poly1.term.degree, null);
					end = start; 
					poly1 = poly1.next; 
					poly2 = poly2.next;
				}
				else if(poly1.term.coeff + poly2.term.coeff != 0) {
					end.next = new Node(poly1.term.coeff + poly2.term.coeff, poly1.term.degree, null);
					end = end.next;
					poly1 = poly1.next;
					poly2 = poly2.next;
				}
				else {
					poly1 = poly1.next;
					poly2 = poly2.next;
				}				
			}
				
		}
		
		if(poly1 == null && poly2 != null) {
			if(end == null) { 
				return clone(poly2);
			}
			while(poly2 != null) {
				end.next = new Node(poly2.term.coeff, poly2.term.degree, null);
				end = end.next;
				poly2 = poly2.next;
			}
		} 
		else if(poly2 == null && poly1 != null) {
			if(end == null) { 
				return clone(poly1);
			}
			while(poly1 != null) {
				end.next = new Node(poly1.term.coeff, poly1.term.degree, null);
				end = end.next;
				poly1 = poly1.next;
			}
		}
		
		return start;
	}
	/**
	 * Clones Node to not change original variable
	 * 
	 * @param Node to be cloned
	 * @return Cloned Node
	 */
	private static Node clone(Node poly) {
		Node head = null, end = null;
		while(poly != null) {
			if(head == null) {
				head = new Node(poly.term.coeff, poly.term.degree, null);
				end = head;
			}
			else {
				end.next = new Node(poly.term.coeff, poly.term.degree, null);
				end = end.next;
			}
			poly = poly.next;
		}
		return head;
	}
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		if (poly1 == null || poly2 == null) 
			return null;
		
		Node start1 = null, end1  = null, poly2copy = poly2;
		
		while(poly1 != null) {	
			while(poly2 != null) {
				if(start1 == null) {
					start1 = new Node(poly1.term.coeff * poly2.term.coeff, poly1.term.degree + poly2.term.degree, null);
					end1 = start1;
				}
				else {
					end1.next = new Node(poly1.term.coeff * poly2.term.coeff, poly1.term.degree + poly2.term.degree, null);
					end1 = end1.next;
				}
				poly2 = poly2.next;
			}
			poly2 = poly2copy;
			poly1 = poly1.next;
		}

		return sortnMerge(start1);
	}
	
	/**
	 * Sorts Polynomial by Degree and Combines Coefficients of Same-Degree Terms
	 * 
	 * @param The head of the unsorted polynomial linked list
	 * @return The head of the sorted polynomial linked list
	 */
	private static Node sortnMerge(Node head) {
		Node first = head, second = head.next;
		float tempc = 0;
		int tempd = 0, count = 0;
		while(second != null) {
			
			if(first.term.degree == second.term.degree) {
				first.term.coeff += second.term.coeff;
				first.next = second.next;
				second = second.next;
				count++;
			}
			else if(first.term.degree > second.term.degree) {
				tempc = second.term.coeff;
				tempd = second.term.degree;
				second.term.coeff = first.term.coeff;
				second.term.degree = first.term.degree;
				first.term.coeff = tempc;
				first.term.degree = tempd;
				count++;
				first = first.next;
				second = second.next;
			}
			else {
				first = first.next;
				second = second.next;
			}
			
		}
		first = head;
		second = head.next;
		while(second != null) {
			if(first.term.coeff == 0) {
				first = second;
				second = second.next;
				head = first;
			}
			else if(second.term.coeff == 0){
				first.next = second.next;
				second = second.next;
			}
			else {
				first = second;
				second = second.next;
			}
		}
		if(count == 0)
			return head;
		else
			return sortnMerge(head);
		
	}
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		float sum = 0;	
		for(Node i = poly; i != null; i = i.next) {
			sum += i.term.coeff * Math.pow(x, i.term.degree);
		}
		return sum;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}

package com.infosys.mp.practice;
import java.util.Scanner;

//Check for traffic light color
public class SwitchStatement {
	public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char lightColor = scanner.next().charAt(0);
        performAction(lightColor);
        scanner.close();
    }
    
	 public static void performAction(char lightColor) {
	        // Write your code here
	        switch(lightColor){
	            case 'R':
	            System.out.println("Stop");
	            break;
	            case 'Y':
	            System.out.println("Wait");
	            break;
	            case 'G':
	            System.out.println("Go");
	            break;
	            default: 
	            	System.out.println("Invalid option");
	            	break;
	        }
	        
	    }

}

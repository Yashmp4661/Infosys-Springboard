package com.infosys.mp.practice;
import java.util.Scanner;

public class IfLoop {
	
	 public static void checkMultipleOfFive(int number) {
	        // Write your code here
	        if(number%5 == 0)
	        System.out.println("Multiple of 5");
	        else 
	        	 System.out.println("Not a multiple of 5");
	        System.out.println("Program ended");
	    }
	 
	 public static void main(String[] args) {
	        Scanner scanner = new Scanner(System.in);
	        int number = scanner.nextInt();
	        checkMultipleOfFive(number);
	    }

}

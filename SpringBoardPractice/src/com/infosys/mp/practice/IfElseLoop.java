package com.infosys.mp.practice;

import java.util.Scanner;

public class IfElseLoop {
	 public static void main(String[] args) {
	        Scanner scanner = new Scanner(System.in);
	        int number = scanner.nextInt();
	        checkOddEven(number);
	    }
	    
	    public static void checkOddEven(int number) {
	        // Write your code here
	        if(number%2 == 0){
	            System.out.println("Even");
	        }
	        else{
	            System.out.println("Odd");
	        }
	    }
}

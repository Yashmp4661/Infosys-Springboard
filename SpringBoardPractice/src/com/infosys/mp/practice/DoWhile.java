package com.infosys.mp.practice;

public class DoWhile {

	 public static void main(String[] args) {
	        int number = 5;
	        int multiplier = 1;
	        do {
	            System.out.println(number + " * " + multiplier + " = " + (number * multiplier));
	            multiplier++;
	        } while (multiplier <= 10);   
	    }
}

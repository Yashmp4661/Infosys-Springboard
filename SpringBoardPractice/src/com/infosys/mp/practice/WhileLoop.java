package com.infosys.mp.practice;

public class WhileLoop {
	public static void main(String[] args) {
        int number = 5;
        int factorial = 1;
        while (number > 0) {
            factorial *= number;
            number--;
        }
        System.out.println("Factorial: " + factorial);
    }

}

package com.infosys.mp.practice;
import java.util.Scanner;

//Check a number is positive, zero or negative
public class ElseIfLadder {
	public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        // write your code here
        if(number==0){
            System.out.println("Zero");
        }
        else if(number>0){
            System.out.println("Positive");
        }
        else if(number<0){
            System.out.println("Negative");
        }
    }

}

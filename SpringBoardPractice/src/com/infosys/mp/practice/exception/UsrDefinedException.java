package com.infosys.mp.practice.exception;

import java.util.Scanner;

public class UsrDefinedException {
	private static int age;
	static void validate(int age) throws Age{
	//	Scanner sc=new Scanner(System.in);
		//System.out.println("enter ur age");
		//age = sc.nextInt();
		if(age<18) {
			throw new Age("invalid age");
		}
		else {
			System.out.println("votes");
		}
		
	}

}

package com.infosys.mp.practice.exception;


public class Demo {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			UsrDefinedException.validate(6);
		}
		catch(Age e) {
			System.out.println("caught"+e.getMessage());
		}
		

	}


}

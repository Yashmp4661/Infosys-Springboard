package com.infosys.mp.practice.exception;

public class ExceptionExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			int d=0;
			try {
				int a=42/d;
				System.out.println("mp");
				
			}
			catch(ArithmeticException e){
				System.out.println("Exception cannot divide"+e.getLocalizedMessage());
				
			}
			
		

	}

}
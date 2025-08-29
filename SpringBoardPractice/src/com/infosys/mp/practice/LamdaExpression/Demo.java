package com.infosys.mp.practice.LamdaExpression;


public class Demo {
	public static void main(String[] args) {
	
		Cube c=(a)->{return a*a*a;};
		System.out.println(c.cal(7));
		

	}

}

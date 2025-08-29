package com.infosys.mp.practice.Generics;



public class Demo {
	public static void main(String[] args) { 
		Generic <String>stringobj=new Generic();
		stringobj.setData("hello");
		System.out.println(stringobj.getData());
		
		Generic <Integer>obj=new Generic();
		obj.setData(22);
		System.out.println(+obj.getData());
		
		Generic <Double> doubleobj=new Generic();
		doubleobj.setData(22.7);
		System.out.println(+doubleobj.getData());
		}

}

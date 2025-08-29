package com.infosys.mp.practice.comparator;

import java.util.ArrayList;
import java.util.Collections;



public class Demo {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Student>ar=new ArrayList<Student>();
		ar.add(new Student(11,"a","lon"));
		ar.add(new Student(10,"c","usa"));
		ar.add(new Student(12,"b","par"));
		System.out.println("---");
		Collections.sort(ar,new Sortroll());
		for(int i=0;i<ar.size() ;i++) {
			System.out.println(ar.get(i));
			
		}
			System.out.println("---");
			Collections.sort(ar,new Sortname());
			for(int j=0;j<ar.size() ;j++) {
				System.out.println(ar.get(j));
		}

	}

}

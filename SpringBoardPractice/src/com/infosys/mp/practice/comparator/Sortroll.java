package com.infosys.mp.practice.comparator;

import java.util.Comparator;

public class Sortroll implements Comparator<Student>{

	@Override
	public int compare(Student a, Student b) {
		// TODO Auto-generated method stub
		return a.rollno-b.rollno;
	}

}
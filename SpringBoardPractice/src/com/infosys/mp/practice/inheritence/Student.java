package com.infosys.mp.practice.inheritence;

public class Student extends Customer{
	private String collegeName;
	private int rollno;
	public Student() {
		super();
	}
	public Student(String name, int id, long adhaarno, long phoneno,String collegeName,int rollno) {
		super(name, id, adhaarno, phoneno);
		this.collegeName=collegeName;
		this.rollno=rollno;
		// TODO Auto-generated constructor stub
	}
	public String getCollegeName() {
		return collegeName;
	}
	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}
	public int getRollno() {
		return rollno;
	}
	public void setRollno(int rollno) {
		this.rollno = rollno;
	}
	@Override
	public String toString() {
		return "Student [collegeName=" + collegeName + ", rollno=" + rollno + ", getName()=" + getName() + ", getId()="
				+ getId() + ", getAdhaarno()=" + getAdhaarno() + ", getPhoneno()=" + getPhoneno() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}
	
}

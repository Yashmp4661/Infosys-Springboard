package com.infosys.mp.practice.inheritence;

public class Customer {
	private String name;
	private int id;
	private long adhaarno;
	private long phoneno;
	//default constructor
	public Customer() {
		super();
	}
	public Customer(String name, int id, long adhaarno, long phoneno) {
		super();
		this.name = name;
		this.id = id;
		this.adhaarno = adhaarno;
		this.phoneno = phoneno;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getAdhaarno() {
		return adhaarno;
	}
	public void setAdhaarno(long adhaarno) {
		this.adhaarno = adhaarno;
	}
	public long getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(long phoneno) {
		this.phoneno = phoneno;
	}
	@Override
	public String toString() {
		return "Customer [name=" + name + ", id=" + id + ", adhaarno=" + adhaarno + ", phoneno=" + phoneno + "]";
	}
}

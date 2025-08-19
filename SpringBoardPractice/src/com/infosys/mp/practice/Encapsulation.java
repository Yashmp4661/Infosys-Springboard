package com.infosys.mp.practice;

public class Encapsulation {
	private int serialNum=10;
	private String name;
	private int age;
	
	void show()
	{
		System.out.println("serialnumber"+serialNum+"\t Name"+name+"\t age" +age);
	}

	public int getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(int serialNum) {
		this.serialNum = serialNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Encapsulation [serialNum=" + serialNum + ", name=" + name + ", age=" + age + "]";
	}
	
	

}

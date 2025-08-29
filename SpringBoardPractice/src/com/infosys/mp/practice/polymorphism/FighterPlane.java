package com.infosys.mp.practice.polymorphism;



public class FighterPlane extends Plane{
	public void takeoff() {
		System.out.println("The Fighter plane is taking off");
		
	}
	public void fly() {
		System.out.println("The  Fighter plane is Flying");
	}
	public void land() {
		System.out.println("The Fighter plane is landing");
	}
}


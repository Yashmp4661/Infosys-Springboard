package com.infosys.mp.practice.polymorphism;



public class PassengerPlane extends Plane {
	public void takeoff() {
		System.out.println("The Passenger plane is taking off");
		
	}
	public void fly() {
		System.out.println("The Passenger plane is Flying");
	}
	public void land() {
		System.out.println("The Passenger plane is landing");
	}
}



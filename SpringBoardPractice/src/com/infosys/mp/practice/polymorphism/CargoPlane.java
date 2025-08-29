package com.infosys.mp.practice.polymorphism;



public class CargoPlane extends Plane {

	public void takeoff() {
		System.out.println("The Cargo plane is taking off");
		
	}
	public void fly() {
		System.out.println("The Cargp plane is Flying");
	}
	public void land() {
		System.out.println("The Cargo plane is landing");
	}
	public void flew() {
		System.out.println("The Cargo plane flew");
	}
}

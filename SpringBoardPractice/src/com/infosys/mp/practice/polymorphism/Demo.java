package com.infosys.mp.practice.polymorphism;



public class Demo {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Airport.display(new CargoPlane());
		Airport.display(new PassengerPlane());
		Airport.display(new FighterPlane());
		

	}
}

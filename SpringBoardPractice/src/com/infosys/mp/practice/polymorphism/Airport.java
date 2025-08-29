package com.infosys.mp.practice.polymorphism;



public class Airport {
	public static void display(Plane p) {

		p.takeoff();
		p.fly();
		p.land();
		
		
		if(p instanceof CargoPlane) {
			((CargoPlane)p).flew();
		}
	
	}

}

package com.infosys.mp.practice.threads;



public class Firstway extends Thread{
	public void run() {
		System.out.println("Creation of thread by extending thread class");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       Firstway ob=new Firstway();
       
       ob.start();//runnable
       ob.stop();
       
	}

}

package com.infosys.mp.practice.threads;



public class Secondway implements Runnable {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Secondway ob=new Secondway();
		Thread th=new Thread(ob);
		th.start();
	
		th.run();
		th.stop();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Creation of thread by extending interface");
		
	}

}


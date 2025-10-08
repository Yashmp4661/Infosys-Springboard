package com.infosys.mp.practice.slidingWindow;



public class Linear {
	
	public int linearSearch(int[] arr,int x) {
		int n = arr.length;
		for(int i=0;i<n;i++) {
			if(arr[i] == x) {
				return i;
			}
			
		}
		return -1;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Linear s = new Linear();
		System.out.println(s.linearSearch(new int[] {1,5,8,5,6}, 5));
		
	}

}

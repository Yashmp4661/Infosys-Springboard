package com.infosys.mp.practice.slidingWindow;



public class BinarySearch {

	public int binarySearch(int[] arr, int x) {
		int high = arr.length-1;
		int low = 0;
		while(low<=high) {
			int mid = (high + low)/2;
			if(arr[mid]==x) {
				return mid;
			}
			if(x<arr[mid]) {
				high = mid-1;
			} else {
				low = mid+1;
			}
		}
		return -1;
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		BinarySearch s = new BinarySearch();
	
		System.out.println(s.binarySearch(new int[] {1,2,3,4,5,6}, 5));
		

	}
}

package com.infosys.mp.practice.slidingWindow;



public class InsertionSort {
	
	//insertion sort
		public void insertionSort(int[] arr) {
			int n = arr.length;
			for(int i=1;i<n;i++) {
				int temp = arr[i];
				int j = i-1;
				while(j>=0 && arr[j]>temp) {
					arr[j+1] = arr[j];
					j = j-1;
				}
				arr[j+1] = temp;
			}
					
		}
		
		//insertion sort descending
			public void insertionSort1(int[] arr) {
				int n = arr.length;
				for(int i=1;i<n;i++) {
					int temp = arr[i];
					int j = i-1;
					while(j>=0 && arr[j]<temp) {
						arr[j+1] = arr[j];
						j = j-1;
					}
					arr[j+1] = temp;
				}
			}
						
				public void printArray(int[] array) {
					for(int num : array) {
						System.out.print(num+" ");
					}
					System.out.println();
				}
			
			public static void main(String[] args) {
				int[] arr = {5,4,4,8,10,12};
				int[] temp = new int[arr.length];
				InsertionSort s = new InsertionSort();
				s.printArray(arr);
				s.insertionSort(arr);
				s.printArray(arr);
				s.insertionSort1(arr);
				s.printArray(arr);

}
}

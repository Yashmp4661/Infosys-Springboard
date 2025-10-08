package com.infosys.mp.practice.slidingWindow;



public class SelectionSort {
	
	//Selection Sort
			public void selectionSort(int[] arr) {
				int n = arr.length;
				for(int i=0;i<n-1;i++) {
					int min = i;
					
					for(int j =i+ 1;j<n;j++) {
						if(arr[j]<arr[min]) {
							min = j;
							
						}
						int temp = arr[min];
						arr[min] = arr[i];
						arr[i] = temp;
					}
					
				}
			}
			
			//Selection Sort descending
					public void selectionSort1(int[] arr) {
						int n = arr.length;
						for(int i=0;i<n-1;i++) {
							int max = i;
							
							for(int j =i+ 1;j<n;j++) {
								if(arr[j]>arr[max]) {
									max = j;
									
								}
								int temp = arr[max];
								arr[max] = arr[i];
								arr[i] = temp;
							}
							
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
		SelectionSort s = new SelectionSort();
		s.printArray(arr);
		s.selectionSort(arr);
		s.printArray(arr);
		s.selectionSort1(arr);
		s.printArray(arr);

	}

}

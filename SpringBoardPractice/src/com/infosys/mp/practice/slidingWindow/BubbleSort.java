package com.infosys.mp.practice.slidingWindow;



public class BubbleSort {

		
		public void printArray(int[] array) {
			for(int num : array) {
				System.out.print(num+" ");
			}
			System.out.println();
		}
		
		//increasing order
		public void bubbleSort(int[] arr) {
			int n = arr.length;
			boolean isSwapped;
			for(int i = 0;i < n-1;i++) {
				isSwapped = false;
				for(int j = 0;j<n-1-i;j++) {
					if(arr[j]>arr[j+1]) {
						int temp = arr[j];
						arr[j] = arr[j+1];
						arr[j+1] = temp;
						isSwapped = true;
					}
				}
				if(!isSwapped)
					return;
			}
		}
		//decreasing order
		public void bubbleSort1(int[] arr) {
			int n = arr.length;
			boolean isSwapped;
			for(int i = 0;i < n-1;i++) {
				isSwapped = false;
				for(int j = 0;j<n-1-i;j++) {
					if(arr[j]<arr[j+1]) {
						int temp = arr[j];
						arr[j] = arr[j+1];
						arr[j+1] = temp;
						isSwapped = true;
					}
				}
				if(!isSwapped)
					return;
			}
		
		}

		public static void main(String[] args) {
			int[] arr = {5,4,4,8,10,12};
			int[] temp = new int[arr.length];
			BubbleSort s = new BubbleSort();
			s.printArray(arr);
			s.bubbleSort(arr);
			s.printArray(arr);
			s.bubbleSort1(arr);
			s.printArray(arr);
}
		}
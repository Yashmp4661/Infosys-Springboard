package com.infosys.mp.practice.slidingWindow;



import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SlidingWindow {
	//Find maximum of the window k
	public static List<Integer> maxSlidingWindow(int[] arr, int k){
		List<Integer> res = new ArrayList<>();
		int[] ngeArr = nextGreaterElement(arr);
		for(int i=0;i<=arr.length-k;i++) {
			int j=i;
			while(ngeArr[j]<i+k) {
				j = ngeArr[j];
			}
		res.add(arr[j]);
		}
		return res;
	}
	
	public static int[] nextGreaterElement(int[] arr) {
		int[] result = new int[arr.length];
		Stack<Integer> s = new Stack<>();
		for(int i=arr.length-1;i>=0;i--) {
			if(!s.isEmpty()) {
				while(!s.isEmpty() && arr[s.peek()]<=arr[i]) {
					s.pop();
				}
			}
			if(s.isEmpty()) {
				result[i] = arr.length; 
			} else {
				result[i] = s.peek();
			}
			s.push(i);
		}
		return result;
		
	}
	
	//find maximum sum of the subarray of window size k
	public static int maxSubArraySum(int[] arr,int k) {
		int maxSum=0;
		int windowSum = 0;
		int start = 0;
		for(int end=0;end<arr.length;end++) {
			windowSum  += arr[end];
			if(end>=k-1) {
				maxSum = Math.max(maxSum,windowSum);
				windowSum -= arr[start];
				start++;
			}
		}
		return maxSum;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Integer> res = maxSlidingWindow(new int[] {44,77,33,44,88,11},3);
		for(int n:res) {
			System.out.print(n+" ");
		}
		System.out.println();
			System.out.println(maxSubArraySum(new int[] {44,77,33,44,88,11},3));
	}
	

}

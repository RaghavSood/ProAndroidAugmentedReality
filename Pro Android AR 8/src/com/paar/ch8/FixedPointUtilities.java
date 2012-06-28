package com.paar.ch8;

public class FixedPointUtilities {
	public static final int ONE = 0x10000;
	

	public static int toFixed(float val) {
		return (int)(val * 65536F);
	}


	public static int[] toFixed(float[] arr) {
		int[] res = new int[arr.length];
		toFixed(arr, res);
		return res;
	}
	

	public static void toFixed(float[] arr, int[] storage)
	{
		for (int i=0;i<storage.length;i++) {
			storage[i] = toFixed(arr[i]);
		}
	}
	

	public static float toFloat(int val) {
		return ((float)val)/65536.0f;
	}
	

	public static float[] toFloat(int[] arr) {
		float[] res = new float[arr.length];
		toFloat(arr, res);
		return res;
	}
	

	public static void toFloat(int[] arr, float[] storage)
	{
		for (int i=0;i<storage.length;i++) {
			storage[i] = toFloat(arr[i]);
		}
	}
	

	public static int multiply (int x, int y) {
		long z = (long) x * (long) y;
		return ((int) (z >> 16));
	}


	public static int divide (int x, int y) {
		long z = (((long) x) << 32);
		return (int) ((z / y) >> 16);
	}	
	

	 public static int sqrt (int n) {
		int s = (n + 65536) >> 1;
		for (int i = 0; i < 8; i++) {
			s = (s + divide(n, s)) >> 1;
		}
		return s;
	 }
}
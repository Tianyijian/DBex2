package bPlusTree2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Test {

	public static void main(String[] args) {
//		test2();
//		test3();
		test4();
	}
	
	public static void test2() {
		int[] a = new int[] { 2, 7, 13, 3, 11, 23, 31, 47, 43, 5, 19, 37, 17, 41, 29};
		BPlusTree<Integer, Integer> b = new BPlusTree<>(3);
		for (int i= 0; i < a.length;i++) {
			b.insert(a[i], i);
		}
		for (int i= 0; i < a.length;i++) {
			System.out.println(b.find(a[i]));
		}
		System.out.println("\n\nPrint tree: ");
		b.print();
	}
	
	public static void test3() {
		BPlusTree<Integer, String> b = new BPlusTree<>(4);
		int[] a = new int[100];
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/main/testData.txt")));
			String line = "";
			int i = 0;
			while((line = br.readLine()) != null) {
				String[] str = line.split(" ");
//				System.out.println(str[0]);
//				System.out.println(str[1]);
				a[i] = Integer.valueOf(str[0]);
//				System.out.println(a[i]);
				b.insert(a[i], str[1]);
				i++;
			}
			br.close();
			System.out.println(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("\n\nPrint tree: ");
		b.print();
		for (int i = 0; i < a.length; i++) {
			String  r = b.find(a[i]);
			System.out.println(r);
		}
	}
	
	public static void test4() {
		BPlusTree<Integer, String> b = new BPlusTree<>(8);
		int[] a = new int[1000000];
		try {
			long start = System.currentTimeMillis();
			BufferedReader br = new BufferedReader(new FileReader(new File("src/main/data.txt")));
			String line = "";
			int i = 0;
			while((line = br.readLine()) != null) {
				String[] str = line.split(" ");
				a[i] = Integer.valueOf(str[0]);
				b.insert(a[i], str[1]);
				i++;
			}
	        System.out.printf("BPlusTree insert time: %s ms \n" , (System.currentTimeMillis() - start));
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		System.out.println("\n\nPrint tree: ");
//		b.print();
//		for (int i = 0; i < 100; i++) {
//			String  r = b.find(a[i]);
//			System.out.println(r);
//		}
		System.out.println(b.find(6291556));	//000003
		System.out.println(b.find(56));		//000034
		System.out.println(b.find(2097159));	//000022
	}
}

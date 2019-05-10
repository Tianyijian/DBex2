package bPlusTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Test {
	public static void main(String[] args) {
//		test2();
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		test3();
//		test4();
	}

	public static void test2() {
		int[] a = new int[] { 10, 17, 3, 29, 4, 5, 18, 6, 22, 1, 33, 35, 15, 16, 14};
		BPlusTree<Integer, Integer> b = new BPlusTree<>(4);
		for (int i= 0; i < a.length;i++) {
			b.insert(i, a[i]);
		}
		for (int i= 0; i < a.length;i++) {
			System.out.println(b.find(a[i]));
		}
		b.print();
	}
	
	public static void test3() {
		BPlusTree<String, Integer> b = new BPlusTree<>(4);
		int[] a = new int[1000000];
		try {
//			BufferedReader br = new BufferedReader(new FileReader(new File("src/main/testData.txt")));
			BufferedReader br = new BufferedReader(new FileReader(new File("src/main/data.txt")));
			String line = "";
			int i = 0;
			while((line = br.readLine()) != null) {
				String[] str = line.split(" ");
//				System.out.println(str[0]);
//				System.out.println(str[1]);
				a[i] = Integer.valueOf(str[0]);
				System.out.println(a[i]);
				if (str[1].equals("000147")) {
					b.print();
					System.out.println("------------");
				}
				b.insert(str[1], a[i]);
				i++;
			}
			br.close();
			System.out.println(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		b.print();
//		for (int i = 0; i < a.length; i++) {
//			String  r = b.find(a[i]);
//			System.out.println(r);
//		}
	}
	
	public static void test4() {
		System.out.println(Double.valueOf(Math.ceil(5 / 2.0)).intValue());
	}
}

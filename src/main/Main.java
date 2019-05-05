package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashSet;


public class Main {
	public static void main(String[] args) {
		genData("src/main/data.txt");
//		genTestData("src/main/testData.txt");
	}
	
	public static void genData(String path) {
		long begin = System.currentTimeMillis();
		SecureRandom random = new SecureRandom();
		HashSet<Integer> set = new HashSet<Integer>();
		while(set.size() < 1000000) {
			set.add(random.nextInt(10000000));
		}
		StringBuilder res = new StringBuilder();
		int i = 1;
		for (int A : set) {
			String B = String.format("%06d", i);
			res.append(A +" " + B + "\n");
			i++;
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(path));
			BufferedOutputStream bout = new BufferedOutputStream(out);
			bout.write(res.toString().getBytes());
			bout.flush();
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		System.out.println("Use Time: " + (end - begin) / 1000.0);
	}
	
	public static void genTestData(String path) {
		StringBuilder res = new StringBuilder();
		SecureRandom random = new SecureRandom();
		for (int i = 0; i<100;i++) {
			int A = random.nextInt(1000);
			String B = String.format("%03d", i);
			res.append(A +" " + B + "\n");
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(path));
			BufferedOutputStream bout = new BufferedOutputStream(out);
			bout.write(res.toString().getBytes());
			bout.flush();
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

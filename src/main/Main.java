package main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class Main {
	public static void main(String[] args) {
		genData("src/main/data.txt");
	}
	
	public static void genData(String path) {
		long begin = System.currentTimeMillis();

		StringBuilder res = new StringBuilder();
		SecureRandom random = new SecureRandom();
		for (int i = 0; i<1000000;i++) {
			int A = random.nextInt(1000000);
			String B = String.format("%06d", i);
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
		long end = System.currentTimeMillis();
		System.out.println("Use Time: " + (end - begin) / 1000.0);
	}
}

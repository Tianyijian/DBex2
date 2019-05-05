package sort;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Sort {

	private final static int RECORD_NUM = 1000000;
	private final static int BLOCK_SIZE = 250000;
	private final static int LIST_NUMBER = RECORD_NUM / BLOCK_SIZE;
	private static int[] inputBuffer = new int[LIST_NUMBER];
	private static int[] outputBuffer = new int[BLOCK_SIZE];

	public static void main(String[] args) {
		phrase1();
		try {
			phase2();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 重复四次，每次1MB的记录
	 */
	public static void phrase1() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/main/data.txt")));
			long start = System.currentTimeMillis();
			for (int k = 0; k < LIST_NUMBER; k++) {
				long time1 = System.currentTimeMillis();
				// 读入内存
				for (int i = 0; i < BLOCK_SIZE; i++) {
					String line = br.readLine();
					String[] str = line.split(" ");
					outputBuffer[i] = Integer.valueOf(str[0]);
				}
				// 进行排序
				long time2 = System.currentTimeMillis();
				Arrays.sort(outputBuffer);
				long time3 = System.currentTimeMillis();
				// 输出到文件
				writeFile(getFileName(k));
				long time4 = System.currentTimeMillis();
				System.out.printf("Block %d, read %d ms, sort %d ms, write %d ms\n", k, time2 - time1, time3 - time2,
						time4 - time3);
			}
			long end = System.currentTimeMillis();
			System.out.printf("Total time: %d ms\n", end - start);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String getFileName(int k) {
		return "src/sort/sorted" + k + ".txt";
	}

	public static void phase2() throws IOException {
		String outFile = "src/sort/result.txt";
		BufferedReader[] br = new BufferedReader[LIST_NUMBER];
		int i = 0;
		int total = 0; // 总共写入的记录数
		int count = 0; // 输出缓冲区已写入的记录数
		long start = System.currentTimeMillis(); // 计时
		// 初始化bufferReader 以及输入缓冲区
		for (int k = 0; k < LIST_NUMBER; k++) {
			br[k] = new BufferedReader(new FileReader(new File(getFileName(k))));
			inputBuffer[k] = Integer.valueOf(br[k].readLine());
		}
		while (total < RECORD_NUM) {
			// 选择输入缓冲区中最小的填充到输出缓冲区
			int index = minInBuffer();
			outputBuffer[count] = inputBuffer[index];
			count++;
			total++;
			// 输出缓冲区已满, 写到文件
			if (count >= BLOCK_SIZE) {
				count = 0;
				writeFile(outFile);
			}
			// 从外部文件继续读取一个记录到缓冲区
			String line = "";
			if ((line = br[index].readLine()) != null) {
				inputBuffer[index] = Integer.valueOf(line);
			} else {
				// 该文件读完,从其它外部文件读入
				for (i = 0; i < LIST_NUMBER; i++) {
					if ((line = br[i].readLine()) != null) {
						inputBuffer[index] = Integer.valueOf(line);
						break;
					}
				}
			}
			// 判断是否外部子文件都被读完
			if (i >= LIST_NUMBER) { // 此时仅剩输入缓冲区中的记录,直接排序，写到文件
				Arrays.sort(inputBuffer);
				for (int j = 1; j < LIST_NUMBER; j++) {
					outputBuffer[BLOCK_SIZE - LIST_NUMBER + j] = inputBuffer[j];
					count++;
					total++;
				}
				writeFile(outFile);
			}
		}
		// 关闭bufferReader
		for (int k = 0; k < LIST_NUMBER; k++) {
			br[k].close();
		}
		long end = System.currentTimeMillis();
		System.out.printf("Merge time: %d ms\n", end - start);
	}

	/**
	 * 找到输入缓冲区中的最小记录的下标
	 * 
	 * @return
	 */
	private static int minInBuffer() {
		int min = inputBuffer[0];
		int minIndex = 0;
		for (int k = 1; k < LIST_NUMBER; k++) {
			if (inputBuffer[k] < min) {
				min = inputBuffer[k];
				minIndex = k;
			}
		}
		return minIndex;
	}

	/**
	 * 将输出缓冲区写入指定路径文件
	 * 
	 * @param path
	 */
	private static void writeFile(String path) {
		try {
			FileOutputStream out = new FileOutputStream(new File(path), true);
			BufferedOutputStream bout = new BufferedOutputStream(out);
			for (int j = 0; j < BLOCK_SIZE; j++) {
				bout.write((outputBuffer[j] + "\n").getBytes());
			}
			bout.flush();
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

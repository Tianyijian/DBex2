package sort;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import main.Record;

public class Sort {

	private final static int RECORD_NUM = 1000000;
	private final static int BLOCK_SIZE = 62500;
//	private final static int BLOCK_SIZE = 31250;
	private final static int LIST_NUMBER = RECORD_NUM / BLOCK_SIZE; // 16
	private static Record[] inputBuffer = new Record[LIST_NUMBER];
	private static Record[] outputBuffer = new Record[BLOCK_SIZE];

	public static void main(String[] args) throws IOException {
		split();
		merge();
//		testQuickSort();
	}

	/**
	 * 将大文件分割成小文件，进行排序
	 * 
	 */
	public static void split() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/main/data.txt")));
			long start = System.currentTimeMillis();
			for (int k = 0; k < LIST_NUMBER; k++) {
				long time1 = System.currentTimeMillis();
				// 读入内存
				for (int i = 0; i < BLOCK_SIZE; i++) {
					String line = br.readLine();
					String[] str = line.split(" ");
					outputBuffer[i] = new Record(Integer.valueOf(str[0]), str[1]);
				}
				// 进行排序
				long time2 = System.currentTimeMillis();
//				Arrays.sort(outputBuffer);
				quickSort(outputBuffer, 0, outputBuffer.length - 1);
				long time3 = System.currentTimeMillis();
				// 输出到文件
				writeFile(getFileName(k));
				long time4 = System.currentTimeMillis();
				System.out.printf("Block %d, read %d ms, sort %d ms, write %d ms\n", k, time2 - time1, time3 - time2,
						time4 - time3);
			}
			long end = System.currentTimeMillis();
			System.out.printf("Split time: %d ms\n", end - start);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String getFileName(int k) {
		return "src/sort/sorted_" + k + ".txt";
	}

	/**
	 * 合并阶段，将排好序的小文件进行归并
	 * 
	 * @throws IOException
	 */
	public static void merge() throws IOException {
		String outFile = "src/sort/result.txt";
		FileOutputStream out = new FileOutputStream(new File(outFile), true); // 追加方式
		BufferedOutputStream bout = new BufferedOutputStream(out);
		BufferedReader[] br = new BufferedReader[LIST_NUMBER];
		int i = 0;
		int total = 0; // 总共写入的记录数
		int count = 0; // 输出缓冲区已写入的记录数
		long start = System.currentTimeMillis(); // 计时
		// 初始化bufferReader 以及输入缓冲区
		for (int k = 0; k < LIST_NUMBER; k++) {
			br[k] = new BufferedReader(new FileReader(new File(getFileName(k))));
			String line = br[k].readLine();
			String[] str = line.split(" ");
			inputBuffer[k] = new Record(Integer.valueOf(str[0]), str[1]);
		}
		while (total < RECORD_NUM) {
			// 选择输入缓冲区中最小的填充到输出缓冲区
			int index = minInBuffer();
			copy(outputBuffer[count], inputBuffer[index]);
			count++;
			total++;
			// 输出缓冲区已满, 写到文件
			if (count >= BLOCK_SIZE) {
				count = 0;
				writeResult(bout);
			}
			// 从外部文件继续读取一个记录到缓冲区
			String line = "";
			if ((line = br[index].readLine()) != null) {
				inputBuffer[index].setValue(line);
			} else {
				// 该文件读完,从其它外部文件读入
				for (i = 0; i < LIST_NUMBER; i++) {
					if ((line = br[i].readLine()) != null) {
						br[index] = br[i]; // 将读完的reader指针切换到未读完的reader上
						inputBuffer[index].setValue(line);
						break;
					}
				}
			}
			// 判断是否外部子文件都被读完
			if (i >= LIST_NUMBER) { // 此时仅剩输入缓冲区中的记录,直接排序，写到文件
//				Arrays.sort(inputBuffer);
				quickSort(inputBuffer, 0, inputBuffer.length - 1);
				for (int j = 1; j < LIST_NUMBER; j++) {
					copy(outputBuffer[BLOCK_SIZE - LIST_NUMBER + j], inputBuffer[j]);
					count++;
					total++;
				}
				writeResult(bout);
			}
		}
		// 关闭bufferReader
		for (int k = 0; k < LIST_NUMBER; k++) {
			br[k].close();
		}
		bout.close(); // 关闭bufferWriter
		long end = System.currentTimeMillis();
		System.out.printf("Merge time: %d ms\n", end - start);
	}

	/**
	 * 找到输入缓冲区中的最小记录的下标
	 * 
	 * @return
	 */
	private static int minInBuffer() {
		int min = inputBuffer[0].getKey();
		int minIndex = 0;
		for (int k = 1; k < LIST_NUMBER; k++) {
			if (inputBuffer[k].getKey() < min) {
				min = inputBuffer[k].getKey();
				minIndex = k;
			}
		}
		return minIndex;
	}

	/**
	 * 将记录B的值赋值给记录A
	 * 
	 * @param A
	 * @param B
	 */
	private static void copy(Record A, Record B) {
		A.setKey(B.getKey());
		A.setContent(B.getContent());
	}

	/**
	 * 将输出缓冲区写入指定路径文件
	 * 
	 * @param path
	 */
	private static void writeFile(String path) {
		try {
			FileOutputStream out = new FileOutputStream(new File(path), false);	//子文件采用覆盖方式写入
			BufferedOutputStream bout = new BufferedOutputStream(out);
			for (int j = 0; j < BLOCK_SIZE; j++) {
				bout.write((outputBuffer[j].toString() + "\n").getBytes());
			}
			bout.flush();
			bout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将输出缓冲区写入结果文件
	 * 
	 * @param bout
	 */
	private static void writeResult(BufferedOutputStream bout) {
		try {
			for (int j = 0; j < BLOCK_SIZE; j++) {
				bout.write((outputBuffer[j].toString() + "\n").getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 快排
	 * 
	 * @param data
	 * @param l
	 * @param r
	 */
	private static void quickSort(Record[] data, int l, int r) {
		if (r <= l) {
			return;
		}
		int i = l;
		int j = r;
		Record key = data[l];
		while (j > i) {
			while (j > i && data[j].compareTo(key) >= 0) {
				j--;
			}
			data[i] = data[j];
			while (i < j && data[i].compareTo(key) <= 0) {
				i++;
			}
			data[j] = data[i];
		}
		data[i] = key;
		quickSort(data, l, i - 1);
		quickSort(data, i + 1, r);
	}

	/**
	 * 快排测试
	 * 
	 */
	private static void testQuickSort() {
		int[] key = new int[] { 12, 20, 5, 16, 15, 1, 30, 45 };
		Record[] data = new Record[8];
		for (int i = 0; i < 8; i++) {
			data[i] = new Record(key[i], "" + i);
		}
		for (int i = 0; i < 8; i++) {
			System.out.println(data[i]);
		}
		System.out.println("-----------------");
		quickSort(data, 0, data.length - 1);
		for (int i = 0; i < 8; i++) {
			System.out.println(data[i]);
		}
	}
}

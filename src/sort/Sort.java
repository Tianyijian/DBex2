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
	 * �����ļ��ָ��С�ļ�����������
	 * 
	 */
	public static void split() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("src/main/data.txt")));
			long start = System.currentTimeMillis();
			for (int k = 0; k < LIST_NUMBER; k++) {
				long time1 = System.currentTimeMillis();
				// �����ڴ�
				for (int i = 0; i < BLOCK_SIZE; i++) {
					String line = br.readLine();
					String[] str = line.split(" ");
					outputBuffer[i] = new Record(Integer.valueOf(str[0]), str[1]);
				}
				// ��������
				long time2 = System.currentTimeMillis();
//				Arrays.sort(outputBuffer);
				quickSort(outputBuffer, 0, outputBuffer.length - 1);
				long time3 = System.currentTimeMillis();
				// ������ļ�
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
	 * �ϲ��׶Σ����ź����С�ļ����й鲢
	 * 
	 * @throws IOException
	 */
	public static void merge() throws IOException {
		String outFile = "src/sort/result.txt";
		FileOutputStream out = new FileOutputStream(new File(outFile), true); // ׷�ӷ�ʽ
		BufferedOutputStream bout = new BufferedOutputStream(out);
		BufferedReader[] br = new BufferedReader[LIST_NUMBER];
		int i = 0;
		int total = 0; // �ܹ�д��ļ�¼��
		int count = 0; // �����������д��ļ�¼��
		long start = System.currentTimeMillis(); // ��ʱ
		// ��ʼ��bufferReader �Լ����뻺����
		for (int k = 0; k < LIST_NUMBER; k++) {
			br[k] = new BufferedReader(new FileReader(new File(getFileName(k))));
			String line = br[k].readLine();
			String[] str = line.split(" ");
			inputBuffer[k] = new Record(Integer.valueOf(str[0]), str[1]);
		}
		while (total < RECORD_NUM) {
			// ѡ�����뻺��������С����䵽���������
			int index = minInBuffer();
			copy(outputBuffer[count], inputBuffer[index]);
			count++;
			total++;
			// �������������, д���ļ�
			if (count >= BLOCK_SIZE) {
				count = 0;
				writeResult(bout);
			}
			// ���ⲿ�ļ�������ȡһ����¼��������
			String line = "";
			if ((line = br[index].readLine()) != null) {
				inputBuffer[index].setValue(line);
			} else {
				// ���ļ�����,�������ⲿ�ļ�����
				for (i = 0; i < LIST_NUMBER; i++) {
					if ((line = br[i].readLine()) != null) {
						br[index] = br[i]; // �������readerָ���л���δ�����reader��
						inputBuffer[index].setValue(line);
						break;
					}
				}
			}
			// �ж��Ƿ��ⲿ���ļ���������
			if (i >= LIST_NUMBER) { // ��ʱ��ʣ���뻺�����еļ�¼,ֱ������д���ļ�
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
		// �ر�bufferReader
		for (int k = 0; k < LIST_NUMBER; k++) {
			br[k].close();
		}
		bout.close(); // �ر�bufferWriter
		long end = System.currentTimeMillis();
		System.out.printf("Merge time: %d ms\n", end - start);
	}

	/**
	 * �ҵ����뻺�����е���С��¼���±�
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
	 * ����¼B��ֵ��ֵ����¼A
	 * 
	 * @param A
	 * @param B
	 */
	private static void copy(Record A, Record B) {
		A.setKey(B.getKey());
		A.setContent(B.getContent());
	}

	/**
	 * �����������д��ָ��·���ļ�
	 * 
	 * @param path
	 */
	private static void writeFile(String path) {
		try {
			FileOutputStream out = new FileOutputStream(new File(path), false);	//���ļ����ø��Ƿ�ʽд��
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
	 * �����������д�����ļ�
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
	 * ����
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
	 * ���Ų���
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

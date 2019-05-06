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
	private final static int LIST_NUMBER = RECORD_NUM / BLOCK_SIZE; // 16
	private static Record[] inputBuffer = new Record[LIST_NUMBER];
	private static Record[] outputBuffer = new Record[BLOCK_SIZE];

	public static void main(String[] args) {
		phrase1();
		try {
			phase2();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ظ��ĴΣ�ÿ��1MB�ļ�¼
	 */
	public static void phrase1() {
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
				Arrays.sort(outputBuffer);
				long time3 = System.currentTimeMillis();
				// ������ļ�
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
		return "src/sort/sorted_" + k + ".txt";
	}

	public static void phase2() throws IOException {
		String outFile = "src/sort/result.txt";
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
				writeFile(outFile);
			}
			// ���ⲿ�ļ�������ȡһ����¼��������
			String line = "";
			if ((line = br[index].readLine()) != null) {
				inputBuffer[index].setValue(line);
			} else {
				// ���ļ�����,�������ⲿ�ļ�����
				for (i = 0; i < LIST_NUMBER; i++) {
					if ((line = br[i].readLine()) != null) {
						inputBuffer[index].setValue(line);
						break;
					}
				}
			}
			// �ж��Ƿ��ⲿ���ļ���������
			if (i >= LIST_NUMBER) { // ��ʱ��ʣ���뻺�����еļ�¼,ֱ������д���ļ�
				Arrays.sort(inputBuffer);
				for (int j = 1; j < LIST_NUMBER; j++) {
					copy(outputBuffer[BLOCK_SIZE - LIST_NUMBER + j], inputBuffer[j]);
					count++;
					total++;
				}
				writeFile(outFile);
			}
		}
		// �ر�bufferReader
		for (int k = 0; k < LIST_NUMBER; k++) {
			br[k].close();
		}
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
			FileOutputStream out = new FileOutputStream(new File(path), true);
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
}

package bPlusTree;

public class Test {
	public static void main(String[] args) {
		test2();
	}

	public static void test1() {
		BPlusTree<Product, Integer> b = new BPlusTree<>(4);
		long time1 = System.nanoTime();

		for (int i = 0; i < 10000; i++) {
			Product p = new Product(i, "test" + i, 1.0 * i);
			b.insert(p, p.getId());
		}

		long time2 = System.nanoTime();

		for (int i = 9999; i >= 0; i--) {
			Product p1 = b.find(i);
			System.out.println(p1.getName());
		}

		long time3 = System.nanoTime();

		System.out.println("插入耗时: " + (time2 - time1));
		System.out.println("查询耗时: " + (time3 - time2));
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
	}
}

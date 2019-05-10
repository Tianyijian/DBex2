package bPlusTree2;

import java.util.LinkedList;
import java.util.Queue;


public class BPlusTree<T extends Comparable<T>, V> {
	private int n; // 阶数
	private int MAX_KEYS;
	private int MIN_KEYS;
	private int MAX_POINTER;

	private Node<T, V> root = null;

	public BPlusTree(int n) {
		super();
		this.n = n;
		this.MAX_KEYS = n;
		this.MIN_KEYS = Double.valueOf(Math.ceil(1.0 * this.n / 2)).intValue();
		this.MAX_POINTER = n + 1;
		this.root = new LeafNode<T, V>();
	}

	public void insert(T key, V value) {
		Node<T, V> node = this.root.insert(key, value);
		if (node != null) {
			this.root = node;
		}
	}

	public V find(T key) {
		return this.root.find(key);
	}

	public void print() {
		Queue<Node<T, V>> quene = new LinkedList<Node<T,V>>();
		quene.add(this.root);
		int num = 1;
		int num2 = 0;
		while(!quene.isEmpty()) {
			for (int j = 0; j < num;j++) {
				Node<T, V> head = quene.poll();
				num2 += (head.size + 1);
				for (int i = 0; i < head.size;i++) {
					System.out.print(head.keys[i]+ " ");
				}
				if (head instanceof InternalNode) {
					for (int i = 0; i <= head.size;i++) {
						quene.add(((InternalNode)head).pointers[i]);
					}
				}
				if (j < num -1) {
					System.out.print("-->");
				}
			}
			System.out.println();
			num = num2;
			num2 = 0;
		}
	}
	abstract class Node<T extends Comparable<T>, V> {
		protected Node<T, V> parent;

		protected Object[] keys;

		protected int size;

		abstract Node<T, V> insert(T key, V value);

		abstract V find(T key);
	}

	class InternalNode<T extends Comparable<T>, V> extends Node<T, V> {
		private Node<T, V>[] pointers;

		public InternalNode() {
			this.size = 0;
			this.pointers = new Node[MAX_POINTER];
			this.keys = new Object[MAX_KEYS];
		}

		@Override
		Node<T, V> insert(T key, V value) {
			int i = 0;
			while (i < this.size) {
				if (key.compareTo((T) this.keys[i]) < 0) {
					break;
				}
				i++;
			}
			return this.pointers[i].insert(key, value);
		}

		@Override
		V find(T key) {
			int i = 0;
			while (i < this.size) {
				if (key.compareTo((T) this.keys[i]) < 0) {
					break;
				}
				i++;
			}
			return this.pointers[i].find(key);
		}

		private Node<T, V> insertInParent(T key, Node<T, V> leftChild, Node<T, V> rightChild) {
			// 父结点为新建的节点
			if (this.size == 0) {
				this.size++;
				this.pointers[0] = leftChild;
				this.pointers[1] = rightChild;
				this.keys[0] = key;
				return this;
			}
            Object[] newKeys = new Object[MAX_KEYS + 1];
            Node[] newPointers = new Node[MAX_POINTER + 1];
            
            int i = 0;
            for(; i < this.size; i++) {
                T curKey = (T)this.keys[i];
                if (curKey.compareTo(key) > 0) break;
            }
            
            System.arraycopy(this.keys, 0, newKeys, 0, i);
            newKeys[i] = key;
            System.arraycopy(this.keys, i, newKeys, i + 1, this.size - i);
            
            System.arraycopy(this.pointers, 0, newPointers, 0, i + 1);
            newPointers[i + 1] = rightChild;
            System.arraycopy(this.pointers, i + 1, newPointers, i + 2, this.size - i);

            this.size++;
            if(this.size <= MAX_KEYS) {
                System.arraycopy(newKeys, 0, this.keys, 0, this.size);
                System.arraycopy(newPointers, 0, this.pointers, 0, this.size + 1);
                return null;
            }


			int m = (this.size / 2);

			// split the internal node
			InternalNode<T, V> newNode = new InternalNode<T, V>();

//			newNode.size = this.size - m - 1;
			newNode.size = m;
			System.arraycopy(newKeys, this.size - m, newNode.keys, 0, m);
			System.arraycopy(newPointers, this.size - m, newNode.pointers, 0, m+1);

			// reset the children's parent to the new node.
			for (int j = 0; j <= newNode.size; j++) {
				newNode.pointers[j].parent = newNode;
			}

			this.size = this.size - m - 1;
			this.keys = new Object[MAX_KEYS];
			this.pointers = new Node[MAX_POINTER];
			System.arraycopy(newKeys, 0, this.keys, 0, this.size);
			System.arraycopy(newPointers, 0, this.pointers, 0, this.size + 1);

			if (this.parent == null) {
				this.parent = new InternalNode<T, V>();
			}
			newNode.parent = this.parent;

			return ((InternalNode<T, V>) this.parent).insertInParent((T) newKeys[this.size], this, newNode);

		}

		/**
		 * 直接在叶子节点中插入
		 */
		public void insert_in_leaf(T key, Node<T, V> node, int i) {
			// 移动插入
			if (i < this.size) {
				System.arraycopy(this.keys, i, this.keys, i + 1, this.size - i);
				System.arraycopy(this.pointers, i, this.pointers, i + 1, this.size + 1 - i);
			}
			this.keys[i] = key;
			this.pointers[i] = node;
			this.size++;
		}
	}

	class LeafNode<T extends Comparable<T>, V> extends Node<T, V> {
		private Object[] values;

		public LeafNode() {
			this.size = 0;
			this.keys = new Object[MAX_KEYS];
			this.values = new Object[MAX_POINTER];
			this.parent = null;
		}

		@Override
		Node<T, V> insert(T key, V value) {
			// 寻找位置
			int i = 0;
			while (i < this.size) {
				T curKey = (T) this.keys[i];
				//关键字相同,则更新
				if (curKey.compareTo(key) == 0) {
					this.values[i] = value;
					return null;
				}

				if (curKey.compareTo(key) > 0)
					break;
				i++;
			}
			// 不需要分裂
			if (this.size < MAX_KEYS) {
				this.insert_in_leaf(key, value, i);
				return null;
			}
			
			Object[] newKeys = new Object[MAX_KEYS + 1];
			Object[] newValues = new Object[MAX_POINTER + 1];
			// 复值到新区域
			System.arraycopy(this.keys, 0, newKeys, 0, i);
			newKeys[i] = key;
			System.arraycopy(this.keys, i, newKeys, i + 1, this.size - i);

			System.arraycopy(this.values, 0, newValues, 0, i);
			newValues[i] = value;
			System.arraycopy(this.values, i, newValues, i + 1, this.size - i);

			this.size++;

			// 需要分裂
			int m = this.size / 2;

			this.keys = new Object[MAX_KEYS];
			this.values = new Object[MAX_POINTER];
			System.arraycopy(newKeys, 0, this.keys, 0, m);
			System.arraycopy(newValues, 0, this.values, 0, m);
			// 新建节点
			LeafNode<T, V> newNode = new LeafNode<T, V>();
			newNode.size = this.size - m;
			System.arraycopy(newKeys, m, newNode.keys, 0, newNode.size);
			System.arraycopy(newValues, m, newNode.values, 0, newNode.size);

			this.size = m;
			// 父结点为空,新建父结点
			if (this.parent == null) {
				this.parent = new InternalNode<T, V>();
			}
			newNode.parent = this.parent;
			// 叶子节点拆分成功,插入父结点
			InternalNode<T, V> parentNode = (InternalNode<T, V>) this.parent;
			return parentNode.insertInParent((T) newNode.keys[0], this, newNode);
		}

		@Override
		V find(T key) {
			if (this.size <= 0) {
				return null;
			}
			int l = 0;
			int r = this.size;
			int m = (l + r) / 2;
			while (l < r) {
				T mKey = (T) this.keys[m];
				if (key.compareTo(mKey) == 0) {
					return (V) this.values[m];
				} else if (key.compareTo(mKey) < 0) {
					r = m;
				} else {
					l = m;
				}
				m = (l + r) / 2;
			}
			return null;
		}

		/**
		 * 直接在叶子节点中插入
		 */
		public void insert_in_leaf(T key, V value, int i) {
			// 移动插入
			if (i < this.size) {
				System.arraycopy(this.keys, i, this.keys, i + 1, this.size - i);
				System.arraycopy(this.values, i, this.values, i + 1, this.size - i);
			}
			this.keys[i] = key;
			this.values[i] = value;
			this.size++;
		}
	}
}

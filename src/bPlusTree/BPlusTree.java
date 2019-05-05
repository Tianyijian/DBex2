package bPlusTree;

import java.util.LinkedList;
import java.util.Queue;

public class BPlusTree<T, V extends Comparable<V>> {
	//
	private Integer bTreeOrder;
	// B+树的非叶子点最大拥有的节点数量，也是键的数量
	private Integer maxNumber;

	private Integer minNumber;
	
	private Node<T, V> root;

	private LeafNode<T, V> left;

	public BPlusTree() {
		this(3);
	}

	public BPlusTree(Integer bTreeOrder) {
		super();
		this.bTreeOrder = bTreeOrder;
		this.maxNumber = bTreeOrder + 1;
		this.minNumber = Double.valueOf(Math.ceil(bTreeOrder / 2.0)).intValue();
		this.root = new LeafNode<T, V>();
		this.left = null;
	}

	/**查找
	 * @param key
	 * @return
	 */
	public T find(V key) {
		T t = this.root.find(key);
		if (t == null) {
			System.out.println("不存在");
		} 
		return t;
	}
	
	/**插入
	 * @param value
	 * @param key
	 */
	public void insert(T value, V key) {
		if (key == null) {
			return;
		}
		Node<T,V> t = this.root.insert(value, key); 
		if (t!=null) {
			this.root = t;
		}
		this.left = (LeafNode<T, V>)this.root.refreshLeft();
	}
	
	public void print() {
		Queue<Node<T, V>> quene = new LinkedList<BPlusTree<T,V>.Node<T,V>>();
		quene.add(this.root);
		int num = 1;
		int num2 = 0;
		while(!quene.isEmpty()) {
			for (int j = 0; j < num;j++) {
				Node<T, V> head = quene.poll();
				num2 += head.number;
				for (int i = 0; i < head.number;i++) {
					System.out.print(head.keys[i]+ " ");
					if (head.childs[i] != null) {
						quene.add(head.childs[i]);
					}
				}
				System.out.print("-->");
			}
			System.out.println();
			num = num2;
			num2 = 0;
		}
	}
	
	
	abstract class Node<T, V extends Comparable<V>> {
		// 父结点
		protected Node<T, V> parent;
		// 子结点
		protected Node<T, V>[] childs;
		// 键(子结点)数量
		protected Integer number;
		// 键
		protected Object keys[];

		public Node() {
			this.keys = new Object[maxNumber];
			this.childs = new Node[maxNumber];
			this.number = 0;
			this.parent = null;
		}

		// 查找
		abstract T find(V key);

		// 插入
		abstract Node<T, V> insert(T value, V key);
		
		abstract LeafNode<T, V> refreshLeft();
		
	}
	

	class BPlusNode<T, V extends Comparable<V>> extends Node<T, V> {

		public BPlusNode() {
			super();
		}

		@Override
		T find(V key) {
			int i = 0;
			while (i < this.number) {
				if (key.compareTo((V) this.keys[i]) <= 0) {
					break;
				}
				i++;
			}
			if (i == this.number) {
				return null;
			}
			return this.childs[i].find(key);
		}

		@Override
		Node<T, V> insert(T value, V key) {
			int i = 0;
			while (i < this.number) {
				if (key.compareTo((V) this.keys[i]) < 0) {
					break;
				}
				i++;
			}
			// TODO
			if (key.compareTo((V) this.keys[this.number - 1]) >= 0) {
				i--;
			}
			return this.childs[i].insert(value, key);
		}
		
		@Override
		LeafNode<T, V> refreshLeft() {
			return this.childs[0].refreshLeft();
		}

		Node<T, V> insertNode(Node<T, V> node1, Node<T, V> node2, V key) {
			V oldKey = null;
			if (this.number > 0) {
				oldKey = (V) this.keys[this.number - 1];
			}
			// 父结点为空，直接放入两个子结点
			if (key == null || this.number <= 0) {
				this.keys[0] = node1.keys[node1.number - 1];
				this.keys[1] = node2.keys[node2.number - 1];
				this.childs[0] = node1;
				this.childs[1] = node2;
				this.number += 2;
				return this;
			}
			// 原有节点不为空，应该先寻找原有节点的位置，
			int i = 0;
			while (key.compareTo((V) this.keys[i]) != 0) {
				i++;
			}
			// 左边节点的最大值可以直接插入
			this.keys[i] = node1.keys[node1.number - 1];
			this.childs[i] = node1;

			Object[] tempKeys = new Object[maxNumber];
			Object[] tempChilds = new Object[maxNumber];

			System.arraycopy(this.keys, 0, tempKeys, 0, i + 1);
			System.arraycopy(this.childs, 0, tempChilds, 0, i + 1);
			System.arraycopy(this.keys, i + 1, tempKeys, i+2, this.number - i - 1); // TODO
			System.arraycopy(this.childs, i + 1, tempChilds, i+2, this.number - i - 1);
			tempKeys[i + 1] = node2.keys[node2.number - 1];
			tempChilds[i + 1] = node2;
			this.number++;
			// 判断是否需要拆分
			if (this.number <= bTreeOrder) {
				System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
				System.arraycopy(tempChilds, 0, this.childs, 0, this.number);
				
				// 有可能需要更新父节点的边界值
				Node node = this;
				while (node.parent != null) {
					V tempKey = (V) node.keys[node.number - 1];
					if (tempKey.compareTo((V) node.parent.keys[node.parent.number - 1]) > 0) {	
						node.parent.keys[node.parent.number - 1] = tempKey;
					} 
					node = node.parent; // TODO
				}
				return null;
			}
			int middle = this.number / 2;
			// 新建非叶节点，作为拆分的右半部分
			BPlusNode<T, V> tempNode = new BPlusNode<T, V>();
			tempNode.number = this.number - middle;
			tempNode.parent = this.parent;
			if (this.parent == null) {
				BPlusNode<T, V> tempBPlusNode = new BPlusNode<>();
				tempNode.parent = tempBPlusNode;
				this.parent = tempBPlusNode;
				oldKey = null;
			}
			System.arraycopy(tempKeys, middle, tempNode.keys, 0, tempNode.number);
			System.arraycopy(tempChilds, middle, tempNode.childs, 0, tempNode.number);
			for (int j = 0; j < tempNode.number; j++) {
				tempNode.childs[j].parent = tempNode;
			}
			// 让原有非叶子节点作为左边节点
			this.number = middle;
			this.keys = new Object[maxNumber];
			this.childs = new Node[maxNumber];
			System.arraycopy(tempKeys, 0, this.keys, 0, middle);
			System.arraycopy(tempChilds, 0, this.childs, 0, middle);

			// 叶子节点拆分成功后,需要把新生成的节点插入父节点
			BPlusNode<T, V> parentNode = (BPlusNode<T, V>) this.parent;
			return parentNode.insertNode(this, tempNode, oldKey);
		}


	}

	class LeafNode<T, V extends Comparable<V>> extends Node<T, V> {

		protected Object values[];
		protected LeafNode left;
		protected LeafNode right;

		public LeafNode() {
			super();
			this.values = new Object[maxNumber];
			this.left = null;
			this.right = null;
		}

		@Override
		T find(V key) {
			if (this.number <= 0) {
				return null;
			}
			int l = 0;
			int r = this.number;
			int m = (l + r) / 2;
			while (l < r) {
				V mKey = (V) this.keys[m];
				if (key.compareTo(mKey) == 0) {
					return (T) this.values[m];
				} else if (key.compareTo(mKey) < 0) {
					r = m;
				} else {
					l = m;
				}
				m = (l + r) / 2;
			}
			return null;
		}

		@Override
		Node<T, V> insert(T value, V key) {
			// 保留原始存在的父节点的键
			V oldKey = null;
			if (this.number > 0) {
				oldKey = (V) this.keys[this.number - 1];
			}
			// 插入数据
			int i = 0;
			while (i < this.number) {
				if (key.compareTo((V) this.keys[i]) < 0) {
					break;
				}
				i++;
			}
			// 复制数组
			Object tempKeys[] = new Object[maxNumber];
			Object tempValues[] = new Object[maxNumber];
			System.arraycopy(this.keys, 0, tempKeys, 0, i);
			System.arraycopy(this.values, 0, tempValues, 0, i);
			System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
			System.arraycopy(this.values, i, tempValues, i + 1, this.number - i);
			tempKeys[i] = key;
			tempValues[i] = value;
			this.number++;
			// 判断是否需要拆分
			if (this.number <= bTreeOrder) {
				System.arraycopy(tempKeys, 0, keys, 0, this.number);
				System.arraycopy(tempValues, 0, values, 0, this.number);

				// 有可能需要更新父节点的边界值
				Node node = this;
				while (node.parent != null) {
					V tempKey = (V) node.keys[node.number - 1];
					if (tempKey.compareTo((V) node.parent.keys[node.parent.number - 1]) > 0) {	
						node.parent.keys[node.parent.number - 1] = tempKey;
					} 
					node = node.parent; // TODO
				}
				return null;
			}
			// 需要拆分
			int m = this.number / 2;
			// 新建拆分的右半部分
			LeafNode<T, V> tempNode = new LeafNode<T, V>();
			tempNode.number = this.number - m;
			tempNode.parent = this.parent;
			// 如果父结点为空，新建父节点
			if (this.parent == null) {
				BPlusNode<T, V> temPlusNode = new BPlusNode<>();
				tempNode.parent = temPlusNode;
				this.parent = temPlusNode;
				oldKey = null;
			}
			System.arraycopy(tempKeys, m, tempNode.keys, 0, tempNode.number);
			System.arraycopy(tempValues, m, tempNode.values, 0, tempNode.number);
			// 原有叶子节点作为拆分的左半部分
			this.number = m;
			this.keys = new Object[maxNumber];
			this.values = new Object[maxNumber];
			System.arraycopy(tempKeys, 0, this.keys, 0, m);
			System.arraycopy(tempValues, 0, this.values, 0, m);

			this.right = tempNode;
			tempNode.left = this;

			// 叶子节点拆分成功，插入父结点
			BPlusNode<T, V> parentNode = (BPlusNode<T, V>) this.parent;
			return parentNode.insertNode(this, tempNode, oldKey);
		}

		@Override
		LeafNode<T, V> refreshLeft() {
			if (this.number < 0) {
				return null;
			}
			return this;
		}
	}
}

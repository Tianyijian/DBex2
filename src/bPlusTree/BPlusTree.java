package bPlusTree;

public class BPlusTree<T, V extends Comparable<V>> {
	//
	private Integer bTreeOrder;
	// B+���ķ�Ҷ�ӵ����ӵ�еĽڵ�������Ҳ�Ǽ�������
	private Integer maxNumber;

	private Node<T, V> root;

	private LeafNode<T, V> left;

	public BPlusTree() {
		this(3);
	}

	public BPlusTree(Integer bTreeOrder) {
		super();
		this.bTreeOrder = bTreeOrder;
		this.maxNumber = bTreeOrder + 1;
		this.root = new LeafNode<T, V>();
		this.left = null;
	}

	/**����
	 * @param key
	 * @return
	 */
	public T find(V key) {
		T t = this.root.find(key);
		if (t == null) {
			System.out.println("������");
		} 
		return t;
	}
	
	/**����
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
	
	
	abstract class Node<T, V extends Comparable<V>> {
		// �����
		protected Node<T, V> parent;
		// �ӽ��
		protected Node<T, V>[] childs;
		// ��(�ӽ��)����
		protected Integer number;
		// ��
		protected Object keys[];

		public Node() {
			this.keys = new Object[maxNumber];
			this.childs = new Node[maxNumber];
			this.number = 0;
			this.parent = null;
		}

		// ����
		abstract T find(V key);

		// ����
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
			// �����Ϊ�գ�ֱ�ӷ��������ӽ��
			if (key == null || this.number <= 0) {
				this.keys[0] = node1.keys[node1.number - 1];
				this.keys[1] = node2.keys[node2.number - 1];
				this.childs[0] = node1;
				this.childs[1] = node2;
				this.number += 2;
				return this;
			}
			// ԭ�нڵ㲻Ϊ�գ�Ӧ����Ѱ��ԭ�нڵ��λ�ã�
			int i = 0;
			while (key.compareTo((V) this.keys[i]) != 0) {
				i++;
			}
			// ��߽ڵ�����ֵ����ֱ�Ӳ���
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
			// �ж��Ƿ���Ҫ���
			if (this.number <= bTreeOrder) {
				System.arraycopy(tempKeys, 0, this.keys, 0, this.number);
				System.arraycopy(tempChilds, 0, this.childs, 0, this.number);
				return null;
			}
			int middle = this.number / 2;
			// �½���Ҷ�ڵ㣬��Ϊ��ֵ��Ұ벿��
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
			// ��ԭ�з�Ҷ�ӽڵ���Ϊ��߽ڵ�
			this.number = middle;
			this.keys = new Object[maxNumber];
			this.childs = new Node[maxNumber];
			System.arraycopy(tempKeys, 0, this.keys, 0, middle);
			System.arraycopy(tempChilds, 0, this.childs, 0, middle);

			// Ҷ�ӽڵ��ֳɹ���,��Ҫ�������ɵĽڵ���븸�ڵ�
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
			// ����ԭʼ���ڵĸ��ڵ�ļ�
			V oldKey = null;
			if (this.number > 0) {
				oldKey = (V) this.keys[this.number - 1];
			}
			// ��������
			int i = 0;
			while (i < this.number) {
				if (key.compareTo((V) this.keys[i]) < 0) {
					break;
				}
				i++;
			}
			// ��������
			Object tempKeys[] = new Object[maxNumber];
			Object tempValues[] = new Object[maxNumber];
			System.arraycopy(this.keys, 0, tempKeys, 0, i);
			System.arraycopy(this.values, 0, tempValues, 0, i);
			System.arraycopy(this.keys, i, tempKeys, i + 1, this.number - i);
			System.arraycopy(this.values, i, tempValues, i + 1, this.number - i);
			tempKeys[i] = key;
			tempValues[i] = value;
			this.number++;
			// �ж��Ƿ���Ҫ���
			if (this.number <= bTreeOrder) {
				System.arraycopy(tempKeys, 0, keys, 0, this.number);
				System.arraycopy(tempValues, 0, values, 0, this.number);

				// �п�����Ҫ���¸��ڵ�ı߽�ֵ
				Node node = this;
				while (node.parent != null) {
					V tempKey = (V) node.keys[node.number - 1];
					if (tempKey.compareTo((V) node.parent.keys[node.parent.number - 1]) > 0) {	
						node.parent.keys[node.parent.number - 1] = tempKey;
						node = node.parent; // TODO
					} else {	//TODO
						break;
					}
				}
				return null;
			}
			// ��Ҫ���
			int m = this.number / 2;
			// �½���ֵ��Ұ벿��
			LeafNode<T, V> tempNode = new LeafNode<T, V>();
			tempNode.number = this.number - m;
			tempNode.parent = this.parent;
			// ��������Ϊ�գ��½����ڵ�
			if (this.parent == null) {
				BPlusNode<T, V> temPlusNode = new BPlusNode<>();
				tempNode.parent = temPlusNode;
				this.parent = temPlusNode;
				oldKey = null;
			}
			System.arraycopy(tempKeys, m, tempNode.keys, 0, tempNode.number);
			System.arraycopy(tempValues, m, tempNode.values, 0, tempNode.number);
			// ԭ��Ҷ�ӽڵ���Ϊ��ֵ���벿��
			this.number = m;
			this.keys = new Object[maxNumber];
			this.values = new Object[maxNumber];
			System.arraycopy(tempKeys, 0, this.keys, 0, m);
			System.arraycopy(tempValues, 0, this.values, 0, m);

			this.right = tempNode;
			tempNode.left = this;

			// Ҷ�ӽڵ��ֳɹ������븸���
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

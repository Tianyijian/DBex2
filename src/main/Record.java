package main;

public class Record implements Comparable<Record>{

	private int key;
	private String content;

	public Record() {
		super();
	}
	
	public Record(int key, String content) {
		super();
		this.key = key;
		this.content = content;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	public void setValue(String line) {
		String[] str = line.split(" ");
		this.key = Integer.valueOf(str[0]);
		this.content = str[1];
	}
	@Override
	public String toString() {
		return this.key + " " + this.content;
	}

	@Override
	public int compareTo(Record o) {
		return Integer.compare(this.key, o.key);
	}
	
	
}

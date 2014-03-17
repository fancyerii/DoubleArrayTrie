package com.antbrains.datrie;

import java.io.Serializable;

public class IntArrayList implements Serializable {
	private static final long serialVersionUID = 1908530358259070518L;
	private int[] data;
	private int count;
	private int expandFactor;

	public IntArrayList(int size) {
		this(size, 10240);
	}

	public IntArrayList(int size, int factor) {
		this.data = new int[size];
		this.count = 0;
		this.expandFactor = factor;
	}

	public void add(int num) {
		if (this.count == this.data.length) {
			int[] newData = new int[this.data.length + this.expandFactor];
			System.arraycopy(this.data, 0, newData, 0, this.data.length);
			this.data = newData;
		}
		this.data[this.count] = num;
		this.count += 1;
	}

	public int size() {
		return this.count;
	}

	public int getExpandFactor() {
		return this.expandFactor;
	}

	public void set(int pos, int num) {
		this.data[pos] = num;
	}

	public int get(int pos) {
		return this.data[pos];
	}
	
	public void removeLast(){
		count--;
	}
	
	public int getLast(){
		return data[count-1];
	}
	
	public int pop(){
		return data[--count];
	}
}

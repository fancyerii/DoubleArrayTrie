package com.antbrains.datrie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class DoubleArrayTrie implements Serializable {
	private static final long serialVersionUID = 5586394930559218801L;
	private static final int leafBit = 1073741824;
	private static final int ROOT_INDEX = 1;
	private static final int ROOT_BASE = 1;
	private static final int[] EMPTY_WALK_STATE = { -1, -1 };
	CharacterMapping charMap;
	private char unuseChar = '\000';
	private int unuseCharValue = 0;
	IntArrayList check;
	IntArrayList base;
	private int number;

	public DoubleArrayTrie(){
		this(new Utf8CharacterMapping());
	}

	public DoubleArrayTrie(CharacterMapping charMap){
		this.charMap = charMap;
		this.base = new IntArrayList(charMap.getInitSize());
		this.check = new IntArrayList(charMap.getInitSize());

		this.base.add(0);

		this.check.add(0);

		this.base.add(1);
		this.check.add(0);
		expandArray(charMap.getInitSize());
		this.unuseCharValue = charMap.zeroId();
	}
	
	public int getBaseSize(){
		return base.getSize();
	}
	
	public int getCheckSize(){
		return check.getSize();
	}

	private boolean isLeaf(int value) {
		return (value > 0) && ((value & 0x40000000) != 0);
	}

	private int setLeafValue(int value) {
		return value | 0x40000000;
	}

	private int getLeafValue(int value) {
		return value ^ 0x40000000;
	}

	public int getSize() {
		return this.base.getSize();
	}

	private int getBase(int position) {
		return this.base.get(position);
	}

	private int getCheck(int position) {
		return this.check.get(position);
	}

	private void setBase(int position, int value) {
		this.base.set(position, value);
	}

	private void setCheck(int position, int value) {
		this.check.set(position, value);
	}

	protected boolean isEmpty(int position) {
		return getCheck(position) <= 0;
	}

	private int getNextFreeBase(int nextChar){
		int pos = -getCheck(0);
		while (pos != 0) {
			if (pos > nextChar + 1) {
				return pos - nextChar;
			}
			pos = -getCheck(pos);
		}
		int oldSize = getSize();
		expandArray(oldSize + this.base.getExpandFactor());
		return oldSize;
	}

	private void addFreeLink(int position) {
		this.check.set(position, this.check.get(-this.base.get(0)));
		this.check.set(-this.base.get(0), -position);
		this.base.set(position, this.base.get(0));
		this.base.set(0, -position);
	}

	private void delFreeLink(int position) {
		this.base.set(-this.check.get(position), this.base.get(position));
		this.check.set(-this.base.get(position), this.check.get(position));
	}

	private void expandArray(int maxSize){
		int curSize = getSize();
		if (curSize > maxSize) {
			return;
		}
		if (maxSize >= leafBit) {
			throw new RuntimeException("Double Array Trie too large", null);
		}
		for (int i = curSize; i <= maxSize; i++) {
			this.base.add(0);
			this.check.add(0);
			addFreeLink(i);
		}
	}

	private boolean insert(String str, int value, boolean cover) {
		if ((null == str) || (str.contains(String.valueOf(this.unuseChar)))) {
			return false;
		}
		if ((value < 0) || ((value & 0x40000000) != 0)) {
			return false;
		}
		value = setLeafValue(value);

		int[] ids = this.charMap.toIdList(str + this.unuseChar);

		int fromState = 1;
		int toState = 1;
		int ind = 0;
		while (ind < ids.length) {
			int c = ids[ind];
			
			toState = getBase(fromState) + c;

			expandArray(toState);
			if (isEmpty(toState)) {
				delFreeLink(toState);

				setCheck(toState, fromState);
				if (ind == ids.length - 1) {
					this.number += 1;
					setBase(toState, value);
				} else {
					int nextChar = ids[(ind + 1)];
					setBase(toState, getNextFreeBase(nextChar));
				}
			} else if (getCheck(toState) != fromState) {
				solveConflict(fromState, c);

				continue;
			}
			fromState = toState;
			ind++;
		}
		if (cover) {
			setBase(toState, value);
		}
		return true;
	}

	private int moveChildren(SortedSet<Integer> children){
		int minChild = ((Integer) children.first()).intValue();
		int maxChild = ((Integer) children.last()).intValue();
		int cur = 0;
		while (getCheck(cur) != 0) {
			if (cur > minChild + 1) {
				int tempBase = cur - minChild;
				boolean ok = true;
				for (Iterator<Integer> itr = children.iterator(); itr.hasNext();) {
					int toPos = tempBase + ((Integer) itr.next()).intValue();
					if (toPos >= getSize()) {
						ok = false;
						break;
					}
					if (!isEmpty(toPos)) {
						ok = false;
						break;
					}
				}
				if (ok) {
					return tempBase;
				}
			}
			cur = -getCheck(cur);
		}
		int oldSize = getSize();
		expandArray(oldSize + maxChild);
		return oldSize;
	}

	private void solveConflict(int parent, int newChild){
		TreeSet<Integer> children = new TreeSet<>();

		children.add(new Integer(newChild));
		for (int c = 0; c < this.charMap.getCharsetSize(); c++) {
			int tempNext = getBase(parent) + c;
			if (tempNext >= getSize()) {
				break;
			}
			if ((tempNext < getSize()) && (getCheck(tempNext) == parent)) {
				children.add(new Integer(c));
			}
		}
		int newBase = moveChildren(children);

		children.remove(new Integer(newChild));
		for (Integer child : children) {
			int c = child.intValue();

			delFreeLink(newBase + c);

			setCheck(newBase + c, parent);

			setBase(newBase + c, getBase(getBase(parent) + c));

			int childBase = getBase(getBase(parent) + c);
			if (!isLeaf(childBase)) {
				for (int d = 0; d < this.charMap.getCharsetSize(); d++) {
					int nextPos = childBase + d;
					if (nextPos >= getSize()) {
						break;
					}
					if ((nextPos < getSize())
							&& (getCheck(nextPos) == getBase(parent) + c)) {
						setCheck(nextPos, newBase + c);
					}
				}
			}
			addFreeLink(getBase(parent) + c);
		}
		setBase(parent, newBase);
	}

	public int getNumbers() {
		return this.number;
	}

	public boolean coverInsert(String str, int value) {
		return insert(str, value, true);
	}

	public boolean uncoverInsert(String str, int value) {
		return insert(str, value, false);
	}

	public int[] find(String query, int start) {
		if ((query == null) || (start >= query.length())) {
			return new int[] { 0, -1 };
		}
		int curState = 1;
		int maxLength = 0;
		int lastVal = -1;
		for (int i = start; i < query.length(); i++) {
			int[] res = walkTrie(curState, query.charAt(i));
			if (res[0] == -1) {
				break;
			}
			curState = res[0];
			if (res[1] != -1) {
				maxLength = i - start + 1;
				lastVal = res[1];
			}
		}
		return new int[] { maxLength, lastVal };
	}
	
	public int[] findWithSupplementary(String query, int start){
		if ((query == null) || (start >= query.length())) {
			return new int[] { 0, -1 };
		}
		int curState = 1;
		int maxLength = 0;
		int lastVal = -1;
		int charCount=1;
		for (int i = start; i < query.length(); i+=charCount) {
			int codePoint=query.codePointAt(i);
			charCount=Character.charCount(codePoint);
			int[] res = walkTrie(curState, codePoint);
			if (res[0] == -1) {
				break;
			}
			curState = res[0];
			if (res[1] != -1) {
				maxLength = i - start + 1;
				lastVal = res[1];
			}
		}
		return new int[] { maxLength, lastVal };
		
	}
	
	public List<int[]> findAllWithSupplementary(String query, int start) {
		List<int[]> ret = new ArrayList<>(5);
		if ((query == null) || (start >= query.length())) {
			return ret;
		}
		int curState = 1;
		int charCount=1;
		for (int i = start; i < query.length(); i+=charCount) {
			int codePoint=query.codePointAt(i);
			charCount=Character.charCount(codePoint);
			int[] res = walkTrie(curState, codePoint);
			if (res[0] == -1) {
				break;
			}
			curState = res[0];
			if (res[1] != -1) {
				ret.add(new int[] { i - start + 1, res[1] });
			}
		}
		return ret;
	}

	public List<int[]> findAll(String query, int start) {
		List<int[]> ret = new ArrayList<>(5);
		if ((query == null) || (start >= query.length())) {
			return ret;
		}
		int curState = 1;
		for (int i = start; i < query.length(); i++) {
			int[] res = walkTrie(curState, query.charAt(i));
			if (res[0] == -1) {
				break;
			}
			curState = res[0];
			if (res[1] != -1) {
				ret.add(new int[] { i - start + 1, res[1] });
			}
		}
		return ret;
	}

	public int getRoot() {
		return ROOT_INDEX;
	}

	public int[] walkTrie(int curState, int codepoint) {
		if (curState < 1) {
			return EMPTY_WALK_STATE;
		}
		if ((curState != 1) && (isEmpty(curState))) {
			return EMPTY_WALK_STATE;
		}
		int[] ids = this.charMap.toIdList(codepoint);
		if (ids.length == 0) {
			return EMPTY_WALK_STATE;
		}
		for (int i = 0; i < ids.length; i++) {
			int c = ids[i];
			if ((getBase(curState) + c < getSize())
					&& (getCheck(getBase(curState) + c) == curState)) {
				curState = getBase(curState) + c;
			} else {
				return EMPTY_WALK_STATE;
			}
		}
		if (getCheck(getBase(curState) + this.unuseCharValue) == curState) {
			int value = getLeafValue(getBase(getBase(curState)
					+ this.unuseCharValue));
			return new int[] { curState, value };
		}
		return new int[] { curState, -1 };
	}

	public int delete(String str) {
		if (str == null) {
			return -1;
		}
		int curState = 1;
		int[] ids = this.charMap.toIdList(str);

		int[] path = new int[ids.length + 1];
		int i=0;
		for (; i < ids.length; i++) {
			int c = ids[i];
			if ((getBase(curState) + c >= getSize())
					|| (getCheck(getBase(curState) + c) != curState)) {
				break;
			}
			curState = getBase(curState) + c;
			path[i] = curState;
		}
		int ret = -1;
		if (i == ids.length) {
			if (getCheck(getBase(curState) + this.unuseCharValue) == curState) {
				this.number -= 1;
				ret = getLeafValue(getBase(getBase(curState)
						+ this.unuseCharValue));
				path[(path.length - 1)] = (getBase(curState) + this.unuseCharValue);
				for (int j = path.length - 1; j >= 0; j--) {
					boolean isLeaf = true;
					int state = path[j];
					for (int k = 0; k < this.charMap.getCharsetSize(); k++) {
						if (isLeaf(getBase(state))) {
							break;
						}
						if ((getBase(state) + k < getSize())
								&& (getCheck(getBase(state) + k) == state)) {
							isLeaf = false;
							break;
						}
					}
					if (!isLeaf) {
						break;
					}
					addFreeLink(state);
				}
			}
		}
		return ret;
	}

	public int getEmptySize() {
		int cnt = 0;
		for (int i = 0; i < getSize(); i++) {
			if (isEmpty(i)) {
				cnt++;
			}
		}
		return cnt;
	}

	public int getMaximumValue() {
		return leafBit-1;
	}
}

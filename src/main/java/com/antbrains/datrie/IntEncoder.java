package com.antbrains.datrie;

public interface IntEncoder {
	public int[] toIdList(int codePoint);
	public int[] toIdList(String paramString);
}

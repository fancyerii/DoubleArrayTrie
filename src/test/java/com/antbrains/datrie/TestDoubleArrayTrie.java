package com.antbrains.datrie;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestDoubleArrayTrie {
	
	private int N=1000;
	private int M=100;
	@Test
	public void testTrieWithUtf8() throws Exception {
		DoubleArrayTrie datrie = new DoubleArrayTrie(new Utf8CharacterMapping());
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 0.5, false);
		for (StringIntPair word : dicts) {
			datrie.coverInsert(word.s, word.i);
		}
				
		for (StringIntPair word : dicts) {
			int[] arr = datrie.find(word.s, 0);

			assertEquals(word.s.length(), arr[0]);
			assertEquals(word.i, arr[1]);
		}

		for (String word : notInDicts) {
			int[] arr = datrie.find(word, 0);
			assertNotEquals(word.length(), arr[0]);
		}
		
		for (String word : notInDicts) {
			int[] arr = datrie.find(word, 0);
			assertNotEquals(word.length(), arr[0]);
		}
		
		for (StringIntPair word:dicts){
			datrie.delete(word.s);
			int[] arr = datrie.find(word.s, 0);
			assertNotEquals(word.s.length(), arr[0]);
		}
	}
	
	@Test
	public void testIterator2() throws Exception{
		DoubleArrayTrie datrie=new DoubleArrayTrie();
		List<String> lines=loadEnglishDict();
		int lineNum=0;
		for(String line:lines){
			datrie.coverInsert(line, lineNum++);
		}
		Collections.sort(lines);
		assertEquals(lines.size(), datrie.size());
		DatrieIterator iter=datrie.iterator();
		Iterator<String> iter2=lines.iterator();
		while(iter.hasNext()&&iter2.hasNext()){
			iter.next();
			String s=iter2.next();
			assertEquals(s, iter.key());
		}
		
		assertFalse(iter.hasNext());
		assertFalse(iter2.hasNext());
	}
	
	@Test
	public void testPrefix(){
 
		DoubleArrayTrie datrie=new DoubleArrayTrie();
		datrie.coverInsert("ba", 4);
		datrie.coverInsert("bad", 5);
		datrie.coverInsert("badd", 6);
		datrie.coverInsert("bade", 7);
		datrie.coverInsert("abc", 1);
		datrie.coverInsert("a",2);
		datrie.coverInsert("abd", 3);
		List<String> prefixes=datrie.prefixMatch("a");
		assertEquals(3,prefixes.size());
		Iterator<String> iter=prefixes.iterator();
		assertEquals("a", iter.next());
		assertEquals("abc", iter.next());
		assertEquals("abd", iter.next());
	}
	
	@Test
	public void testPrefix2() throws Exception{
		List<String> words=loadEnglishDict();
		DoubleArrayTrie datrie=new DoubleArrayTrie();
		for(String word:words){
			datrie.coverInsert(word, 1);
		}
		List<String> prefixes=datrie.prefixMatch("ab");
		for(String prefix:prefixes){
			assertTrue(prefix.startsWith("ab"));
		}
		HashSet<String> set=new HashSet<String>(prefixes);
		for(String word:words){
			if(word.startsWith("ab")){
				assertTrue(set.contains(word));
			}
		}
	}

	@Test
	public void testPrefix3() throws Exception{
		List<String> words=loadChineseDict();
		DoubleArrayTrie datrie=new DoubleArrayTrie();
		for(String word:words){
			datrie.coverInsert(word, 1);
		}
		List<String> prefixes=datrie.prefixMatch("李");
		for(String prefix:prefixes){
			assertTrue(prefix.startsWith("李"));
		}
		HashSet<String> set=new HashSet<String>(prefixes);
		for(String word:words){
			if(word.startsWith("李")){
				assertTrue(set.contains(word));
			}
		}
	}
	
	@Test
	public void testIterator(){
		DoubleArrayTrie datrie=new DoubleArrayTrie();
		datrie.coverInsert("ba", 4);
		datrie.coverInsert("bad", 5);
		datrie.coverInsert("badd", 6);
		datrie.coverInsert("bade", 7);
		datrie.coverInsert("abc", 1);
		datrie.coverInsert("a",2);
		datrie.coverInsert("abd", 3);

		DatrieIterator iter=datrie.iterator();
		
		assertTrue(iter.hasNext());
		iter.next();
		assertEquals("a",iter.key());
		assertEquals(2,iter.value());
		
		assertTrue(iter.hasNext());
		iter.next();
		assertEquals("abc",iter.key());
		assertEquals(1,iter.value());
		
		iter.setValue(5);
		assertEquals(5,iter.value());
		
		assertTrue(iter.hasNext());
		iter.next();
		assertEquals("abd",iter.key());
		assertEquals(3,iter.value());
		
		assertTrue(iter.hasNext());
		iter.next();
		assertEquals("ba",iter.key());
		assertEquals(4,iter.value());
		
		assertTrue(iter.hasNext());
		iter.next();
		assertEquals("bad",iter.key());
		assertEquals(5,iter.value());
		
		assertTrue(iter.hasNext());
		iter.next();
		assertEquals("badd",iter.key());
		assertEquals(6,iter.value());
		
		assertTrue(iter.hasNext());
		iter.next();
		assertEquals("bade",iter.key());
		assertEquals(7,iter.value());
		
		assertFalse(iter.hasNext());
	}
	
	@Test
	public void testTrieWithUtf8BuildSpeed() throws Exception {
		long start=System.currentTimeMillis();
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 1.0, false);
		CharacterMapping cm=new Utf8CharacterMapping();
		for(int i=0;i<M;i++){
			DoubleArrayTrie datrie = new DoubleArrayTrie(cm);

			for (StringIntPair word : dicts) {
				datrie.coverInsert(word.s, word.i);
			}
		}
		System.out.println("testTrieWithUtf8BuildSpeed: "+(System.currentTimeMillis()-start)+" ms");
	}
	
	public void testTrieWithHighFreqBuildSpeed() throws Exception {
		long start=System.currentTimeMillis();
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 1.0, false);
		CharacterMapping cm=new HighFreqRangeCharacterMapping();
		for(int i=0;i<M;i++){
			DoubleArrayTrie datrie = new DoubleArrayTrie(cm);

			for (StringIntPair word : dicts) {
				datrie.coverInsert(word.s, word.i);
			}
		}
		System.out.println("testTrieWithHighFreqBuildSpeed: "+(System.currentTimeMillis()-start)+" ms");
	}
	
	@Test
	public void testMaxMatchSegment(){
		DoubleArrayTrie datrie=new DoubleArrayTrie();
		String[] dicts=new String[]{
			"今天",
			"天气",
			"非常"
		};
		for(String word:dicts){
			datrie.coverInsert(word, 0);
		}
		String sen="今天的天气真的是非常好啊";
		for(int idx=0;idx<sen.length();){
			int[] res=datrie.find(sen, idx);
			if(res[0]>0){
				System.out.print(sen.substring(idx, idx+res[0])+"\t");
				idx+=res[0];
			}else{
				System.out.print(sen.substring(idx,idx+1)+"\t");
				idx++;
			}
		}
		System.out.println();
	}
	
	@Test
	public void testValue(){
		DoubleArrayTrie datrie=new DoubleArrayTrie();
		String[] dicts=new String[]{
			"今天",
			"天气",
			"非常"
		};
		int idx=0;
		for(String word:dicts){
			datrie.coverInsert(word, idx++);
		}
		int[] res=datrie.find("今天", 0);
		assertEquals(2, res[0]);
		assertEquals(0, res[1]);
		
		res=datrie.find("非常", 0);
		assertEquals(2, res[0]);
		assertEquals(2, res[1]);
		
		
		res=datrie.find("中国", 0);
		assertEquals(0, res[0]);
	}
	
	@Test
	public void testHashMapBuildSpeed() throws Exception {
		long start=System.currentTimeMillis();
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 1.0, false);
		for(int i=0;i<M;i++){
			HashMap<MyString,Integer> map=new HashMap<MyString, Integer>();

			for (StringIntPair word : dicts) {
				map.put(new MyString(word.s.toCharArray()), word.i);
			}
		}
		System.out.println("testHashMapBuildSpeed: "+(System.currentTimeMillis()-start)+" ms");
	}	
	
	@Test
	public void testTroveMapBuildSpeed() throws Exception {
		long start=System.currentTimeMillis();
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 1.0, false);
		for(int i=0;i<M;i++){
			TObjectIntHashMap<MyString> map=new TObjectIntHashMap<MyString>();

			for (StringIntPair word : dicts) {
				map.put(new MyString(word.s.toCharArray()), word.i);
			}
		}
		System.out.println("testTroveMapBuildSpeed: "+(System.currentTimeMillis()-start)+" ms");
	}	

	@Test
	public void testTrieWithUtf8QuerySpeed() throws Exception {
		DoubleArrayTrie datrie = new DoubleArrayTrie(new Utf8CharacterMapping());
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 0.5, false);
		for (StringIntPair word : dicts) {
			datrie.coverInsert(word.s, word.i);
		}
		long start=System.currentTimeMillis();
		for(int i=0;i<N;i++){
			for (StringIntPair word : dicts) {
				datrie.find(word.s, 0);
			}
	
			for (String word : notInDicts) {
				datrie.find(word, 0);
			}
		}
		System.out.println("testTrieWithUtf8QuerySpeed: "+(System.currentTimeMillis()-start)+" ms");
	}

	
	public void testTrieWithHighFreqVInt() throws Exception {
		DoubleArrayTrie datrie = new DoubleArrayTrie(
				new HighFreqRangeCharacterMapping());
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 0.5, false);
		for (StringIntPair word : dicts) {
			datrie.coverInsert(word.s, word.i);
		}

		for (StringIntPair word : dicts) {
			int[] arr = datrie.find(word.s, 0);

			assertEquals(word.s.length(), arr[0]);
			assertEquals(word.s,word.i, arr[1]);
		}
		


		for (String word : notInDicts) {
			int[] arr = datrie.find(word, 0);
			assertNotEquals(word.length(), arr[0]);
		}
		
		for (StringIntPair word:dicts){
			datrie.delete(word.s);
			int[] arr = datrie.find(word.s, 0);
			assertNotEquals(word.s.length(), arr[0]);
		}
	}
	
	
	public void testTrieWithHighFreqVIntAndUtf8() throws Exception {
		RangeMapping rm=new RangeMapping();
		DoubleArrayTrie datrie = new DoubleArrayTrie(
				new HighFreqRangeCharacterMapping(rm,new VIntAndUtf8(rm)));
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 0.5, false);
 
		for (StringIntPair word : dicts) {
			datrie.coverInsert(word.s, word.i);
		}

		for (StringIntPair word : dicts) {
			int[] arr = datrie.find(word.s, 0);

			assertEquals(word.s, word.s.length(), arr[0]);
			assertEquals(word.s,word.i, arr[1]);
		}
		


		for (String word : notInDicts) {
			int[] arr = datrie.find(word, 0);
			assertNotEquals(word.length(), arr[0]);
		}
		
		for (StringIntPair word:dicts){
			datrie.delete(word.s);
			int[] arr = datrie.find(word.s, 0);
			assertNotEquals(word.s.length(), arr[0]);
		}
	}
	
	
	public void testTrieWithHighFreqQuerySpeed() throws Exception {
		DoubleArrayTrie datrie = new DoubleArrayTrie(
				new HighFreqRangeCharacterMapping());
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 0.5, false);
		
		
		for (StringIntPair word : dicts) {
			datrie.coverInsert(word.s, word.i);
		}
		long start=System.currentTimeMillis();
		for(int i=0;i<N;i++){
			for (StringIntPair word : dicts) {
				datrie.find(word.s, 0);
	 
			}
	
			for (String word : notInDicts) {
				datrie.find(word, 0);
	 
			}
		}
		System.out.println("testTrieWithHighFreqQuerySpeed: "+(System.currentTimeMillis()-start)+ " ms");
	}	
	
	@Test
	public void testHashMapQuerySpeed() throws Exception {
		HashMap<MyString,Integer> map=new HashMap<MyString,Integer>();
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 0.5, false);
		
		
		for (StringIntPair word : dicts) {
			map.put(new MyString(word.s.toCharArray()), word.i);
		}
		long start=System.currentTimeMillis();
		for(int i=0;i<N;i++){
			for (StringIntPair word : dicts) {
				map.get(word);
			}
	
			for (String word : notInDicts) {
				map.get(word);
			}
		}
		System.out.println("testHashMapQuerySpeed: "+(System.currentTimeMillis()-start)+ " ms");
	}	
	@Test
	public void testTroveHashMapQuerySpeed() throws Exception {
		TObjectIntHashMap<MyString> map=new TObjectIntHashMap<MyString>();
		List<StringIntPair> dicts = new ArrayList<StringIntPair>();
		List<String> notInDicts = new ArrayList<String>();
		this.loadData(dicts, notInDicts, 0.5, false);
		
		
		for (StringIntPair word : dicts) {
			map.put(new MyString(word.s.toCharArray()), word.i);
		}
		long start=System.currentTimeMillis();
		for(int i=0;i<N;i++){
			for (StringIntPair word : dicts) {
				map.get(word);
			}
	
			for (String word : notInDicts) {
				map.get(word);
			}
		}
		System.out.println("testTroveHashMapQuerySpeed: "+(System.currentTimeMillis()-start)+ " ms");
	}	

	@Test
	public void testHashMapFootPrint() throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		{
			List<StringIntPair> dicts = new ArrayList<StringIntPair>();
			List<String> notInDicts = new ArrayList<String>(0);
			this.loadData(dicts, notInDicts, 1, false);
			assertEquals(0, notInDicts.size());
			for (StringIntPair pair : dicts) {
				map.put(pair.s, pair.i);
			}
		}
		System.gc();
		long used = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("HashMap memory usage: " + used+" bytes");
		System.out.println(map.size());//reference to avoid map be gced.
	}

	@Test
	public void testUtf8FootPrint() throws Exception {
		DoubleArrayTrie datrie = new DoubleArrayTrie(new Utf8CharacterMapping());
		{
			List<StringIntPair> dicts = new ArrayList<StringIntPair>();
			List<String> notInDicts = new ArrayList<String>(0);
			this.loadData(dicts, notInDicts, 1, false);
			assertEquals(0, notInDicts.size());
			for (StringIntPair pair : dicts) {
				datrie.coverInsert(pair.s, pair.i);
			}
		}
		System.gc();
		long used = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("Utf8-Datrie memory usage: " + used+" bytes");
		System.out.println("trie size: base="+datrie.getBaseArraySize()+",check="+datrie.getCheckArraySize());
		System.out.println("free="+datrie.getFreeSize());
	}
	
	public void testHighFreqFootPrint() throws Exception {
		DoubleArrayTrie datrie = new DoubleArrayTrie(
				new HighFreqRangeCharacterMapping());
		{
			List<StringIntPair> dicts = new ArrayList<StringIntPair>();
			List<String> notInDicts = new ArrayList<String>(0);
			this.loadData(dicts, notInDicts, 1, false);
			assertEquals(0, notInDicts.size());
			for (StringIntPair pair : dicts) {
				datrie.coverInsert(pair.s, pair.i);
			}
		}
		System.gc();
		long used = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("HighFreq-Datrie memory usage: " + used+" bytes");
		System.out.println("trie size: base="+datrie.getBaseArraySize()+",check="+datrie.getCheckArraySize());
	}

	public void testHighFreqFootPrint2() throws Exception {
		RangeMapping rm=new RangeMapping();
		DoubleArrayTrie datrie = new DoubleArrayTrie(
				new HighFreqRangeCharacterMapping(rm,new VIntAndUtf8(rm)));
		
		{
			List<StringIntPair> dicts = new ArrayList<StringIntPair>();
			List<String> notInDicts = new ArrayList<String>(0);
			this.loadData(dicts, notInDicts, 1, false);
			assertEquals(0, notInDicts.size());
			for (StringIntPair pair : dicts) {
				datrie.coverInsert(pair.s, pair.i);
			}
		}
		System.gc();
		long used = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("HighFreq-Datrie2 memory usage: " + used+" bytes");
		System.out.println("trie size: base="+datrie.getBaseArraySize()+",check="+datrie.getCheckArraySize());
	}
	
	@Test
	public void testTroveHashMapFootPrint() throws Exception {
		TObjectIntHashMap<String> map=new TObjectIntHashMap<String>();
		{
			List<StringIntPair> dicts = new ArrayList<StringIntPair>();
			List<String> notInDicts = new ArrayList<String>(0);
			this.loadData(dicts, notInDicts, 1, false);
			assertEquals(0, notInDicts.size());
			for (StringIntPair pair : dicts) {
				map.put(pair.s, pair.i);
			}
		}
		System.gc();
		long used = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		System.out.println("Trove HashMap memory usage: " + used+" bytes");
		System.out.println(map.size());
	}
	
	private List<String> loadEnglishDict() throws Exception{
		InputStream is = getClass().getResourceAsStream("/en.txt");
		BufferedReader br = null;
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line;
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			return lines;

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	private List<String> loadChineseDict() throws Exception{
		InputStream is = getClass().getResourceAsStream("/cn.txt");
		BufferedReader br = null;
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line;
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			return lines;

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}
	
	private void loadData(List<StringIntPair> dicts, List<String> notInDicts,
			double ratio, boolean shuffle) throws Exception {
		InputStream is = getClass().getResourceAsStream("/dict.txt");
		BufferedReader br = null;
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line;
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null) {
				if(!line.equals("")){
					lines.add(line);
				}
			}
			if (shuffle) {
				Collections.shuffle(lines);
			}
			int len = (int) (lines.size() * ratio);
			for (int i = 0; i < len; i++) {
				String[] arr = lines.get(i).split("\t");
				if(arr.length==2){
					dicts.add(new StringIntPair(arr[0], Integer.valueOf(arr[1])));
				}else{
					dicts.add(new StringIntPair(arr[0], 1));
				}
			}
			for (int i = len; i < lines.size(); i++) {
				String[] arr = lines.get(i).split("\t");
				notInDicts.add(arr[0]);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				throw e;
			}
		}
	}
}

class StringIntPair {
	public String s;
	public int i;

	public StringIntPair(String s, int i) {
		this.s = s;
		this.i = i;
	}
}
 

package com.antbrains.datrie;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	
	@Test
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


	@Test
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
	
	@Test
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
	
	@Test
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
		System.out.println("trie size: base="+datrie.getBaseSize()+",check="+datrie.getCheckSize());
	}
	
 

	@Test
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
		System.out.println("trie size: base="+datrie.getBaseSize()+",check="+datrie.getCheckSize());
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

	private void loadData(List<StringIntPair> dicts, List<String> notInDicts,
			double ratio, boolean shuffle) throws Exception {
		InputStream is = getClass().getResourceAsStream("/dict.txt");
		BufferedReader br = null;
		ArrayList<String> lines = new ArrayList<String>();
		try {
			String line;
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null) {
				lines.add(line);
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
 

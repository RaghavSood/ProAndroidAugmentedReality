package com.paar.ch8;

public class SimpleTokenizer {
	String str = "";
	String delimiter = " ";
	int delimiterLength = delimiter.length();
	int i =0;
    int j =0;
	public final String getStr() {
		return str;
	}
	public final void setStr(String str) {
		this.str = str;
		i =0;
	    j =str.indexOf(delimiter);
	}
	public final String getDelimiter() {
		return delimiter;
	}
	public final void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		delimiterLength = delimiter.length();
	}
	public final boolean hasNext() {
		return j >= 0;
	}
	public final String next() {
		if(j >= 0) {
			String result = str.substring(i,j);
			i = j + 1;
			j = str.indexOf(delimiter, i); 
			return result;
		} else {
			return str.substring(i);
		}
	}
	public final String last() {
		return str.substring(i);
	}
	
	public final int delimOccurCount() {		
		int result = 0;
		if (delimiterLength > 0) {
			int start = str.indexOf(delimiter);
			while (start != -1) {
				result++;
				start = str.indexOf(delimiter, start + delimiterLength);
			}
		}
		return result;
	}
	
}
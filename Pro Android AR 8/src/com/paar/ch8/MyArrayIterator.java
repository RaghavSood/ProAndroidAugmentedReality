package com.paar.ch8;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayIterator implements Iterator {
	private Object array[];
	private int position = 0;

	public  MyArrayIterator(Object anArray[]) {
		array = anArray;
	}

	public boolean hasNext() {
		return position < array.length;
	}

	public Object next() throws NoSuchElementException {
		if (hasNext())
			return array[position++];
		else
			throw new NoSuchElementException();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
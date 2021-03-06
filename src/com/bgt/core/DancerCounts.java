/*  Copyright 2015 Bear Gulch Technologies, Inc.  All Rights Reserved.
 * 
 *  This file is part of SquareRotation.
 *
 *  SquareRotation is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SquareRotation is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SquareRotation.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.bgt.core;

import java.util.ArrayList;

public class DancerCounts {

	private ArrayList<ArrayList<Short>>count;
	
	public DancerCounts(int size)
	{
		count  = new ArrayList<ArrayList<Short>>(size);
		
		for(int ix = 0; ix < size; ix++) 
		{
			count.add(new ArrayList<Short>(size));
		    for(int jx = 0; jx < size; jx++) count.get (ix).add((short)0);
		}	
	}
	
	public void increment(int d0, int d1, short incr)
	{
		count.get(d0).set(d1, (short)(count.get(d0).get(d1) + incr));
		count.get(d1).set(d0, (short)(count.get(d1).get(d0) + incr));
	}
	
	public int size()
	{
		return count.size();
	}
	
	public void ensureCapacity(int newCapacity)
	{
		int oldCapacity = count.size();
		
		// this method is only useful for increasing capacity.  if
		// capacity is decreased, it is handled separately below in 
		// method remove().
		if(newCapacity > oldCapacity)
		{
			count. ensureCapacity(newCapacity);
			for(int ix = 0; ix < oldCapacity; ix++)
			{
				count. get(ix).ensureCapacity(newCapacity);
				for(int jx = oldCapacity; jx < newCapacity; jx++)
			    {
			    	count. get(ix).add((short)0);
			    }
			}
			for(int ix = oldCapacity; ix < newCapacity; ix++)
			{
				count .add(new ArrayList<Short>(newCapacity));
				for(int jx = 0; jx < newCapacity; jx++)
			    {
			    	count. get(ix).add((short)0);
			    }
			}
		}
	}
	
	public void remove(int dancer) 
	{ 
		for(int ix = 0; ix < count.size(); ix++) count.get(ix).remove(dancer);

		count.remove(dancer);
	}
	
	public short get(int ix, int jx)
	{
		//System.out.println("count size:  " + count.size() + ", count.get(0).size():  " + (count.size() > 0 ? count.get(0).size() : "n/a"));
		return count.get(ix).get(jx);
	}
	
	public int compare(int ix, int jx, int kx, int lx)
	{
		if(count.get(ix).get(jx) < count.get(kx).get(lx)) return -1;
		if(count.get(ix).get(jx) > count.get(kx).get(lx)) return  1;
		return 0;
	}
}

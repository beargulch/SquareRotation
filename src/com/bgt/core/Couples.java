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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Couples implements Serializable
{	
	private static final long serialVersionUID = 1L;

	private class Couple implements Serializable
	{
		private static final long serialVersionUID = 1L;
		private short   dancer0;
		private short   dancer1;
		private boolean selectedForSquare;
		
		private Couple() 
		{
			dancer0 = 0;
			dancer1 = 0;
			selectedForSquare = false;
		}
	}
	
	private ArrayList<Couple>couple;
	
	public Couples(int noOfCouples) 
	{
		// allocate the couple array and initialize with couples.
		
		couple = new ArrayList<Couple>(noOfCouples);	
		for(int ix = 0; ix < noOfCouples; ix++) couple.add(new Couple());
	}

	public void setDancer0(int coupleNo, short pDancer0)
	{
		couple.get(coupleNo).dancer0 = pDancer0;
	}
	
	public void setDancer1(int coupleNo, short pDancer1)
	{
		couple.get(coupleNo).dancer1 = pDancer1;
	}
	
	public void setSelectedForSquare(int coupleNo, boolean pSelectedForSquare)
	{
		couple.get(coupleNo).selectedForSquare = pSelectedForSquare;
	}

	public short getDancer0(int coupleNo)
	{
		// System.out.println("getDancer0, coupleNo = " + coupleNo + ", size of couple:  " + couple.size());
		return couple.get(coupleNo).dancer0;
	}
	
	public short getDancer1(int coupleNo)
	{
		// System.out.println("getDancer1, coupleNo = " + coupleNo + ", size of couple:  " + couple.size());
		return couple.get(coupleNo).dancer1;
	}
	
	public boolean getSelectedForSquare(int coupleNo)
	{
		return couple.get(coupleNo).selectedForSquare;
	}
	
	public void remove(int ix)
	{
		// System.out.println("Couples, removing coupleNo = " +ix);
		couple.remove(ix);
	}
	
	public void shuffle()
	{
		Collections.shuffle(couple);
	}

	public void clearCouplesUsedFlag()
	{
		// clear the third element in the couples array that is used to track whether the couple
		// has been used to form a square in a tip.
		for(int ix = 0; ix < couple.size(); ix++) couple.get(ix).selectedForSquare = false;
	}
	
	public short getNoOfCouples() 
	{
		return (short)couple.size();
	}

}

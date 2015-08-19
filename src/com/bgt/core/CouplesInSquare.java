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

import com.bgt.jtable.DancersJTable;

public class CouplesInSquare implements Serializable 
{
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ArrayList<Short>>coupleNo;
	
	public CouplesInSquare()
	{
		initializeCouplesInSquare();
	}
	
	public void initializeCouplesInSquare()
	{
		// we compute the expected number of squares by dividing the total number
		// of dancers by 8, and adding one.  since this does not take into account
		// dancers who are absent, and since it always adds 1 to the total, it 
		// should always be a little higher than the actual number of squares, 
		// which is fine for our purposes.
		
		int squaresPerTip = (DancersJTable.getInstance().getRowCount() / 8) + 1;	// rough estimate
		coupleNo = new ArrayList<ArrayList<Short>>(squaresPerTip);

		for(short sx = 0; sx < squaresPerTip; sx++)
		{
			ArrayList<Short>couplePositions = new ArrayList<Short>(4);
			for(short kx = 0; kx < 4; kx++) couplePositions.add(kx, (short) 0);
			coupleNo.add(couplePositions);
		}	
	}
	
	public short getCoupleNo(int square, int position)
	{
		return coupleNo.get(square).get(position);
	}

	public short getCoupleNo(short square, short position)
	{
		return coupleNo.get(square).get(position);
	}
	
	public void setCoupleNo(short square, short position, short pCoupleNo)
	{
		this.coupleNo.get(square).set(position, pCoupleNo);
	}
}

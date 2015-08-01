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

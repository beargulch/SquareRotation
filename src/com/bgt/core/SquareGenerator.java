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

// the SquareGenerator class is very tightly coupled to the Tip class.
//
// the SquareGenerator class invokes methods in the Tip class to select
// dancers and build couples.  see the Tip class for notes on how it builds
// couples.  
//
// the process of building couples in the Tip class populates the "couple" 
// ArrayList.  each element of the couple ArrayList is another ArrayList with
// 2 elements that represent the couple (2 dancer numbers that make up the
// the couple), plus a 3rd element that is a flag to indicate whether the 
// couple has been selected yet to be in a square.
//
// SquareGenerator iterates through the couple array, which is a member of 
// the Tip class, to build the couplesInSquare array, also in the Tip class.
// in its first pass at building squares, SquareGenerator looks for couples
// who have danced together the least number of times.  since couples are
// not static (some couples are built from singles, so are "new" couples
// each time squares are generated), SquareGenerator looks at the number of
// times individual dancers within a couple have danced with the individuals
// that make up each other couple.
//
// the first pass at building couples has no look-ahead capability; it just
// starts assembling squares by looking for couples that have danced with
// each other the fewest number of times.  that means that after the first 
// pass, it is possible that the dancers leftover for the last square have
// danced with each other more than desired, and that it would be possible to
// lower the overall danced-together count by moving some couples around.
// this is called grooming, and after the squares are built for a tip, the
// squares are groomed in an effort to make sure the arrangement of couples
// in squares minimizes the number of times individual dancers have danced
// together.  this process does not care about how many times the individuals
// that make up a given couple have danced together; that issue is dealt
// with in the process that makes up couples.  only counts between couples
// are considered when making up squares, or when grooming them.

public class SquareGenerator 
{	
	private short overallMaxCt = 0; 
	
	private static SquareGenerator instance = null;

	protected SquareGenerator() {}
	
	public static SquareGenerator getInstance()
	{
		if(instance == null)
		{
			System.out.println("instantiate SquareGenerator");
			instance = new SquareGenerator();
		}
		return instance;
	}

	public boolean generateNextTip()
	{	
		CoupleGenerator tip = CoupleGenerator.getInstance();
		
		if(tip.makeCouples())
		{
			tip.incrementTip();
			generateTip();
			groomTip();
			// for(short sx = 0; sx < tip.getNoOfSquares(); sx++) tip.computeDanceCounts(sx, true);
			tip.adjustCounts((short)+1);
			// printCountChart();
			return true;
		}
		return false;
	}
	
	public boolean regenerateCurrentTip()
	{			
		CoupleGenerator cplGen = CoupleGenerator.getInstance();
		if(cplGen.getCurrentTip() < 0) return false;
		
		cplGen.adjustCounts((short)-1);	// decrement the out counts, since this is a do-over.

		if(cplGen.makeCouples())	// make new couples, in case dancers have been added or modified
		{
			// printCountChart();
			generateTip();
			groomTip();
			// for(short sx = 0; sx < cplGen.getNoOfSquares(); sx++) cplGen.computeDanceCounts(sx, true);
			cplGen.adjustCounts((short)+1);
			// printCountChart();
			return true;
		}
		
		cplGen.adjustCounts((short)+1);	// increment the out counts to put us back where
		return false;			// we were before decrementing them above.
	}
	
	private void generateTip()
	{
		CoupleGenerator cplGen = CoupleGenerator.getInstance();
		cplGen.clearCouplesUsedFlag();
		
		// find and store the maximum number of times any dancer has danced with
		// any other dancer.
		short maxGoal = -1;
		for(int ix = 0; ix < cplGen.getDancerCt().size(); ix++)
		{
			for(int jx = 0; jx < cplGen.getDancerCt().size(); jx++)
			{
				if(jx == ix) continue;
				if(cplGen.getDancerCt().get(ix, jx) > maxGoal) maxGoal = cplGen.getDancerCt().get(ix, jx);
			}
		}
		
		// outside loop iterates through the squares in the tip
	    for(short sx = 0; sx < cplGen.getNoOfSquares(); sx++)
	    {
	        // cplCt is the number of couples selected; 4 couples makes a square, of course
	       
	        short cplCt = 0;

	        // goal is the number of times one couple has danced with
	        // another.  we start with a goal of zero, meaning we're
	        // looking for couples who have not yet danced with each
	        // other.  if that's not possible -- if we've danced with
	        // everyone at least once -- then we increment goal and
	        // try again.  we keep incrementing goal until we fill
	        // the square.
	        short goal = 0;
	        
	        while(cplCt < 4)	// need 4 couples for a square
	        {
	            // cx iterates through all couples

	            for(short cx = 0;  cx < cplGen.getNoOfCouples() && cplCt < 4; cx++)
	            {
	                // has this couple been selected for a square yet in this tip?
	                if(!cplGen.getCouples().getSelectedForSquare(cx))
	                {
	                    // is this the first couple in the square?
	                    if(cplCt == 0)
	                    {
	                        addCoupleToSquare(sx, cx, cplCt);
	                        cplCt += 1;
	                        continue;
	                    }

	                    // not the first couple, so let's check to see if the dancers
	                    // already selected for this square have danced with the dancers
	                    // in this couple before.  we start out assuming they have not
	                    // by setting useCouple = 1, where "useCouple" means use this
	                    // couple in the square.

	                    boolean useCouple = true;

	                    // dc0 is dancer 0 of the couple we're considering adding to the square
	                    // dc1 is dancer 1 of the couple we're considering adding to the square

	                    short dc0 = cplGen.getCouples().getDancer0(cx);
	                    short dc1 = cplGen.getCouples().getDancer1(cx);

	                    // iterate over the couples currently selected for this square

	                    for(short cn = 0; cn < cplCt; cn++)
	                    {
	                        // dt0 is dancer 0 of the current couple in the square we're going to compare
	                        //     against the couple we're considering adding to the square
	                        // dt1 is dancer 1 of the current couple in the square we're going to compare
	                        //     against the couple we're considering adding to the square

	                        short cplNo = cplGen.getCouplesInSquare().getCoupleNo(sx, cn);
	                        short dt0   = cplGen.getCouples().getDancer0(cplNo);
	                        short dt1   = cplGen.getCouples().getDancer1(cplNo);
	                    
	                        // if either of the dancers we're considering adding to the square
	                        // have danced with either of the current dancers already in the square
	                        // more than the "$goal" number, we don't select this couple for the
	                        // square ($useCouple = 0).

	                        if(cplGen.getDancerCt().get(dt0, dc0) > goal || cplGen.getDancerCt().get(dt0, dc1) > goal ||
	                           cplGen.getDancerCt().get(dt1, dc0) > goal || cplGen.getDancerCt().get(dt1, dc1) > goal)
	                        {
	                            useCouple = false;
	                            break;
	                        }
	                    }

	                    // did the dancers in this couple survive the selection process?

	                    if(useCouple)
	                    {
	                        addCoupleToSquare(sx, cx, cplCt);
	                        cplCt += 1;
	                    }
	                }
	            }
	            
	            if(cplCt < 4) {
	                goal += 1;
	            }
	            if(goal > maxGoal) break;	// we're done; no
	        }

	        // now we go around the square, and increment the
	        // number of times each dancer in the square has danced
	        // with every other dancer in the square.

	        cplGen.computeDanceCounts(sx, (short)+1);
	    }
	}
	
	private void groomTip()
	{
		CoupleGenerator cplGen = CoupleGenerator.getInstance();

		this.overallMaxCt = cplGen.getMaxActual();
	    if(this.overallMaxCt < 1) return;
	    
	    boolean done 	  = false;
	    boolean foundMove = false;
	    
	    while(!done)
	    {
	    	foundMove = false;
	    	for(short s0 = 0; s0 < cplGen.getNoOfSquares(); s0++)
	    	{
	    		// get the max count between all dancers in this square , as indicated by -1
	    		short squareMaxCt = getSquareMaxCt(s0, (short)-1);
	    		
	    		// is the count for this square already below the overall max?
	    		if(squareMaxCt < this.overallMaxCt) continue;	// count is less than max; skip
	    	
	    		for(short psn0 = 0; psn0 < 4; psn0++)
	    		{
	    			// get the max count with the dancers in couple at psn0 removed from consideration
	    			short reducedMaxCt = getSquareMaxCt(s0, psn0);

	    			// will moving the couple at this position reduce the square max count?
	    			if(reducedMaxCt >= squareMaxCt) continue;	// will not reduce the count; skip
	    		
	    			for(short s1 = 0; s1 < cplGen.getNoOfSquares(); s1++)
	    			{
	    				if(s1 == s0) continue;	// same square; skip
	    			
	    				for(short psn1 = 0; psn1 < 4; psn1++)
	    				{
	    					// does moving from source to target work?
	    					if(checkIfMoveWorks(s0, psn0, s1, psn1, reducedMaxCt))		
		    				{
	    						// does moving from target back to source work?
		    					if(checkIfMoveWorks(s1, psn1, s0, psn0, reducedMaxCt))	
		    					{
		    						doTheMove(s0, psn0, s1, psn1);	// do it!
		    						foundMove = true;
		    					}
		    				}
	    				}
	    			}
	    		}
	    	}
	    	if(!foundMove) done = true;
	    }
	}
	
	private short getSquareMaxCt(short square, short position)
	{
		// find the maximum dancerCt in a square.  if the value of "position" passed to this method
		// has a valid value (0 - 3), the couple at that position is eliminated from consideration, 
		// so as to return the maximum dancerCt as if that couple had been removed from the square.
		
		// System.out.println("getSquareMaxCt for square " + square + ", position = " + position);
		
		CoupleGenerator cplGen = CoupleGenerator.getInstance();
		short max = -1;
		
		for(int psn0 = 0; psn0 < 4; psn0++)
		{
			if(psn0 == position) continue;	// eliminate this couple?
			
			short cp0 = cplGen.getCouplesInSquare().getCoupleNo(square, psn0);
	   		short d00 = cplGen.getCouples().getDancer0(cp0);
	   		short d01 = cplGen.getCouples().getDancer1(cp0);
	   		
		 	for(int psn1 = (psn0+1); psn1 < 4; psn1++)
		   	{	
		 		// System.out.println("getSquareMaxCt for square " + square + ", examining position = " + psn1);
		 		if(psn1 == position) continue;	// eliminate this couple?
		 		
		   		//short cp1 = Tip.getInstance().getCouplesInSquare().get(square).get(psn1);
		   		//short d10 = Tip.getInstance().getCouple().get(cp1).get(0);
		   		//short d11 = Tip.getInstance().getCouple().get(cp1).get(1);
		 		
		   		short cp1 = cplGen.getCouplesInSquare().getCoupleNo(square, psn1);
		   		short d10 = cplGen.getCouples().getDancer0(cp1);
		   		short d11 = cplGen.getCouples().getDancer1(cp1);
		    		
		   		if(cplGen.getDancerCt().get(d00, d10) > max) max = cplGen.getDancerCt().get(d00, d10);
		   		if(cplGen.getDancerCt().get(d01, d10) > max) max = cplGen.getDancerCt().get(d01, d10);
		   		if(cplGen.getDancerCt().get(d00, d11) > max) max = cplGen.getDancerCt().get(d00, d11);
		   		if(cplGen.getDancerCt().get(d01, d11) > max) max = cplGen.getDancerCt().get(d01, d11);
		   	}
		}	
		//System.out.printf("square %d:  %2d %2d %2d %2d  maxDanceCt:  %2d  position:  %2d\n", 
		//		           square, cplGen.getCouplesInSquare().get(square).get(0), 
		//		                   cplGen.getCouplesInSquare().get(square).get(1), 
		//		                   cplGen.getCouplesInSquare().get(square).get(2), 
		//		                   cplGen.getCouplesInSquare().get(square).get(3), max, position);
		return max;
	}
	
	private boolean checkIfMoveWorks(short sourceSquare, short sourcePsn, short targetSquare, short targetPsn, short maxCt)
	{
		CoupleGenerator cplGen = CoupleGenerator.getInstance();
		boolean moveWorks = true;	// start assuming it works, then try to falsify
		
		short sourceCpl = cplGen.getCouplesInSquare().getCoupleNo(sourceSquare, sourcePsn);
        short ds0 = cplGen.getCouples().getDancer0(sourceCpl); // source dancer 0
        short ds1 = cplGen.getCouples().getDancer1(sourceCpl); // source dancer 1

        // with the selected couple from the source square, iterate over the target
        // square to see if it could be moved without exceeding maxCt

        for(short psn = 0; psn < 4; psn++) 
        {
        	if(psn == targetPsn) continue;	// this is the couple that would be moved out, so we don't
            								// care how many times they've danced with the source couple
            short cpl = cplGen.getCouplesInSquare().getCoupleNo(targetSquare, psn);
            short dt0 = cplGen.getCouples().getDancer0(cpl);
            short dt1 = cplGen.getCouples().getDancer1(cpl);
            
            if((cplGen.getDancerCt().get(dt0, ds0) + 1) > maxCt ||
               (cplGen.getDancerCt().get(dt0, ds1) + 1) > maxCt ||
               (cplGen.getDancerCt().get(dt1, ds0) + 1) > maxCt ||
               (cplGen.getDancerCt().get(dt1, ds1) + 1) > maxCt)
            {
                moveWorks = false;
                //System.out.println("moving from source square " + sourceSquare + ", psn " + sourcePsn + 
                //				   " to target square " + targetSquare + ", psn " + targetPsn + " did not work, maxCt = " + maxCt+ " counts: " +
                //				   (cplGen.getDancerCt().get(dt0).get(ds0) + 1) + " / " +
                //				   (cplGen.getDancerCt().get(dt0).get(ds1) + 1) + " / " +
                //				   (cplGen.getDancerCt().get(dt1).get(ds0) + 1) + " / " +
                //				   (cplGen.getDancerCt().get(dt1).get(ds1) + 1) + ", psn = " + psn);
                break;
            }
            //else
            //{
            //	System.out.println("moving from source square " + sourceSquare + ", psn " + sourcePsn + 
            //					   " to target square " + targetSquare + ", psn " + targetPsn + " WORKS, maxCt = " + maxCt + " counts: " +
            //					   (cplGen.getDancerCt().get(dt0).get(ds0) + 1) + " / " +
            //					   (cplGen.getDancerCt().get(dt0).get(ds1) + 1) + " / " +
            //					   (cplGen.getDancerCt().get(dt1).get(ds0) + 1) + " / " +
            //					   (cplGen.getDancerCt().get(dt1).get(ds1) + 1) + ", psn = " + psn);
            //}
        }
		return moveWorks;
	}
	
	private void doTheMove(short sourceSquare, short sourcePsn, short targetSquare, short targetPsn)
	{
		CoupleGenerator cplGen = CoupleGenerator.getInstance();
        
	    // System.out.println("Source square before: " + sourceSquare);
	    // printSquares(sourceSquare);
	    // System.out.println("Target square before: " + targetSquare);
	    // printSquares(targetSquare);
	        	
	    // get couple numbers being swapped between source and target squares
	    short sourceCplToMove = cplGen.getCouplesInSquare().getCoupleNo(sourceSquare, sourcePsn);
	    short targetCplToMove = cplGen.getCouplesInSquare().getCoupleNo(targetSquare, targetPsn);
	           
	    // sourceSquare    is the square we're moving from
	    // sourcePsn       is the position of the couple in the source square being moved
	    // sourceCplToMove is the couple number of the couple in the source square being moved
	    // targetSquare    is the square we found to move to
	    // targetPsn       is the position of the couple in the target square being swapped with the source couple
	    // targetCplToMove is the couple number of the couple in the target square being swapped with the source couple
	            
	    //System.out.println("DOING MOVE.  from source square " + sourceSquare + ", swapping couple " + sourceCplToMove + 
	    //				   " at position " + sourcePsn + " to square " + targetSquare + ", couple " + targetCplToMove + 
	    //				   " at position " + targetPsn);
	     
	    // move source to target
	    cplGen.computeDanceCounts(targetSquare, (short)-1);  				// decrement counts in square targetSquare before move
        addCoupleToSquare(targetSquare, sourceCplToMove, targetPsn);	// move in new couple from square sourceSquare
        cplGen.computeDanceCounts(targetSquare, (short)+1);  				// increment counts in square targetSquare after move
        
        // move target to source
        cplGen.computeDanceCounts(sourceSquare, (short)-1);  				// decrement counts in square sourceSquare before move
        addCoupleToSquare(sourceSquare, targetCplToMove, sourcePsn);	// move in new couple from square targetSquare
        cplGen.computeDanceCounts(sourceSquare, (short)+1);  				// increment counts in square sourceSquare after move
	            
        // System.out.println("Source square after: " + sourceSquare);
        // printSquares(sourceSquare);
	    // System.out.println("Target square after: " + targetSquare);
	    // printSquares(targetSquare);    
	}
	
	private void addCoupleToSquare(short square, short coupleNo, short cplPsn)
	{
		CoupleGenerator cplGen = CoupleGenerator.getInstance();
		
		// System.out.println("in addCoupleToSquare, square = " + square + ", couple = " + coupleNo + ", cplPsn = " + cplPsn);

	    cplGen.getCouplesInSquare().setCoupleNo(square, cplPsn, coupleNo);	// store the couple number at the indicated position in the square,
	    cplGen.getCouples().setSelectedForSquare(coupleNo, true);			// and mark this couple as used.
	} 
/*	
	private void printCountChart()
	{
		CoupleGenerator cplGen = CoupleGenerator.getInstance();
		Vector<Vector<Object>>dancerVector = Globals.getInstance().getDancersTableModel().getDataVector();
  
		printSquares((short)-1);
		
	    int max  = -1;
        int ixsv = -1;
        int jxsv = -1;
        int pfnd = 0;
        
        // dancerCt printout
        System.out.print("  ");
        for(int ix = 0; ix < cplGen.getDancerCt().size(); ix++)
        {
        	System.out.print("  " + ix%10 + " ");	// header
        }
        System.out.println("");
        max = -1;
        for(int ix = 0; ix < cplGen.getDancerCt().size(); ix++)
        {   
            System.out.print(ix%10 + " ");
        	for(int jx = 0; jx < cplGen.getDancerCt().size(); jx++)
        	{
        		if(cplGen.getDancerCt().get(ix, jx) > max)
        		{
        			max  = cplGen.getDancerCt().get(ix, jx);
        			ixsv = ix;
        			jxsv = jx;
        		}
        		if(ix == jx)
        			System.out.print(" -  "); 
        		else
        			if((Integer)dancerVector.get(ix).get(Dancer.PARTNER_IX) == jx)
        			{
        				System.out.print(" P  ");
        				pfnd += 1;
        			}
        			else
        			if((Integer)dancerVector.get(ix).get(Dancer.PARTNER_IX) < 0)
        				if((Integer)dancerVector.get(jx).get(Dancer.PARTNER_IX) < 0)
        					System.out.print("ss" + cplGen.getDancerCt().get(ix, jx) + " ");
        				else
        					System.out.print("sc" + cplGen.getDancerCt().get(ix, jx) + " ");
        			else
        				if((Integer)dancerVector.get(jx).get(Dancer.PARTNER_IX) < 0)
        					System.out.print("cs" + cplGen.getDancerCt().get(ix, jx) + " ");
        				else
        					System.out.print("cc" + cplGen.getDancerCt().get(ix, jx) + " ");
        	}
        	System.out.println("");
        }
        System.out.println("Partners found:  " + pfnd);
        System.out.println("current max dancer count of " + max + " first detected between dancer " + ixsv + " and dancer " + jxsv);
	}
	
	private void printSquares(short square)
	{
		CoupleGenerator cplGen = CoupleGenerator.getInstance();
		
		short bgn = 0;
		short end = cplGen.getNoOfSquares();
		
		if(square > -1)
		{
			bgn = square;
			end = (short)(square + 1);
		}
	    // inside loop goes through all squares in the tip
	    for(short sx = bgn; sx < end; sx++)
	    {
	    	// System.out.println("tx = " + tx + ", sx = " + sx);
	        // pull out couple numbers into cp0, c1, c2, c3
	    	short[] cp = new short[4];
	        cp[0] = cplGen.getCouplesInSquare().getCoupleNo(sx, 0);
	        cp[1] = cplGen.getCouplesInSquare().getCoupleNo(sx, 1);
	        cp[2] = cplGen.getCouplesInSquare().getCoupleNo(sx, 2);
	        cp[3] = cplGen.getCouplesInSquare().getCoupleNo(sx, 3);
	        
	        short[][]dnc = new short[4][2];
	        
	        int max = -1;
	        // pull out the dancer numbers in this square into the dnc array, organized
	        // by position within the square.
	        for(int ix = 0; ix < 4; ix++)
	        {
	        	try
	        	{
	        		dnc[ix][0] = cplGen.getCouples().getDancer0(cp[ix]);
	        		dnc[ix][1] = cplGen.getCouples().getDancer1(cp[ix]);
	        	}
	        	catch(IndexOutOfBoundsException e)
	        	{
	        		System.out.println("(1) index out of bounds:  cp[" + ix + "] = " + cp[ix]);
	        	}
	        }
	        // go around the square and determine the highest count of dancers dancing
	        // with each other
	        for(int ix = 0; ix < 4; ix++)
	        {
	        	for(int jx = ix+1; jx < 4; jx++)
	        	{
	        		try
	        		{
	        			if(cplGen.getDancerCt().get(dnc[ix][0], dnc[jx][0]) > max) 
	        				max = cplGen.getDancerCt().get(dnc[ix][0], dnc[jx][0]);
	        			if(cplGen.getDancerCt().get(dnc[ix][1], dnc[jx][0]) > max) 
	        				max = cplGen.getDancerCt().get(dnc[ix][1], dnc[jx][0]);
	        			if(cplGen.getDancerCt().get(dnc[ix][0], dnc[jx][1]) > max) 
	        				max = cplGen.getDancerCt().get(dnc[ix][0], dnc[jx][1]);
	        			if(cplGen.getDancerCt().get(dnc[ix][1], dnc[jx][1]) > max) 
	        				max = cplGen.getDancerCt().get(dnc[ix][1], dnc[jx][1]);
	        		}
		        	catch(IndexOutOfBoundsException e)
		        	{
		        		System.out.println("(2a) index out of bounds:  dnc[" + ix + "][0] = " + dnc[ix][0] + ", dnc[" + ix + "][1] = " + dnc[ix][1]);
		        		System.out.println("(2b) index out of bounds:  dnc[" + jx + "][0] = " + dnc[jx][0] + ", dnc[" + jx + "][1] = " + dnc[jx][1]);
		        	}
	        		
	        	}
	        }
	            
	        System.out.printf("square %d:  %2d %2d %2d %2d  maxDanceCt:  %2d\n", sx, cp[0], cp[1], cp[2], cp[3], max);
	            
	        // print:  cplGen number, square number, couples in the square
	        System.out.printf("square %d:  %2d %2d %2d %2d (%2d %2d), (%2d %2d), (%2d %2d), (%2d %2d)\n",
	        					sx, cp[0], cp[1], cp[2], cp[3], 
	        					cplGen.getCouples().getDancer0(cp[0]), cplGen.getCouples().getDancer1(cp[0]), 
	        					cplGen.getCouples().getDancer0(cp[1]), cplGen.getCouples().getDancer1(cp[1]), 
	        					cplGen.getCouples().getDancer0(cp[2]), cplGen.getCouples().getDancer1(cp[2]), 
	        					cplGen.getCouples().getDancer0(cp[3]), cplGen.getCouples().getDancer1(cp[3]));        
	    }
	}
	*/
}
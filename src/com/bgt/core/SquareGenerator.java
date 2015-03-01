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

import java.util.Vector;

public class SquareGenerator 
{	
	public SquareGenerator() {}
	private short overallMaxCt = 0; 

	public boolean generateNextTip()
	{	
		Tip tip = Globals.getInstance().getTip();
		
		if(tip.makeCouples())
		{
			tip.incrementTip();
			generateTip();
			groomTip();
			// for(short sx = 0; sx < tip.getNoOfSquares(); sx++) tip.computeDanceCounts(sx, true);
			tip.adjustCounts(+1);
			printCountChart();
			return true;
		}
		return false;
	}
	
	public boolean regenerateCurrentTip()
	{			
		Tip tip = Globals.getInstance().getTip();
		if(tip.getCurrentTip() < 0) return false;
		
		tip.adjustCounts(-1);	// decrement the out counts, since this is a do-over.

		if(tip.makeCouples())	// make new couples, in case dancers have been added or modified
		{
			printCountChart();
			generateTip();
			groomTip();
			// for(short sx = 0; sx < tip.getNoOfSquares(); sx++) tip.computeDanceCounts(sx, true);
			tip.adjustCounts(+1);
			printCountChart();
			return true;
		}
		
		tip.adjustCounts(+1);	// increment the out counts to put us back where
		return false;			// we were before decrementing them above.
	}
	
	private void generateTip()
	{
		Tip tip = Globals.getInstance().getTip();
		tip.clearCouplesUsedFlag();
		
		// find and store the maximum number of times any dancer has danced with
		// any other dancer.
		short maxGoal = -1;
		for(int ix = 0; ix < tip.getDancerCt().size(); ix++)
		{
			for(int jx = 0; jx < tip.getDancerCt().size(); jx++)
			{
				if(jx == ix) continue;
				if(tip.getDancerCt().get(ix).get(jx) > maxGoal) maxGoal = tip.getDancerCt().get(ix).get(jx);
			}
		}
		
		// outside loop iterates through the squares in the tip
	    for(short sx = 0; sx < tip.getNoOfSquares(); sx++)
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

	            for(short cx = 0;  cx < tip.getNoOfCouples() && cplCt < 4; cx++)
	            {
	                // has this couple been selected for a square yet in this tip?
	                if(tip.getCouple().get(cx).get(2) == 0)
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

	                    short dc0 = tip.getCouple().get(cx).get(0);
	                    short dc1 = tip.getCouple().get(cx).get(1);

	                    // iterate over the couples currently selected for this square

	                    for(short cn = 0; cn < cplCt; cn++)
	                    {
	                        // dt0 is dancer 0 of the current couple in the square we're going to compare
	                        //     against the couple we're considering adding to the square
	                        // dt1 is dancer 1 of the current couple in the square we're going to compare
	                        //     against the couple we're considering adding to the square

	                        short cplNo = tip.getCouplesInSquare().get(sx).get(cn);
	                        short dt0   = tip.getCouple().get(cplNo).get(0);
	                        short dt1   = tip.getCouple().get(cplNo).get(1);
	                    
	                        // if either of the dancers we're considering adding to the square
	                        // have danced with either of the current dancers already in the square
	                        // more than the "$goal" number, we don't select this couple for the
	                        // square ($useCouple = 0).

	                        if(tip.getDancerCt().get(dt0).get(dc0) > goal || tip.getDancerCt().get(dt0).get(dc1) > goal ||
	                           tip.getDancerCt().get(dt1).get(dc0) > goal || tip.getDancerCt().get(dt1).get(dc1) > goal)
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

	        tip.computeDanceCounts(sx, true);
	    }
	}
	
	private void groomTip()
	{
		Tip tip = Globals.getInstance().getTip();

		this.overallMaxCt = tip.getMaxActual();
	    if(this.overallMaxCt < 1) return;
	    
	    boolean done 	  = false;
	    boolean foundMove = false;
	    
	    while(!done)
	    {
	    	foundMove = false;
	    	for(short s0 = 0; s0 < tip.getNoOfSquares(); s0++)
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
	    		
	    			for(short s1 = 0; s1 < tip.getNoOfSquares(); s1++)
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
		
		System.out.println("getSquareMaxCt for square " + square + ", position = " + position);
		
		Tip tip = Globals.getInstance().getTip();
		short max = -1;
		
		for(int psn0 = 0; psn0 < 4; psn0++)
		{
			if(psn0 == position) continue;	// eliminate this couple?
			
			short cp0 = tip.getCouplesInSquare().get(square).get(psn0);
	   		short d00 = tip.getCouple().get(cp0).get(0);
	   		short d01 = tip.getCouple().get(cp0).get(1);
	   		
		 	for(int psn1 = (psn0+1); psn1 < 4; psn1++)
		   	{	
		 		System.out.println("getSquareMaxCt for square " + square + ", examining position = " + psn1);
		 		if(psn1 == position) continue;	// eliminate this couple?
		 		
		   		//short cp1 = Tip.getInstance().getCouplesInSquare().get(square).get(psn1);
		   		//short d10 = Tip.getInstance().getCouple().get(cp1).get(0);
		   		//short d11 = Tip.getInstance().getCouple().get(cp1).get(1);
		 		
		   		short cp1 = tip.getCouplesInSquare().get(square).get(psn1);
		   		short d10 = tip.getCouple().get(cp1).get(0);
		   		short d11 = tip.getCouple().get(cp1).get(1);
		    		
		   		if(tip.getDancerCt().get(d00).get(d10) > max) max = tip.getDancerCt().get(d00).get(d10);
		   		if(tip.getDancerCt().get(d01).get(d10) > max) max = tip.getDancerCt().get(d01).get(d10);
		   		if(tip.getDancerCt().get(d00).get(d11) > max) max = tip.getDancerCt().get(d00).get(d11);
		   		if(tip.getDancerCt().get(d01).get(d11) > max) max = tip.getDancerCt().get(d01).get(d11);
		   	}
		}	
		//System.out.printf("square %d:  %2d %2d %2d %2d  maxDanceCt:  %2d  position:  %2d\n", 
		//		           square, tip.getCouplesInSquare().get(square).get(0), 
		//		                   tip.getCouplesInSquare().get(square).get(1), 
		//		                   tip.getCouplesInSquare().get(square).get(2), 
		//		                   tip.getCouplesInSquare().get(square).get(3), max, position);
		return max;
	}
	
	private boolean checkIfMoveWorks(short sourceSquare, short sourcePsn, short targetSquare, short targetPsn, short maxCt)
	{
		Tip tip = Globals.getInstance().getTip();
		boolean moveWorks = true;	// start assuming it works, then try to falsify
		
		short sourceCpl = tip.getCouplesInSquare().get(sourceSquare).get(sourcePsn);
        short ds0 = tip.getCouple().get(sourceCpl).get(0); // source dancer 0
        short ds1 = tip.getCouple().get(sourceCpl).get(1); // source dancer 1

        // with the selected couple from the source square, iterate over the target
        // square to see if it could be moved without exceeding maxCt

        for(short psn = 0; psn < 4; psn++) 
        {
        	if(psn == targetPsn) continue;	// this is the couple that would be moved out, so we don't
            								// care how many times they've danced with the source couple
            short cpl = tip.getCouplesInSquare().get(targetSquare).get(psn);
            short dt0 = tip.getCouple().get(cpl).get(0);
            short dt1 = tip.getCouple().get(cpl).get(1);
            
            if((tip.getDancerCt().get(dt0).get(ds0) + 1) > maxCt ||
               (tip.getDancerCt().get(dt0).get(ds1) + 1) > maxCt ||
               (tip.getDancerCt().get(dt1).get(ds0) + 1) > maxCt ||
               (tip.getDancerCt().get(dt1).get(ds1) + 1) > maxCt)
            {
                moveWorks = false;
                //System.out.println("moving from source square " + sourceSquare + ", psn " + sourcePsn + 
                //				   " to target square " + targetSquare + ", psn " + targetPsn + " did not work, maxCt = " + maxCt+ " counts: " +
                //				   (tip.getDancerCt().get(dt0).get(ds0) + 1) + " / " +
                //				   (tip.getDancerCt().get(dt0).get(ds1) + 1) + " / " +
                //				   (tip.getDancerCt().get(dt1).get(ds0) + 1) + " / " +
                //				   (tip.getDancerCt().get(dt1).get(ds1) + 1) + ", psn = " + psn);
                break;
            }
            //else
            //{
            //	System.out.println("moving from source square " + sourceSquare + ", psn " + sourcePsn + 
            //					   " to target square " + targetSquare + ", psn " + targetPsn + " WORKS, maxCt = " + maxCt + " counts: " +
            //					   (tip.getDancerCt().get(dt0).get(ds0) + 1) + " / " +
            //					   (tip.getDancerCt().get(dt0).get(ds1) + 1) + " / " +
            //					   (tip.getDancerCt().get(dt1).get(ds0) + 1) + " / " +
            //					   (tip.getDancerCt().get(dt1).get(ds1) + 1) + ", psn = " + psn);
            //}
        }
		return moveWorks;
	}
	
	private void doTheMove(short sourceSquare, short sourcePsn, short targetSquare, short targetPsn)
	{
		Tip tip = Globals.getInstance().getTip();
        
	    System.out.println("Source square before: " + sourceSquare);
	    printSquares(sourceSquare);
	    System.out.println("Target square before: " + targetSquare);
	    printSquares(targetSquare);
	        	
	    // get couple numbers being swapped between source and target squares
	    short sourceCplToMove = tip.getCouplesInSquare().get(sourceSquare).get(sourcePsn);
	    short targetCplToMove = tip.getCouplesInSquare().get(targetSquare).get(targetPsn);
	           
	    // sourceSquare    is the square we're moving from
	    // sourcePsn       is the position of the couple in the source square being moved
	    // sourceCplToMove is the couple number of the couple in the source square being moved
	    // targetSquare    is the square we found to move to
	    // targetPsn       is the position of the couple in the target square being swapped with the source couple
	    // targetCplToMove is the couple number of the couple in the target square being swapped with the source couple
	            
	    System.out.println("DOING MOVE.  from source square " + sourceSquare + ", swapping couple " + sourceCplToMove + 
	    				   " at position " + sourcePsn + " to square " + targetSquare + ", couple " + targetCplToMove + 
	    				   " at position " + targetPsn);
	     
	    // move source to target
	    tip.computeDanceCounts(targetSquare, false);  					// decrement counts in square targetSquare before move
        addCoupleToSquare(targetSquare, sourceCplToMove, targetPsn);	// move in new couple from square sourceSquare
        tip.computeDanceCounts(targetSquare, true);  					// increment counts in square targetSquare after move
        
        // move target to source
        tip.computeDanceCounts(sourceSquare, false);  					// decrement counts in square sourceSquare before move
        addCoupleToSquare(sourceSquare, targetCplToMove, sourcePsn);	// move in new couple from square targetSquare
        tip.computeDanceCounts(sourceSquare, true);  					// increment counts in square sourceSquare after move
	            
        System.out.println("Source square after: " + sourceSquare);
        printSquares(sourceSquare);
	    System.out.println("Targer square after: " + targetSquare);
	    printSquares(targetSquare);    
	}
	
	private void addCoupleToSquare(short square, short couple, short cplPsn)
	{
		Tip tip = Globals.getInstance().getTip();
		
		System.out.println("in addCoupleToSquare, square = " + square + ", couple = " + couple + ", cplPsn = " + cplPsn);

	    tip.getCouplesInSquare().get(square).set(cplPsn, couple);		// store the couple number,
	    tip.getCouple().get(couple).set(2, (short)1);					// mark this couple as used.
	} 
	
	private void printCountChart()
	{
		Tip tip = Globals.getInstance().getTip();
		Vector<Vector<Object>>dancerVector = Globals.getInstance().getDancersTableModel().getDataVector();
  
		printSquares((short)-1);
		
	    int max  = -1;
        int ixsv = -1;
        int jxsv = -1;
        int pfnd = 0;
        
        /* dancerCt printout */
        System.out.print("  ");
        for(int ix = 0; ix < tip.getDancerCt().size(); ix++)
        {
        	System.out.print("  " + ix%10 + " ");	// header
        }
        System.out.println("");
        max = -1;
        for(int ix = 0; ix < tip.getDancerCt().size(); ix++)
        {   
            System.out.print(ix%10 + " ");
        	for(int jx = 0; jx < tip.getDancerCt().size(); jx++)
        	{
        		if(tip.getDancerCt().get(ix).get(jx) > max)
        		{
        			max  = tip.getDancerCt().get(ix).get(jx);
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
        					System.out.print("ss" + tip.getDancerCt().get(ix).get(jx) + " ");
        				else
        					System.out.print("sc" + tip.getDancerCt().get(ix).get(jx) + " ");
        			else
        				if((Integer)dancerVector.get(jx).get(Dancer.PARTNER_IX) < 0)
        					System.out.print("cs" + tip.getDancerCt().get(ix).get(jx) + " ");
        				else
        					System.out.print("cc" + tip.getDancerCt().get(ix).get(jx) + " ");
        	}
        	System.out.println("");
        }
        System.out.println("Partners found:  " + pfnd);
        System.out.println("current max dancer count of " + max + " first detected between dancer " + ixsv + " and dancer " + jxsv);
        /**/
	}
	
	private void printSquares(short square)
	{
		Tip tip = Globals.getInstance().getTip();
		
		short bgn = 0;
		short end = tip.getNoOfSquares();
		
		if(square > -1)
		{
			bgn = square;
			end = (short)(square + 1);
		}
	    // inside loop goes through all squares in the tip
	    for(short sx = bgn; sx < end; sx++)
	    {
	    	// System.out.println("tx = " + tx + ", sx = " + sx);
	        // pull out couple numbers into c0, c1, c2, c3
	    	short[] cp = new short[4];
	        short c0 = tip.getCouplesInSquare().get(sx).get(0);
	        short c1 = tip.getCouplesInSquare().get(sx).get(1);
	        short c2 = tip.getCouplesInSquare().get(sx).get(2);
	        short c3 = tip.getCouplesInSquare().get(sx).get(3);

	        cp[0] = tip.getCouplesInSquare().get(sx).get(0);
	        cp[1] = tip.getCouplesInSquare().get(sx).get(1);
	        cp[2] = tip.getCouplesInSquare().get(sx).get(2);
	        cp[3] = tip.getCouplesInSquare().get(sx).get(3);
	        
	        short[][]dnc = new short[4][2];
	        
	        int max = -1;
	        // pull out the dancer numbers in this square into the dnc array, organized
	        // by position within the square.
	        for(int ix = 0; ix < 4; ix++)
	        {
	        	try
	        	{
	        		dnc[ix][0] = tip.getCouple().get(cp[ix]).get(0);
	        		dnc[ix][1] = tip.getCouple().get(cp[ix]).get(1);
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
	        			if(tip.getDancerCt().get(dnc[ix][0]).get(dnc[jx][0]) > max) 
	        				max = tip.getDancerCt().get(dnc[ix][0]).get(dnc[jx][0]);
	        			if(tip.getDancerCt().get(dnc[ix][1]).get(dnc[jx][0]) > max) 
	        				max = tip.getDancerCt().get(dnc[ix][1]).get(dnc[jx][0]);
	        			if(tip.getDancerCt().get(dnc[ix][0]).get(dnc[jx][1]) > max) 
	        				max = tip.getDancerCt().get(dnc[ix][0]).get(dnc[jx][1]);
	        			if(tip.getDancerCt().get(dnc[ix][1]).get(dnc[jx][1]) > max) 
	        				max = tip.getDancerCt().get(dnc[ix][1]).get(dnc[jx][1]);
	        		}
		        	catch(IndexOutOfBoundsException e)
		        	{
		        		System.out.println("(2a) index out of bounds:  dnc[" + ix + "][0] = " + dnc[ix][0] + ", dnc[" + ix + "][1] = " + dnc[ix][1]);
		        		System.out.println("(2b) index out of bounds:  dnc[" + jx + "][0] = " + dnc[jx][0] + ", dnc[" + jx + "][1] = " + dnc[jx][1]);
		        	}
	        		
	        	}
	        }
	            
	        System.out.printf("square %d:  %2d %2d %2d %2d  maxDanceCt:  %2d\n", sx, c0, c1, c2, c3, max);
	            
	        // print:  tip number, square number, couples in the square
	        System.out.printf("square %d:  %2d %2d %2d %2d (%2d %2d), (%2d %2d), (%2d %2d), (%2d %2d)\n",
	                    sx, c0, c1, c2, c3, tip.getCouple().get(c0).get(0), tip.getCouple().get(c0).get(1), 
	                                        tip.getCouple().get(c1).get(0), tip.getCouple().get(c1).get(1), 
	                                        tip.getCouple().get(c2).get(0), tip.getCouple().get(c2).get(1), 
	                                        tip.getCouple().get(c3).get(0), tip.getCouple().get(c3).get(1));        
	    }
	}
}
package com.bgt.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Vector;

import com.bgt.jtable.DancersJTable;

// after the squares have been generated, the TipDisplay class is responsible
// for putting together the Tip display that shows who is dancing, what the
// couple makeup is (important for singles), and who is assigned to what square.

public class TipDisplay {
	
	private ArrayList<ArrayList<Object>>screenData;
	
	public ArrayList<ArrayList<Object>>generateTipDisplay(short noOfSquares, CouplesInSquare couplesInSquare, Couples couples)
	{
		if(noOfSquares == 0) return new ArrayList<ArrayList<Object>>(0);
		
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();

		short dancersToDisplay[] = new short[dancerData.size()];
		short couplesToDisplay   = (short)(dancerData.size());
		for(int ix = 0; ix < dancersToDisplay.length; ix++) dancersToDisplay[ix] = -1;
		
		this.screenData = new ArrayList<ArrayList<Object>>(couplesToDisplay);
		for(int ix = 0; ix < couplesToDisplay; ix++) 
		{
			this.screenData.add(new ArrayList<Object>(3));
		    this.screenData.get (ix).add("zzz");			// square number
		   	this.screenData.get (ix).add("zzz");			// dancer name(s)
		   	this.screenData.get (ix).add((Boolean)false);	// flag:  true if couple includes a single
		}
		
	    // loop goes through all squares in the tip
    	int squareNo = new Random().nextInt(noOfSquares);	// randomly assign beginning square no
    	int line     = 0;
	    for(short sx = 0; sx < noOfSquares; sx++)
	    {
	    	squareNo += 1;
	    	if(squareNo > noOfSquares) squareNo = 1;
	    	
	        // pull out the couples in this square
	    	for(short cx = 0; cx < 4; cx++)
	    	{
                short cpl = couplesInSquare.getCoupleNo(sx, cx);

	            this.screenData.get(line).set(0, Integer.toString(squareNo));	// set square number in display

	            short d0 = couples.getDancer0(cpl);
	            short d1 = couples.getDancer1(cpl);
	            dancersToDisplay[d0] = 1;	// processed dancer d0
	            dancersToDisplay[d1] = 1;	// processed dancer d1
	            if(dancerData.get(d0).get(Dancer.NAME_IX).toString().compareToIgnoreCase(dancerData.get(d1).get(Dancer.NAME_IX).toString()) < 0)
	            {
	            	this.screenData.get(line).set(1, dancerData.get(d0).get(Dancer.NAME_IX).toString() + " & " + 
                          	                         dancerData.get(d1).get(Dancer.NAME_IX).toString());
	            }
	            else
	            {
	            	this.screenData.get(line).set(1, dancerData.get(d1).get(Dancer.NAME_IX).toString() + " & " + 
                        	                         dancerData.get(d0).get(Dancer.NAME_IX).toString());
	            }
	            
	            // this is used to control whether the font should be a special color
	            // if a couple includes a single.  true == single == special color
	            
	            if((Integer)dancerData.get(d0).get(Dancer.PARTNER_IX) < 0 || 
	               (Integer)dancerData.get(d1).get(Dancer.PARTNER_IX) < 0)
	            {
	            	this.screenData.get(line).set(2, (Boolean)true);
	            }
	            else
	            {
	            	this.screenData.get(line).set(2, (Boolean)false);
	            }
	            line += 1;
	        }
	    } 
	    
	    // add in 'out' dancers
	    for(int ix = 0; ix < dancerData.size(); ix ++)
	    {
	    	if(dancersToDisplay[ix] == 1) continue;		// this dancer has been processed
	    	
	    	short d0 = (short)ix;
	    	short d1 = ((Integer)dancerData.get(ix).get(Dancer.PARTNER_IX)).shortValue();
	    	dancersToDisplay[d0] = 1;					// processed dancer d0
	    	
	    	if((Boolean)dancerData.get(d0).get(Dancer.DANCING_IX) || (Boolean)dancerData.get(d0).get(Dancer.DANCING_IX))
	    		this.screenData.get(line).set(0, Globals.OUT);
	    	else
	    		this.screenData.get(line).set(0, Globals.REQUESTED_OUT);
	    	
	    	this.screenData.get(line).set(2, (Boolean)false);
	    	
	    	// if the options are set so that a couple can be pulled apart to dance
	    	// with singles, it's possible that the partner of the current dancer 
	    	// (assuming there is a partner) has been temporarily assigned to dance
	    	// with someone else.  that means they will appear in the display with 
	    	// the new, temporary partner, and should not be shown again with this 
	    	// dancer as "out".
	    	if(d1 > -1) 	// yes, the current dancer has partner
	    		if(dancersToDisplay[d1] == 1)	// partner has already been displayed, so
	    			d1 = -1;					//  set d1 to -1, as if there is no partner
	    		else
	    			dancersToDisplay[d1] = 1;	// processed dancer d1
	    	
	    	// dancers who are not present are not shown in the tip display as "out"
	    	//if(!(Boolean)dancerData.get(d0).get(Dancer.PRESENT_IX) || 
	    	//   (d1 > 0 && !(Boolean)dancerData.get(d1).get(Dancer.PRESENT_IX))) continue;
	    	
	    	if(!(Boolean)dancerData.get(d0).get(Dancer.DANCER_AT_DANCE_IX)) continue;
	    	
	    	if(d1 < 0)
	    	{
	    		this.screenData.get(line).set(1, dancerData.get(d0).get(Dancer.NAME_IX).toString());
	    	}
	    	else
	    	if(dancerData.get(d0).get(Dancer.NAME_IX).toString().compareToIgnoreCase(dancerData.get(d1).get(Dancer.NAME_IX).toString()) < 0)
            {
            	this.screenData.get(line).set(1, dancerData.get(d0).get(Dancer.NAME_IX).toString() + " & " + 
                    	                         dancerData.get(d1).get(Dancer.NAME_IX).toString());
            }
            else
            {
            	this.screenData.get(line).set(1, dancerData.get(d1).get(Dancer.NAME_IX).toString() + " & " + 
                    	                         dancerData.get(d0).get(Dancer.NAME_IX).toString());
            }
	    	line += 1;
	    }

	    // sort the display in ascending order by dancers
	    Collections.sort(this.screenData, new Comparator<ArrayList<Object>>() {
            @Override
            public int compare(final ArrayList<Object> entry1, final ArrayList<Object> entry2) {
                final String square1 = entry1.get(1).toString();
                final String square2 = entry2.get(1).toString();
                return square1.compareTo(square2);
            }
        });
	    
	    // remove unused elements, indicated by 'zzz' in square number.  they should be at the 
	    // end after the sort ('zzz' is also in unused dancer names), so we iterate backwards.
	    for(int ix = screenData.size()-1; ix > -1; ix--)
	    	if(((String)screenData.get(ix).get(1)).equals("zzz")) screenData.remove(ix);
	    
	    return screenData;
	}
}

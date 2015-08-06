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
import java.util.Vector;

import com.bgt.frame.MainFrame;
import com.bgt.jtable.DancersJTable;

// the Tip class is very tightly coupled to the SquareGenerator class.
//
// the SquareGenerator class invokes methods in the CoupleGenerator class 
// to select dancers and build couples.  the SquareGenerator class then 
// uses the couples to build squares.  see the SquareGenerator class for 
// notes on how it builds squares.

// the CoupleGenerator class has the following responsibilities:
//
// the CoupleGenerator class scans through all the dancers, and selects 
// which dancers will be in the next tip.  it does this by analyzing 
// the number of times a dancer has been out as compared to all other
// dancers.  dancers with higher outs are selected first.  during this 
// process the "must dance" overrides the out-count analysis, and a 
// dancer can also be removed from consideration if they wish to sit
// out.
//
// when done selecting eligible dancers, the CoupleGenerator class then 
// pairs dancers to make couples.  some couples are self-identified, and
// some couples have to be made from singles.  when couples are made
// from singles, another factor considered is how many times 2 singles
// have danced with each other, and an effort is made to use this count to
// rotate singles to minimize the number of times any 2 singles are
// paired with each other.
//
// if the selected options permit, couples will be broken apart to
// dance with a single if the single has been out more than the
// couple.


public class CoupleGenerator implements Serializable
{
	private class GenderCounts
	{
		private int singleBelle;
		private int singleBeau; 
		private int matchedBelles; 
		private int matchedBeaux;
		
		protected GenderCounts()
		{
			singleBelle		= 0;
			singleBeau		= 0; 
			matchedBelles	= 0; 
			matchedBeaux	= 0;
		}
	}
	
	// the CoupleGenerator class is instantiated once and only once when program 
	// execution begins.
	//
	// some CoupleGenerator variables persist across multiple tips, and some 
	// variables are re-allocated or refreshed for each tip.
	
	private static final long serialVersionUID = 1L;

	private static final boolean doBreakUpCouples    = true;
	private static final boolean doNotBreakUpCouples = false;
	private static final boolean doSinglesOnly       = true;
	private static final boolean doSinglesAndCouples = false;

	//private transient DancersTableModel tmdl;	// transient == do not serialize
	
	// persistent variables.  these variables are allocated once, and 
	// persist across multiple tips.
	
	// non-persistent variables.  these variables are reallocated or
	// reinitialized each time a new tip is generated.

	private short currentTip 	=  0;
	private short noOfSquares	=  0;

	private int dancersSelected	= 0;
	private int coupleCt		= 0;
	private int maxOuts			= 0;
	
	// maxActual keeps track of the maximum number of times any one dancer has
	// danced with any other dancer.
	private short maxActual = 0;
	
	// "dancer number" in the comments below is the index into the DancersTableModel vector for a
	// given dancer.  the vector representing dancer number 5, for example, would be retrieved from 
	// the DancersTableModel data vector with something like the following construct:
	
	// (DancersTableModel) dancersTmdl.getDataVector().get(5);

	// the "couple" array is an array of Couples objects, which contain the 2 dancer numbers that make up
	// each couple, plus a flag to indicate whether the couple has been selected to be in a square.
	// this array is built anew for each tip, because the couples in a tip must be assembled from a 
	// combination of single dancers and coupled dancers taken from DancersTableModel, which means the
	// composition of the various couples will vary from tip to tip. 
	private Couples couples;
	
	// couplesInSquare is an array of CouplesInSquare objects, each of which contains the 4 couples 
	// assigned to a square.  the couple number of each couple corresponds to the primary index of the
	// couples array.
	private CouplesInSquare couplesInSquare;

	// dancerCt tracks the number of times one dancer has danced in the same square as another dancer.  
	// note that two dancers dancing in a couple does not increment the count between the two coupled dancers.
	private static DancerCounts dancerCt;
	
	// partnerCt tracks the number of times one dancer has been partnered with another in singles rotation.  
	// note that two dancers dancing in a predefined couple (not a couple built during singles rotation)
	// does not increment the count between the two coupled dancers.
	private static DancerCounts partnerCt;
	
	// participatingDancer is used to keep track of the dancers who are present during the process that
	// selects the dancers who will dance in the next tip.  this is necessary during the tip regeneration
	// process, because that process needs to adjust the number of outs a dancer has experienced.  any
	// dancer who was added after the tip was generated, or who was marked not present or deleted, is not 
	// a participating dancer, and therefore should not have their out counts adjusted.  we use this array 
	// to identify participating dancers.
	//
	// possible values for participatingDancer:
	//  1  dancer selected for a square, so out count will be unaffected
	//  0  dancer available, but not selected for a square, so out count should be incremented
	// -1  dancer voluntarily out; check options as to whether out count should be incremented
	// -2  dancer not available; do not increment out count (initialized value)
	private short[] participatingDancer;
	
	private static CoupleGenerator instance = null;

	protected CoupleGenerator() 
	{
		allocateArrays();
	}
	
	public static CoupleGenerator getInstance()
	{
		if(instance == null)
		{
			System.out.println("instantiate CoupleGenerator");
			instance = new CoupleGenerator();
		}
		return instance;
	}

	public static void loadSerializedInstance(CoupleGenerator cplGen)
	{
		if(cplGen != null) instance = cplGen;
	}
	
	public void allocateArrays() 
	{	
		this.currentTip  =  0;
		this.noOfSquares =  0;

		//System.out.println("allocateArrays(), Globals.getInstance().getDancersJTable().getRowCount() = " + Globals.getInstance().getDancersJTable().getRowCount());
		
		DancersJTable tbl = DancersJTable.getInstance();
		
		dancerCt  = new DancerCounts(tbl.getRowCount());
		partnerCt = new DancerCounts(tbl.getRowCount());
	}

	public void tableModelChanged(int lastRow) 
	{ 
		// partnerCt must be same size as dancerCt so we only check one size to adjust both.
		if(lastRow < 0 || lastRow == Integer.MAX_VALUE || lastRow < dancerCt.size()) 
		{
			MainFrame.getInstance().setDancerStatistics();
			return;
		}
		
		int oldCapacity = dancerCt.size();
		int newCapacity = oldCapacity + (lastRow + 1 - dancerCt.size());
		
		// this method is only useful for increasing capacity.  if
		// capacity is decreased, it is handled separately below in 
		// method deleteDancer().
		if(newCapacity > oldCapacity)
		{
			dancerCt. ensureCapacity(newCapacity);
			partnerCt.ensureCapacity(newCapacity);
		}
		MainFrame.getInstance().setDancerStatistics();
	}

	public void deleteDancer(int dancer) 
	{ 
		dancerCt. remove(dancer);
		partnerCt.remove(dancer);   
	   	
		MainFrame.getInstance().setDancerStatistics();
	}
	
	public void setCurrentTip(short currentTip) 
	{
		this.currentTip = currentTip;
		MainFrame.getInstance().setTipNo();
	}
	
	// getters
	
	public short getMaxActual()
	{
		return maxActual;
	}
	
	public short getCurrentTip() 
	{
		return this.currentTip;
	}
	
	public short getNoOfCouples() 
	{
		return couples.getNoOfCouples();
	}
	
	public short getNoOfSquares() 
	{
		return this.noOfSquares;
	}

	public Couples getCouples()
	{
		return couples;
	}
	
	public DancerCounts getDancerCt()
	{
		return dancerCt;
	}
	
	public CouplesInSquare getCouplesInSquare()
	{
		return couplesInSquare;
	}
	
	// end getters

	public ArrayList<ArrayList<Object>> generateTipDisplay()
	{
		return new TipDisplay().generateTipDisplay(noOfSquares, couplesInSquare, couples);
	}

	public void incrementTip() 
	{
		this.currentTip += 1;
		MainFrame.getInstance().setTipNo();
	}
	
	public void clearCouplesUsedFlag()
	{
		// clear the third element in the couples array that is used to track whether the couple
		// has been used to form a square in a tip.
		couples.clearCouplesUsedFlag();
	}
	
	// prior to generating a tip, it's necessary to build the couples that will be participating in the
	// tip.  this means (a) building couples from the singles, and (b) rotating through the couples who
	// are present (and skipping those who have become absent) to insure that dancers not chosen for the
	// tip are those who have the least number of outs.

	public boolean makeCouples()
	{	
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();

		int coupledCt			= 0;
		int eligibleDancers		= 0;
		
		this.dancersSelected	= 0; 
		this.maxOuts			= 0;
		
		int singleBelle         = 0;
		int singleBeau          = 0; 
		int singleEither        = 0;
		
		participatingDancer = new short[dancerData.size()];
		for(int ix = 0; ix < dancerData.size(); ix++) participatingDancer[ix] = -2;
		
		// first, compute the number of couples that can dance.  this is done by
		// counting all the partnered couples, and the number of couples that can
		// be put together from singles.  we also do some housekeeping here, making
		// sure the SELECTED flag is turned off for everyone, and initializing the
		// value of participatingDancer to 0 for all the dancers that will be
		// participating in the makeCouples logic.
		
		for(int ix = 0; ix < dancerData.size(); ix++)
		{
			Vector<Object>dancer = dancerData.get(ix);
			// reset the flag that tracks whether each dancer has been selected for this tip
			dancer.set(Dancer.DANCER_SELECTED_IX, (Boolean)false);		
			
			// skip dancers not present at the dance, and set participatingDancer to -2.
			if(!(Boolean)dancer.get(Dancer.DANCER_AT_DANCE_IX)) 
			{
				participatingDancer[ix] = -2;
				continue;
			}
			
			// skip dancers voluntarily out, and set participatingDancer to -1.
			// depending on options settings, we may increment the out count for these dancers.
			
			if(!(Boolean)dancer.get(Dancer.PRESENT_IX)) 
			{
				participatingDancer[ix] = -1;
				continue;
			}
			
			eligibleDancers += 1;			// this dancer was eligible to dance.  if not selected
			participatingDancer[ix] = 0;	// for this tip, we'll need to adjust their "out" count.	
											
			// capture this.maxOuts of eligible dancers for use later when making the tip
			if((Integer)dancer.get(Dancer.DANCER_OUTS_IX) > this.maxOuts)	
				this.maxOuts = (Integer)dancer.get(Dancer.DANCER_OUTS_IX); 
			
			if((Integer)dancer.get(Dancer.PARTNER_IX) > -1)
			{
				coupledCt += 1;
			}
			else
			{
				if((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.BEAU_IX)
					singleBeau += 1;
				else
				if((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.BELLE_IX)
					singleBelle += 1;
				else
				if((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.EITHER_IX)
					singleEither += 1;
			}
		}
		
		int noOfCouples = (short)(coupledCt / 2);	// this accounts for the partnered couples who can dance.  now  
													// we need to add in the couples that can be made from singles.

		// add in the couples that can be made from the singles, taking into account whether the
		// 'either' dancers can make up for any extra belles or beaux.  extra 'either dancers 
		// not needed to match with a specific role can also dance with other 'eithers'.
		
		if(singleBeau > singleBelle)
		{
			if(singleBeau > (singleEither + singleBelle))
			{
				noOfCouples += (singleEither + singleBelle);
			}
			else
			{
				noOfCouples += (singleBeau + (((singleBelle + singleEither) - singleBeau)/2));
			}
		}
		else
		if(singleBelle > singleBeau)
		{
			if(singleBelle > (singleEither + singleBeau))
			{
				noOfCouples += (singleEither + singleBeau);
			}
			else
			{
				noOfCouples += (singleBelle + (((singleBeau + singleEither) - singleBelle)/2));
			}
		}
		else
		{
			noOfCouples += (singleBeau + (singleEither/2));
		}

		this.coupleCt    = 0;
		this.noOfSquares = (short)(noOfCouples / 4);	// compute the number of squares from the number of couples,
		this.couples     = new Couples(noOfCouples);	// and allocate the couples array.
		this.couplesInSquare = new CouplesInSquare();
		
		System.out.println("couples.getNoOfCouples():  " + couples.getNoOfCouples() + ", noOfCouples:  " + noOfCouples + ", noOfSquares:  " + noOfSquares);

		if(this.noOfSquares < 1) return false;

		//System.out.println("couples.getNoOfCouples() = " + couples.getNoOfCouples());
		
		/*====================================================================================*/
			
		boolean firstPass	= true;
		int currentMaxOuts	= this.maxOuts;
		ArrayList<Short>randomizedDancer = new ArrayList<Short>(dancerData.size());
		for(short ix = 0; ix < dancerData.size(); ix++) randomizedDancer.add(ix, ix);
		Collections.shuffle(randomizedDancer);
		
		int dancersNeeded = this.noOfSquares * 8;
		while(currentMaxOuts >= 0 && this.dancersSelected < dancersNeeded)
		{
			if(Globals.singlesRotationCanTakeCouplesOut())
			{
				// this option is the most aggressive in terms of breaking apart couples:
				// it 'makes' couples for squares based first on the number of outs accumulated 
				// by a dancer, selecting dancers with higher out-count first.  if either partner 
				// in a couple has indicated a willingness to dance single, that couple can be
				// split apart and paired with 1 or 2 singles, if needed to get singles dancing.

				// before we start breaking up couples, let's see if we can pair up singles with each other.
				selectDancersToCouple(randomizedDancer, doNotBreakUpCouples, doSinglesOnly,       firstPass, currentMaxOuts, dancersNeeded);
				
				// we've tried pairing singles with singles. now let's get aggressive, and try breaking up couples.																					
				selectDancersToCouple(randomizedDancer, doBreakUpCouples,    doSinglesOnly,       firstPass, currentMaxOuts,dancersNeeded);
				
				// ok, it's all skate
				selectDancersToCouple(randomizedDancer, doBreakUpCouples,    doSinglesAndCouples, firstPass, currentMaxOuts, dancersNeeded);	
			}							
			else
			{	
				// 'doNotBreakUpCouples' means that we do not break up couples to pair with singles.
				// couples stay couples, and singles rotate only with other singles.
				selectDancersToCouple(randomizedDancer, doNotBreakUpCouples, doSinglesAndCouples, firstPass, currentMaxOuts, dancersNeeded);	
			}
		
			if(firstPass)
			{
				firstPass = false;
			}
			else
			{
				currentMaxOuts -= 1;
				//System.out.println("selectDancers, done because currentMaxOuts < 0:  " + currentMaxOuts);
			}
		}

		/*====================================================================================*/
		
		// we've selected the dancers who will be used to make the squares, but it's possible
		// that there are enough dancers leftover to make another square, except for the fact
		// that the remaining dancers include singles who cannot be paired with one another to 
		// make a couple because they are belle-belle or beau-beau.  if, however, the couples
		// not yet selected have indicated a willingness to be split up and paired with a single
		// if they would otherwise be out, we try another pass to see if enough dancers can
		// be paired from splitting up the willing couples to match with singles to make another 
		// square.
		
		// remove any 'extra' couples -- that is, couples selected beyond those needed to exactly
		// fill out 'n' squares -- to correct the value of this.dancersSelected.
		removeExtraCouples(dancerData);
		
		//System.out.println("Total eligible dancers = " + eligibleDancers + ", dancersSelected = " + this.dancersSelected);
		if((eligibleDancers - this.dancersSelected) >= 8 && (Globals.singlesRotateWithCouplesThatAreOut() || Globals.singlesRotationCanTakeCouplesOut()))
		{
			//System.out.println("There are enough Out dancers (" + (eligibleDancers - this.dancersSelected) + ") that I'm going to try to make another square.");
			singleBelle  	= 0;
			singleBeau   	= 0;
			singleEither 	= 0;

			for(Vector<Object>dancer : dancerData)
			{
				// skip absent or deleted dancers, and dancers already selected
				if(!(Boolean)dancer.get(Dancer.PRESENT_IX)        	||		// voluntarily out
				   !(Boolean)dancer.get(Dancer.DANCER_AT_DANCE_IX) 	|| 		// not at dance
					(Boolean)dancer.get(Dancer.DANCER_SELECTED_IX) 	|| 		// selected
					(Integer)dancer.get(Dancer.PARTNER_IX) > -1)			// coupled
					continue;
	
				//System.out.println("Examining single dancer remaining after first pass: " + dancer.get(Dancer.NAME_IX));
				
				if((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.BEAU_IX)
					singleBeau += 1;
				else
				if((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.BELLE_IX)
					singleBelle += 1;
				else
				if((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.EITHER_IX)
					singleEither += 1;		
			}
			
			ArrayList<Integer>partnerList = new ArrayList<Integer>(dancerData.size());
			for(int ax = 0; ax < dancerData.size(); ax++) partnerList.add(ax, 0);
			
			noOfCouples         = 0;
			int couplesSplit    = 0;
			int couplesIntact   = 0;
			int unpairedBelles  = (singleBelle - (singleBeau +singleEither)) > 0 ? (singleBelle - (singleBeau +singleEither)) : 0;
			int unpairedBeaux	= (singleBeau  - (singleBelle+singleEither)) > 0 ? (singleBeau  - (singleBelle+singleEither)) : 0;
			
			for(int dx = 0; dx < dancerData.size(); dx++)
			{
				Vector<Object>dancer = dancerData.get(dx);
				
				// skip absent or deleted dancers, and dancers already selected
				if(!(Boolean)dancer.get(Dancer.PRESENT_IX)        	||		// voluntarily out
				   !(Boolean)dancer.get(Dancer.DANCER_AT_DANCE_IX) 	|| 		// not at dance
					(Boolean)dancer.get(Dancer.DANCER_SELECTED_IX) 	||		// selected
					(Integer)dancer.get(Dancer.PARTNER_IX) < 0)				// single
					continue;
				
				if(partnerList.get(dx) > 0) continue;

				//System.out.println("Examining coupled dancers remaining after first pass: " + dancer.get(Dancer.NAME_IX));
				//System.out.println("Examining coupled dancers remaining after first pass: " + dancerData.get((Integer)dancer.get(Dancer.PARTNER_IX)).get(Dancer.NAME_IX));
				
				// we are processing both partners in a couple, so we record that fact for both.
				partnerList.set(dx, 1);
				partnerList.set((Integer)dancer.get(Dancer.PARTNER_IX), 1);
					
				// we're only interested in this couple if both of them are willing to dance single.

				boolean splitCouple = false;
				if((Boolean)dancer.get(Dancer.WILLING_SINGLE_IX) &&
				   (Boolean)dancerData.get((Integer)dancer.get(Dancer.PARTNER_IX)).get(Dancer.WILLING_SINGLE_IX))
				{
					// we only split the couple up if both can be used to pair with a single
					
					if(unpairedBeaux > 1)
					{
						if( ((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.BELLE_IX    ||
						     (Integer)dancer.get(Dancer.ROLE_IX) == Dancer.EITHER_IX) &&
						    ((Integer)dancerData.get((Integer)dancer.get(Dancer.PARTNER_IX)).get(Dancer.ROLE_IX) == Dancer.BELLE_IX || 
						     (Integer)dancerData.get((Integer)dancer.get(Dancer.PARTNER_IX)).get(Dancer.ROLE_IX) == Dancer.EITHER_IX) )
						{	
							noOfCouples    += 2;	// splitting this couple to pair up with 2 singles makes 2 new couples	
							couplesSplit   += 1;	// we split one couple
							unpairedBeaux  -= 2;	// and we matched 2 single beaux
							splitCouple     = true;
							//System.out.println("A");
						}
					}
					else
					if(unpairedBelles > 1)
					{
						if( ((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.BEAU_IX    ||
						     (Integer)dancer.get(Dancer.ROLE_IX) == Dancer.EITHER_IX) &&
						    ((Integer)dancerData.get((Integer)dancer.get(Dancer.PARTNER_IX)).get(Dancer.ROLE_IX) == Dancer.BEAU_IX || 
						     (Integer)dancerData.get((Integer)dancer.get(Dancer.PARTNER_IX)).get(Dancer.ROLE_IX) == Dancer.EITHER_IX) )
						{	
							noOfCouples    += 2;	// splitting this couple to pair up with 2 singles makes 2 new couples	
							couplesSplit   += 1;	// we split one couple
							unpairedBelles -= 2;	// and we matched 2 single belles
							splitCouple     = true;
							//System.out.println("B");
						}
					}
				}
				
				if(!splitCouple)
				{
					// we get here if it turns out there is no point to taking this couple apart -- either the partners dance
					// the wrong role to make a difference, or the remaining singles have already been matched to dancers in
					// another couple.  if that's the case, we just count this couple as a couple.
					noOfCouples   += 1;
					couplesIntact += 1;
					//System.out.println("D");
				}
			}
			
			System.out.println("I think I can make   " + noOfCouples + " couples from the remaining " + (eligibleDancers - this.dancersSelected) + " dancers");
			System.out.println("couples left intact: " + couplesIntact);
			System.out.println("couples split:       " + couplesSplit);
			
			/*==============================================================================================*/
			
			// this option is the less aggressive option in terms of breaking apart couples:  there is a first 
			// pass (which has already been done) that makes as many squares as possible while keeping couples
			// intact.  we reach this point only if there remain enough dancers leftover to make another square,
			// but the square can be made only if one or more couples is broken apart.  this would only happen 
			// if singles are all one role (beau-beau or belle-belle), so can't dance with each other, but that
			// means we have to have at least one couple comprised of 2 dancers willing to dance single, one of 
			// which is an "either" dancer, and the other of which dances the correct role needed to match the
			// out singles, or is also an "either" dancer.  it doesn't work if we have to take apart 2 couples 
			// to match with the singles, because that leaves the problem of who will dance with the other half 
			// of the couple.  since the remaining half of both couples in this latter case will necessarily be
			// dancers in the same role (we know that because the couples were taken apart to match two singles
			// in the same role, leaving partners in the other role -- and we know they were in the other role
			// and not "either" dancers, because "either" dancers could have been matched with a single).
			
			
			if(noOfCouples >= 4)	// we only do further processing if we think we can make at least 4 couples (a square)
			{
				firstPass = true;
				currentMaxOuts = this.maxOuts;
			
				while(currentMaxOuts >= 0 && this.dancersSelected < dancersNeeded)
				{
					//this.noOfSquares += (noOfCouples/4);
					//System.out.println("Second pass through select Dancers:  doBreakUpCouples, doSinglesOnly");
					//System.out.println("noOfCouples:  " + noOfCouples);
					//System.out.println("noOfSquares:  " + noOfSquares);	
				
					// second pass at selecting dancers to make couples.   'doBreakUpCouples' means we can break up 
					// couples who are out (if they are willing) to match with singles to see if we can make another 
					// square.  'doSinglesOnly' means we process the singles first, trying to get them coupled up 
					// before aggressively breaking couples apart.
					selectDancersToCouple(randomizedDancer, doBreakUpCouples, doSinglesOnly, firstPass, currentMaxOuts,dancersNeeded);	
					
					//System.out.println("Third pass through select Dancers:   doNotBreakUpCouples, doSinglesAndCouples");
				
					// third pass at selecting dancers to make couples.  'doNotBreakUpCouples' means we can break
					// up couples who are out (if they are willing) to match with singles to see if we can make 
					// another square.  'doSinglesAndCouples' means we handle any dancer who is left.
					selectDancersToCouple(randomizedDancer, doNotBreakUpCouples, doSinglesAndCouples, firstPass, currentMaxOuts,dancersNeeded);
				
					if(firstPass)
					{
						firstPass = false;
					}
					else
					{
						currentMaxOuts -= 1;
						//System.out.println("selectDancers, done because currentMaxOuts < 0:  " + currentMaxOuts);
					}
				}
			}
			/*==============================================================================================*/
		}
		
		// remove any couple from the couple array that doesn't have an actual couple,
		// as indicated by 2 zero-value dancers.
		int csize = couples.getNoOfCouples() - 1;
		System.out.println("number of couples before culling:  " + couples.getNoOfCouples());
		for(int ix = csize; ix > -1; ix--)	// go backwards, since removing an elements shifts the 
		{									// remaining elements down one notch.
			if(couples.getDancer0(ix) == 0 && couples.getDancer1(ix) == 0) 
			{
				//System.out.println("remove any couple from the couple array that doesn't have an actual couple");
				noOfCouples -= 1;
				couples.remove(ix);
			}
		}
		
		// remove any 'extra' couples
		removeExtraCouples(dancerData);
		
		// !important!  do not groom partners until after the unused couple slots have been
		// removed.  if you groom partners before the cleanup, the grooming algorithm will
		// try to match real dancers with phantom dancers in the unused slots.
		
		groomPartners();	// see if we can move partners around to lower partner counts.
		
		// shuffle the couples prior to making squares
		couples.shuffle();
		System.out.println("number of couples after grooming and shuffling:  " + couples.getNoOfCouples());
		//System.out.println("after shuffling couples");
		return true;
	}
	
	private void removeExtraCouples(Vector<Vector<Object>>dancerData)
	{
		// it's possible that that the couples selection process has selected more couples than
		// can be used to build squares -- that is, the number of couples selected is not an
		// even multiple of 4 (4 couples to a square).  when that happens, we need to de-select
		// the extra couples, but we need to do so carefully.  we don't want to de-select anyone
		// who must dance, or who has a high number of outs.
		
		int csize		= couples.getNoOfCouples() - 1;
		int coupleCt	= 0;
		for(int ix = csize; ix > -1; ix--)	// go backwards, since removing an elements shifts the 
		{									// remaining elements down one notch.
			if(couples.getDancer0(ix) == 0 && couples.getDancer1(ix) == 0) continue;
			coupleCt += 1;
		}
		
		int extraCouples = coupleCt % 4;
		if(extraCouples > 0) 
		{
			System.out.println("number of couples:  " + coupleCt + ", need to remove " + extraCouples + " extraCouples."); 
		}
		else
		{
			System.out.println("number of couples:  " + coupleCt + ", no need to remove extraCouples.");
			return;
		}

		int targetOuts   = 0;
		while(extraCouples > 0)
		{
			for(int ix = csize; ix > -1; ix--)	// go backwards, since removing an elements shifts the 
			{									// remaining elements down one notch.
				
				if(couples.getDancer0(ix) == 0 && couples.getDancer1(ix) == 0) continue;
				
				Vector<Object>dancer0 = dancerData.get(couples.getDancer0(ix));
				Vector<Object>dancer1 = dancerData.get(couples.getDancer1(ix));
				if((Integer)dancer0.get(Dancer.DANCER_OUTS_IX) <= targetOuts && !(Boolean)dancer0.get(Dancer.MUST_DANCE_IX) && 
				   (Integer)dancer1.get(Dancer.DANCER_OUTS_IX) <= targetOuts && !(Boolean)dancer1.get(Dancer.MUST_DANCE_IX) ) 
				{
					//System.out.println("remove any couple from the couple array that is extra.");
					extraCouples -= 1;
					couples.remove(ix);
					dancer0.set(Dancer.DANCER_SELECTED_IX, (Boolean)false);
					dancer1.set(Dancer.DANCER_SELECTED_IX, (Boolean)false);
					this.dancersSelected -= 2;
					
					if(extraCouples <= 0) break;
				}
			}
			targetOuts += 1;
		}
	}

	private void selectDancersToCouple(ArrayList<Short>randomizedDancer, boolean breakupCouples, boolean singlesOnly, boolean firstPass, int currentMaxOuts, int dancersNeeded)
	{	
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
		
		// the number of dancers we're looking for is passed to us in dancersNeeded.  dancers
		// are processed by the number of outs they have incurred; dancers with the highest
		// number of outs are selected first (although that can be overridden in the first pass
		// through the dancers if a given dancer "must dance."  the number of outs we're 
		// currently processing is passed to us in currentMaxOuts.
		
		// genderCounts is a structure that contains the counts of belles and beaux available
		// to dance at this level of outs -- that is, if we are currently looking for
		// dancers with at least 2 outs, for example, we need to know the number of
		// belles and beaux with that many outs.  those numbers are in the genderCounts 
		// structure.
		
		GenderCounts genderCounts = new GenderCounts();
		setGenderCounts(currentMaxOuts, genderCounts);
		
		boolean processDancer = false;
		System.out.println("noOfSquares:  " + this.noOfSquares + ", dancersNeeded:  " + dancersNeeded);
		
		// the point of randomizedDancer is to be able to traverse dancerData randomly.
		// it turns out to be important to shuffle things up between tips to remove 
		// the feeling of predictability in couple and square formation.

		//System.out.println("\n======================> selectDancers, begin looking through roster of dancers.\n");
		for(int iix = 0; iix < dancerData.size(); iix++)
		{	
			int ix = randomizedDancer.get(iix);	// randomize dancer selection
			
			//System.out.println("selectDancers, processing " + iix + ", which translates to " + ix + ", who is " + dancerData.get(ix).get(Dancer.NAME_IX));
				
			// first we check to see if this dancer is eligible to dance.  note that we go through the dancers
			// multiple times, the first time selecting dancers who have been out the most, and decrementing
			// the "out" count on each pass so as to select dancers who have been out the least after first 
			// having selected dancers who have been out the most.
				
			if(firstPass)
			{
				// on the first pass, we process only dancers who must dance
				processDancer = (Boolean)dancerData.get(ix).get(Dancer.MUST_DANCE_IX);
			}
			else
			{
				// on the second and subsequent pass, we continue to process dancers 
				// who must dance, and we also process dancers who have been out the most
				processDancer = (Boolean)dancerData.get(ix).get(Dancer.MUST_DANCE_IX) ||
						        (Integer)dancerData.get(ix).get(Dancer.DANCER_OUTS_IX) >= currentMaxOuts;
			}
		
			if(!processDancer)
			{   
				//System.out.println("   . . . processing for dancer skipped -- not enough outs, or not 'must dance'.  outs = " + (Integer)dancerData.get(ix).get(Dancer.DANCER_OUTS_IX) + ", currentMaxOuts = " + currentMaxOuts + ", firstPass = " + firstPass);
				continue;	// not been out enough and not marked must dance?
			}
				
			if(!(Boolean)dancerData.get(ix).get(Dancer.PRESENT_IX)			||	// voluntarily out?
			   !(Boolean)dancerData.get(ix).get(Dancer.DANCER_AT_DANCE_IX)	|| 	// not at dance?
				(Boolean)dancerData.get(ix).get(Dancer.DANCER_SELECTED_IX))		// already dancing?
			{
				// skip this dancer for now
				//System.out.println("   . . . processing for dancer skipped -- not present, or deleted, or already selected to dance.");
				continue;
			}
				
			// skip anyone in a couple if we're just processing singles
		
			if(singlesOnly && (Integer)dancerData.get(ix).get(Dancer.PARTNER_IX) > -1) 
			{
				//System.out.println("   . . . processing for dancer skipped because they are in a couple, and we're doing singles only.");
				continue;
			}
				
			// we've found someone who is eligible.  if they are coupled, they are relatively easy to handle.  we 
			// select them and their partner, even if the partner has fewer outs.  but . . . if we're permitted 
			// to break up couples, and both of the partnered dancers have indicated a willingness to dance single, 
			// we let this dancer fall through to singles processing.
					
			if( (Integer)dancerData.get(ix).get(Dancer.PARTNER_IX) > -1)	// this dancer is in a couple
			{	
				// if this dancer's partner has already been selected to dance, it means the couple
				// was split up.  if that's the case, then treat this dancer as a single.
				if((Boolean)dancerData.get((Integer)dancerData.get(ix).get(Dancer.PARTNER_IX)).get(Dancer.DANCER_SELECTED_IX) && 
				   (Boolean)dancerData.get(ix).get(Dancer.WILLING_SINGLE_IX))
				{
					processSingle(breakupCouples, ix, randomizedDancer, firstPass, currentMaxOuts, genderCounts);
				}
				else
				{
					//System.out.println("   . . . processing as couple.");
					processCouple(ix);	// process the couple
				}
			}
			else
			{
				// note that it's possible to reach processSingle on a dancer who is coupled, but only if the breakupCouples flag
				// is set, and the dancer is willing to dance single (see 'if' statement immediately above).
				System.out.println("Processing " + dancerData.get(ix).get(Dancer.NAME_IX) + " as single.");
				processSingle(breakupCouples, ix, randomizedDancer, firstPass, currentMaxOuts, genderCounts);	// process the single (might be half of a couple willing 
			}															// to dance single)
			if(this.dancersSelected >= dancersNeeded)
			{
				System.out.println("selectDancers, done because this.dancersSelected >= dancersNeeded:  " + this.dancersSelected + " >= " + dancersNeeded);
				break;
			}
			//System.out.println("dancersSelected now " + dancersSelected);
		}
	}
	
	private void setGenderCounts(int currentMaxOuts, GenderCounts genderCounts)
	{
		// count gender of dancers with the specified number of outs (currentMaxOuts).
		
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
		
		for(int ix = 0; ix < dancerData.size(); ix++)
		{
			Vector<Object>dancer  = dancerData.get(ix);
		
			if(!(Boolean)dancer.get(Dancer.DANCER_AT_DANCE_IX)	|| 	// skip dancers not present at the dance
			   !(Boolean)dancer.get(Dancer.PRESENT_IX)			||	// skip dancers voluntarily out
				(Boolean)dancer.get(Dancer.DANCER_SELECTED_IX)	|| 	// skip dancers already selected
				(Integer)dancer.get(Dancer.DANCER_OUTS_IX) < currentMaxOuts)	// skip dancers with out counts lower
			{																	// than the current max
				continue;
			}
			
			// we're only interested in single dancers.  for our purposes, a coupled
			// dancer can be considered a single if their partner has been assigned
			// to someone else, and the dancer is willing to act as a single.
			
			if((Integer)dancer.get(Dancer.PARTNER_IX) > -1)
			{
				// dancer is in a couple, and is not willing to act as a single.
				if(!(Boolean)dancer.get(Dancer.WILLING_SINGLE_IX) ) continue;
				
				Vector<Object>partner = dancerData.get((Integer)dancer.get(Dancer.PARTNER_IX));
				// partner has not been assigned to dance with someone else, so this dancer is still in a couple.
				if(!(Boolean)partner.get(Dancer.DANCER_SELECTED_IX)) continue;
			}
			
			if((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.BEAU_IX)
				genderCounts.singleBeau += 1;
			else
			if((Integer)dancer.get(Dancer.ROLE_IX) == Dancer.BELLE_IX)
				genderCounts.singleBelle += 1;
		}
	}
	
	private void processCouple(int ix)
	{
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
		
		// we can't use this couple if one of them has already been paired-up to dance with a single
		if((Boolean)dancerData.get((Integer)dancerData.get(ix).get(Dancer.PARTNER_IX)).get(Dancer.DANCER_SELECTED_IX)) return;
		
		//System.out.println("in processCouple, adding couple " + this.coupleCt);
		
		addCouple(ix, (Integer)dancerData.get(ix).get(Dancer.PARTNER_IX), false);
	}
	
	private void processSingle(boolean breakupCouples, int ix, ArrayList<Short>randomizedDancer, boolean firstPass, int currentMaxOuts, GenderCounts genderCounts)
	{
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
		
		// *****************************  SINGLES PROCESSING   ***************************** //
		// this section deals with singles, which is not as straightforward as couples because
		// we have to be concerned about matching a single with another suitable single. in
		// some cases depending on the option selected, a "suitable single" might come from
		// breaking apart a couple.
		//
		// if we have an odd number of singles, and all of them have the same 'out' count,
		// all the singles at the front of the line can pair up with each other (assuming 
		// a role match), which means we'll never even see that last single.  that's normally 
		// OK, since the last single will then have his out-count incremented, so will be 
		// picked up in the next tip.  a problem arises, however, if the dancer's must-dance 
		// flag is set.  the flag will be ignored in these circumstances because the dancer 
		// is last and is never considered as a potential partner.  we need to fix that.
		
		int selectedPartner 	 	= -1;
		boolean processSingle    	= false;
		boolean matchCoupledPartner = false;
		int roleHeavy            	= -1;	// -1 = singles are evenly matched
											// Dancer.BEAU_IX  = singles are heavy with beaux; 
											// Dancer.BELLE_IX = singles are heavy with belles; 
		
		System.out.println("in processSingle, working on dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + 
				           ", breakupCouples = " + breakupCouples + 
				           ", single is from couple = " + ((Integer)dancerData.get(ix).get(Dancer.PARTNER_IX) > -1));
		
		// note that "ix" refers to the original single; "jx" refers to the potential partner
			
		for(int jjx = 0; jjx < dancerData.size(); jjx++)	// jx for potential partner
		{
			int jx = randomizedDancer.get(jjx);	// randomize partner selection
			
			if(jx == ix) continue;				// skip if same dancer
				
			if(firstPass)
			{
				// on the first pass, we process dancers who must dance
				processSingle = (Boolean)dancerData.get(jx).get(Dancer.MUST_DANCE_IX);
			}
			else
			{
				// on the second and subsequent pass, we process dancers who must dance, 
				// and we also process dancers who have been out the most
				processSingle = (Boolean)dancerData.get(jx).get(Dancer.MUST_DANCE_IX) ||
						        (Integer)dancerData.get(jx).get(Dancer.DANCER_OUTS_IX) >= currentMaxOuts;
			}
				
			if(!processSingle) {
				//System.out.println("(1) in processSingle, will not pair with " + dancerData.get(jx).get(Dancer.NAME_IX));
				continue;	// not been out enough and not marked must dance?
			}
				
			if(!(Boolean)dancerData.get(jx).get(Dancer.PRESENT_IX)			 || // voluntarily out?
			   !(Boolean)dancerData.get(jx).get(Dancer.DANCER_AT_DANCE_IX)	 || // not at dance?
				(Boolean)dancerData.get(jx).get(Dancer.DANCER_SELECTED_IX)	 ||	// already dancing? 
			   
			   ((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX) > -1	&&		// dancer is coupled, and
				(!breakupCouples     										 || //   either we're not breaking up couples, or
				  (Integer)dancerData.get(ix).get(Dancer.PARTNER_IX) > -1 	 ||	//   we don't pair a coupled dancer with another coupled dancer, or
				 !(Boolean)dancerData.get(jx).get(Dancer.WILLING_SINGLE_IX)     //   dancer is not a willing single
				)
			   ))																					 
			{
				//System.out.println("(2) in processSingle, will not pair with " + dancerData.get(jx).get(Dancer.NAME_IX));
				continue;
			}
				
			// one final test:  if we got to this point, and one of the dancers is in a couple, we know
			// the other dancer must be a single (we do not pull apart couples to dance with another dancer
			// also pulled from a couple).  if we are breaking up a couple to pair with a single, we only 
			// we do that for singles who have been out as much or more than everyone else -- that is, we 
			// only break up couples for singles who have already been out more than other dancers.
		
			if((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX) > -1)	// dancer matched to dancer passed in is in a couple
			{	
				// has the single been out as much or more than other dancers, and is that number greater than 0?
				if((Integer)dancerData.get(ix).get(Dancer.DANCER_OUTS_IX) < this.maxOuts || this.maxOuts == 0)
					continue;
				
				// has the single been out less than the dancer we're considering for their partner?
				if((Integer)dancerData.get(ix).get(Dancer.DANCER_OUTS_IX) < (Integer)dancerData.get(jx).get(Dancer.DANCER_OUTS_IX))
					continue;
					
				// if the other dancer in the couple we're considering breaking apart has not yet been selected to dance,
				// has that dancers already been out as much as the single?  if so, splitting up the couple will probably 
				// mean the partner will be out more than the single, which is not a good thing.
				int partnerIx = (Integer)dancerData.get(jx).get(Dancer.PARTNER_IX);
				if(!(Boolean)dancerData.get(partnerIx).get(Dancer.DANCER_SELECTED_IX) &&	
				  (Integer)dancerData.get(ix).get(Dancer.DANCER_OUTS_IX) <= (Integer)dancerData.get(partnerIx).get(Dancer.DANCER_OUTS_IX))
					continue;
			}
				
			System.out.println("in processSingle, trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
			System.out.println("outs " + (Integer)dancerData.get(ix).get(Dancer.DANCER_OUTS_IX) + " / " + (Integer)dancerData.get(jx).get(Dancer.DANCER_OUTS_IX));
			System.out.println("coupled " + ((Integer)dancerData.get(ix).get(Dancer.PARTNER_IX) > -1) + " / " + ((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX) > -1));
				
			// we found a candidate, but now we must be concerned about matching a belle to beau, or
			// vice-versa; and we must also factor in the "eithers".
				
			// if one of the specific roles (as opposed to the "either" role) has more dancers than 
			// the other specific role, then ALL of dancers in the role with the smaller number of 
			// dancers must be matched to the opposite role, so as to make it possible to match the
			// "leftover" dancers in the larger set to any dancers who are "either".
				
			boolean mustMatchGender = false;
			boolean selected        = false;
				
			if((genderCounts.singleBelle > genderCounts.singleBeau  && genderCounts.singleBelle > genderCounts.matchedBelles) ||
			   (genderCounts.singleBeau  > genderCounts.singleBelle && genderCounts.singleBeau  > genderCounts.matchedBeaux))
			{
				System.out.println("in processSingle, A trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
				mustMatchGender = true;
				if(genderCounts.singleBelle > genderCounts.singleBeau  && genderCounts.singleBelle > genderCounts.matchedBelles)
					roleHeavy = Dancer.BELLE_IX;	// singles are heavy with belles
				else
					roleHeavy = Dancer.BEAU_IX;		// singles are heavy with beaux
			}
			else
			{
				roleHeavy = -1;
			}
				
			System.out.println("roleHeavy is " + (roleHeavy == Dancer.BEAU_IX  ? "Beau"  : 
					                                 (roleHeavy == Dancer.BELLE_IX ? "Belle" : "Neither")));

			// if the first dancer is an either, select this dancer if it is a beau and there is
			// an excess of beaux (because the excess beaux over belles can only be handled by
			// matching with an "either"); or if it is a belle, and there is an excess of belles
			// (same reasoning as for an excess of beaux); or if we don't care.
				
			if ((Integer)dancerData.get(ix).get(Dancer.ROLE_IX) == Dancer.EITHER_IX)	// 1st dancer is an either
			{
				System.out.println("in processSingle, B trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
				if(mustMatchGender)
				{
					if (((Integer)dancerData.get(jx).get(Dancer.ROLE_IX) == Dancer.BEAU_IX  && genderCounts.singleBeau  > genderCounts.singleBelle) || 
						((Integer)dancerData.get(jx).get(Dancer.ROLE_IX) == Dancer.BELLE_IX && genderCounts.singleBelle > genderCounts.singleBeau))
					{
						System.out.println("in processSingle, C trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
						selected = true;
					}
				}
				else	// not worried about matching genders, so either can match to anyone
				{
					System.out.println("in processSingle, D trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
					selected = true;
				}
			}
			else
					
			// if the first dancer is a beau, select this dancer if it is a belle.  if it's an either, match
			// if there is an excess of beaux (see comments above); or if we don't care.
					
			if( (Integer)dancerData.get(ix).get(Dancer.ROLE_IX) == Dancer.BEAU_IX  && // Beau needs a belle, or either
			      ( (Integer)dancerData.get(jx).get(Dancer.ROLE_IX)   == Dancer.BELLE_IX ||
			        ( (Integer)dancerData.get(jx).get(Dancer.ROLE_IX) == Dancer.EITHER_IX && 
			          (genderCounts.singleBeau > genderCounts.singleBelle || !mustMatchGender)
			        )
			      )
			  )
			{
				System.out.println("in processSingle, E trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
				selected = true;
			}
			else
				
			// if the first dancer is a belle, select this dancer if it is a beau.  if it's an either, match
			// if there is an excess of belle (see comments above); or if we don't care.
				
			if( (Integer)dancerData.get(ix).get(Dancer.ROLE_IX) == Dancer.BELLE_IX && // Belle needs beau, or either
			      ( (Integer)dancerData.get(jx).get(Dancer.ROLE_IX)  == Dancer.BEAU_IX  ||
			        ( (Integer)dancerData.get(jx).get(Dancer.ROLE_IX) == Dancer.EITHER_IX && 
					  (genderCounts.singleBelle > genderCounts.singleBeau || !mustMatchGender)
		            )
			      )
			  )
			{
				System.out.println("in processSingle, F trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
				selected = true;
			}
				
			if(selected  && roleHeavy > -1 &&											// if this dancer has been selected and singles are heavy in one of the dance roles
			  (Integer)dancerData.get(jx).get(Dancer.PARTNER_IX) > -1 && 														// and this dancer is part of a couple
			 !(Boolean)dancerData.get((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX)).get(Dancer.DANCER_SELECTED_IX))		// and their partner has not yet been selected
			{
				System.out.println("Examining partner of selected dancer who is part of a couple.");
				// if this dancer has been selected, and is part of a couple, and singles are heavy in belles or beaux, then we only
				// select them if their partner can also be matched to a remaining single, which means the partner cannot dance the 
				// the role that singles are heavy in.
				if(roleHeavy == Dancer.BEAU_IX  && (Integer)dancerData.get((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX)).get(Dancer.ROLE_IX) == Dancer.BEAU_IX ||	
				   roleHeavy == Dancer.BELLE_IX && (Integer)dancerData.get((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX)).get(Dancer.ROLE_IX) == Dancer.BELLE_IX)
				{
					System.out.println("roleHeavy = " + (roleHeavy == Dancer.BEAU_IX ? "Beau" : "Belle"));
					System.out.println("Dancer " + dancerData.get(jx).get(Dancer.NAME_IX) + " was selected, but has been de-selected because of partner " + 
						dancerData.get((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX)).get(Dancer.NAME_IX) + " has role " + 
						((Integer)dancerData.get((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX)).get(Dancer.ROLE_IX) == Dancer.BEAU_IX ? "Beau" : "Belle"));
					
					selected = false;
				}
				else
				{
					System.out.println("roleHeavy = " + (roleHeavy == Dancer.BEAU_IX ? "Beau" : "Belle"));
					System.out.println("Dancer " + dancerData.get(jx).get(Dancer.NAME_IX) + " was selected, and has an acceptable partner " + 
						dancerData.get((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX)).get(Dancer.NAME_IX) + " has role " + 
						((Integer)dancerData.get((Integer)dancerData.get(jx).get(Dancer.PARTNER_IX)).get(Dancer.ROLE_IX) == Dancer.BEAU_IX ? "Beau" : "Belle"));
					
					matchCoupledPartner = true;
				}
			}
				
			// this dancer has passed all the tests to get selected, but we're looking for
			// someone we've partnered with less.  note that there is tension between dancer
			// outs and the number of times two singles have danced together -- that is, it's
			// possible that we will select a partner we've danced with more than another
			// dancer because the partner we selected has more outs, and therefore is selected
			// to dance before the potential partner with fewer outs.

			System.out.println("in processSingle, G trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
			if(selected)
			{
				System.out.println("in processSingle, H trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
				if(selectedPartner < 0)	// if no partner selected yet, choose this one
				{
					System.out.println("in processSingle, dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " matched with " + dancerData.get(jx).get(Dancer.NAME_IX));
					selectedPartner = jx;
				}
				else					// if partner already selected, choose this one instead if they've danced together less
				{
					System.out.println("ix: " + ix + "jx: " + jx + ", selectedPartner" + selectedPartner);
					System.out.println("partnerCt.get(" + ix + ", " + jx              + "): " + partnerCt.get(ix, jx));
					System.out.println("partnerCt.get(" + ix + ", " + selectedPartner + "): " + partnerCt.get(ix, selectedPartner));
					if(partnerCt.get(ix, jx) < partnerCt.get(ix, selectedPartner))
					{
						System.out.println("in processSingle, dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " re-matched with " + dancerData.get(jx).get(Dancer.NAME_IX));
						selectedPartner = jx;
					}
					else{
						System.out.println("in processSingle, dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " NOT re-matched with " + dancerData.get(jx).get(Dancer.NAME_IX));
					}
				}
			}
			else {
				System.out.println("in processSingle, trying to match dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " with " + dancerData.get(jx).get(Dancer.NAME_IX));
			}
		}
			
		if(selectedPartner > -1)
		{	
			System.out.println("in processSingle, dancer " + dancerData.get(ix).get(Dancer.NAME_IX) + " MATCHED with " + dancerData.get(selectedPartner).get(Dancer.NAME_IX));
			// examine each dancer in this couple to increment matched belle/beau counts.
			if((Integer)dancerData.get(ix).get(Dancer.ROLE_IX) == Dancer.BELLE_IX) 
				genderCounts.matchedBelles += 1;
			else
			if((Integer)dancerData.get(ix).get(Dancer.ROLE_IX) == Dancer.BEAU_IX)  
				genderCounts.matchedBeaux += 1;
			
			if((Integer)dancerData.get(selectedPartner).get(Dancer.ROLE_IX) == Dancer.BELLE_IX) 
				genderCounts.matchedBelles += 1;
			else
			if((Integer)dancerData.get(selectedPartner).get(Dancer.ROLE_IX) == Dancer.BEAU_IX)  
				genderCounts.matchedBeaux += 1;

			addCouple(ix, selectedPartner, true);
			
			if(matchCoupledPartner)
			{
				// passing ix (current single) so we know to avoid matching them again; the PARTNER_IX of
				// the currently selected dancer that we want to match with a single; and the role of the
				// single we're looking to pair up (roleHeavy).
				matchCoupledPartnerToSingle(ix, (Integer)dancerData.get(selectedPartner).get(Dancer.PARTNER_IX), roleHeavy, randomizedDancer, genderCounts);		
			}
		}
	}
	
	void matchCoupledPartnerToSingle(int singleIx, int partnerIx, int roleIx, ArrayList<Short>randomizedDancer, GenderCounts genderCounts)
	{
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();

		// we got here because a single was paired up with a dancer in a couple, and we
		// want to pair the other half of the couple with a remaining single, if that's possible.
		
		// "singleIx"  is the original single.  
		// "partnerIx" is the dancer we are seeking a partner for (the partner of the dancer paired with the original single). 
		// "jx"        is the potential partner.
		
		for(int jjx = 0; jjx < dancerData.size(); jjx++)	// jx for partner
		{
			int jx = randomizedDancer.get(jjx);				// randomize partner selection
			
			if(jx == singleIx 												||	// same dancer?
			   (Integer)dancerData.get(jx).get(Dancer.PARTNER_IX) > -1		||	// not a single?														
			   (Boolean)dancerData.get(jx).get(Dancer.DANCER_SELECTED_IX)	||	// already selected?
			   (Integer)dancerData.get(jx).get(Dancer.ROLE_IX) != roleIx)		// not the role we're looking for?
			{
				continue;
			}
			
			//System.out.println("in matchPartnerToSingle, dancer " + dancerData.get(jx).get(Dancer.NAME_IX) + " MATCHED with " + dancerData.get(partnerIx).get(Dancer.NAME_IX));
			// examine each dancer in this couple to increment matched belle/beau counts.
			if((Integer)dancerData.get(jx).get(Dancer.ROLE_IX) == Dancer.BELLE_IX) 
				genderCounts.matchedBelles += 1;
			else
			if((Integer)dancerData.get(jx).get(Dancer.ROLE_IX) == Dancer.BEAU_IX)  
				genderCounts.matchedBeaux  += 1;
			
			if((Integer)dancerData.get(partnerIx).get(Dancer.ROLE_IX) == Dancer.BELLE_IX) 
				genderCounts.matchedBelles += 1;
			else
			if((Integer)dancerData.get(partnerIx).get(Dancer.ROLE_IX) == Dancer.BEAU_IX)  
				genderCounts.matchedBeaux  += 1;
			
			//System.out.println("in in matchPartnerToSingle, adding couple " + this.coupleCt);

			addCouple(jx, partnerIx, true);

			break;
		}
	}
	
	private void addCouple(int d0, int d1, boolean countPairing)
	{
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
	
		//System.out.println("in addCouple, adding couple " + this.coupleCt + ", size of couple:  " + couples.getNoOfCouples());
		//System.out.println("in addCouple, couple is " + dancerData.get(d0).get(Dancer.NAME_IX) + ", " + dancerData.get(d1).get(Dancer.NAME_IX));
	
		// select both this dancer and their partner.
		dancerData.get(d0).set(Dancer.DANCER_SELECTED_IX, (Boolean)true);
		dancerData.get(d1).set(Dancer.DANCER_SELECTED_IX, (Boolean)true);
		// 1st dancer in couple
		couples.setDancer0(this.coupleCt, (short)d0);														
		// 2nd dancer in couple
		couples.setDancer1(this.coupleCt, (short)d1);
		// flag for whether couple has been chosen for a square yet
		couples.setSelectedForSquare(this.coupleCt, false);
	
		this.coupleCt        += 1;
		this.dancersSelected += 2;
		
		if(countPairing)
		{
			// count the number of times we've made dance partners of these 2 dancers.
			// we don't count how many times the dancers in couples have danced with
			// each other, since we did not put them together.
			partnerCt.increment(d0, d1, (short)1);
		}
	}
	
    private void groomPartners()
    {
    	// as singles are partnered up, it's possible that pairing will have happened in such
        // a way as to always leave the last two singles to dance with themselves.  as a result,
        // these two singles effectively become a couple.  this routine looks for that situation
        // and tries to remedy it.
  
    	Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
    	
    	int maxPartnerCt = -1;	// highest partner count between 2 singles
    	int maxSearchSv  = -1;
        int maxCouple    = -1;	// couple where we found the partnered singles with the highest count
        boolean movedCouple = false;
        
    	boolean done = false;
    	while(!done)
    	{
    		maxPartnerCt = -1;
    		for(int cx = 0; cx < couples.getNoOfCouples(); cx++)
    		{
    			int d0 = couples.getDancer0(cx);
    			int d1 = couples.getDancer1(cx);
    			// couple made up of at least 1 single?  we assume that if one of the dancers is not a single,
    			// they were selected as a willing single in selectDancers, so we can continue to treat them
    			// as a single.
    			if((Integer)dancerData.get(d0).get(Dancer.PARTNER_IX) < 0 || (Integer)dancerData.get(d1).get(Dancer.PARTNER_IX) < 0)
    			{
    				if(partnerCt.get(d0, d1) > maxPartnerCt)
    				{
    					maxPartnerCt = partnerCt.get(d0, d1);
    					maxCouple    = cx;
    				}
    			}
    		}
 
    		// we're looking for a count between partners 2 less than the current max.  if the max is 6,
    		// for example, then we need to find a count no higher than 4, so when it is incremented by
    		// 1 it will still be less than 6.
            int maxSearch = maxPartnerCt - 2;
            if(maxSearch < 0 || (maxSearch == maxSearchSv && !movedCouple))
            {
            	done = true;
            	break;
            }
            maxSearchSv = maxSearch;
            
            // d00 and d01 are the dancers with the high count we'd like to lower
    		short d00 = couples.getDancer0(maxCouple);
    		short d01 = couples.getDancer1(maxCouple);
    		
    		movedCouple = false;
        	
            for(int cx = 0; cx < couples.getNoOfCouples(); cx++)
        	{
        		// skip if same couple.
        		if(cx == maxCouple) continue;

        		// d10 and d11 are the current swap candidates
        		short d10 = couples.getDancer0(cx);
        		short d11 = couples.getDancer1(cx);
        		
        		// if these dancers are paired, we don't take them apart just to enhance 
    			// the singles rotation.
        		if((Integer)dancerData.get(d10).get(Dancer.PARTNER_IX) == d11 && (Integer)dancerData.get(d11).get(Dancer.PARTNER_IX) == d10)
        			continue;
        		
        		// would swapping partners do nothing to lessen the count between partners?
        		if(partnerCt.get(d00, d10) > maxSearch || partnerCt.get(d00, d11) > maxSearch ||
        		   partnerCt.get(d01, d10) > maxSearch || partnerCt.get(d01, d11) > maxSearch)
        			continue;
        		
        		// get the roles (beau, belle, either) of the candidates for swap.
        		int d00role = (Integer)dancerData.get(d00).get(Dancer.ROLE_IX);
        		int d01role = (Integer)dancerData.get(d01).get(Dancer.ROLE_IX);
        		int d10role = (Integer)dancerData.get(d10).get(Dancer.ROLE_IX);
        		int d11role = (Integer)dancerData.get(d11).get(Dancer.ROLE_IX);
        		
        		// would the swap work for the chosen roles?
        		if(((d00role == Dancer.EITHER_IX || d10role == Dancer.EITHER_IX) ||
        		    (d00role == Dancer.BEAU_IX   && d10role == Dancer.BELLE_IX ) ||
        		    (d00role == Dancer.BELLE_IX  && d10role == Dancer.BEAU_IX  ))
        		&& ((d01role == Dancer.EITHER_IX || d11role == Dancer.EITHER_IX) ||
        		    (d01role == Dancer.BEAU_IX   && d11role == Dancer.BELLE_IX ) ||
        		    (d01role == Dancer.BELLE_IX  && d11role == Dancer.BEAU_IX  )))
        		{
        			swapPartners(maxCouple, cx, d00, d01, d10, d11);
        			movedCouple = true;
        			break;
        		}
        		else	// the combination above doesn't work; try the other one
            	if(((d00role == Dancer.EITHER_IX || d11role == Dancer.EITHER_IX) ||
            	    (d00role == Dancer.BEAU_IX   && d11role == Dancer.BELLE_IX ) ||
            	    (d00role == Dancer.BELLE_IX  && d11role == Dancer.BEAU_IX  ))
            	&& ((d01role == Dancer.EITHER_IX || d10role == Dancer.EITHER_IX) ||
            	    (d01role == Dancer.BEAU_IX   && d10role == Dancer.BELLE_IX ) ||
            	    (d01role == Dancer.BELLE_IX  && d10role == Dancer.BEAU_IX  )))
            	{
            		swapPartners(maxCouple, cx, d00, d01, d11, d10);
        			movedCouple = true;
            		break;
            	}
        	}
        	//if(masterCt++ > 3) done = true;
        }
    }
    
    private void swapPartners(int c0, int c1, short d00, short d01, short d10, short d11)
    {
    	// decrement the counts of existing partners, since they are being un-coupled.
    	partnerCt.increment(d00, d01, (short)-1);
    	partnerCt.increment(d10, d11, (short)-1);
    	
    	// increment the counts of new partners, since they are being coupled.
    	partnerCt.increment(d00, d10, (short)+1);
    	partnerCt.increment(d01, d11, (short)+1);
    	
    	// swap the dancers
    	
    	couples.setDancer0(c0, (short)d00);
    	couples.setDancer1(c0, (short)d10);
    	couples.setDancer0(c1, (short)d01);
    	couples.setDancer1(c1, (short)d11);
    }
    
    // adjustCounts is invoked only from the SquareGenerator object.
	
	public void adjustCounts(short incr)
	{
		// there are two flags that monitor whether a dancer is selected for a tip:
		//
		// 1.  on the first pass through the dancers to make couples, dancers who
		//     are selected, either as a couple, or as a single to make a couple,
		//     have their DANCER_SELECTED_IX column marked true.
		// 2.  the process to build couples can actually select more couples than
		//     needed, however, and the couple array built during the process has 
		//     a flag to indicate whether a couple actually made it into a square.
		//
		// since the first flag noted above can indicate that a dancer was selected
		// who did not actually make it into a square, we base out counts on the
		// second flag in the couple array.
		//
		// we need to adjust the "out" count of every person who was present and 
		// not deleted.  we adjust them up or down, as determined by the value of
		// "incr".
		//
		// we also need to adjust the partnerCt -- how many times one single has
		// danced with another single.
		
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
		
		// participatingDancer is an array of dancers that is built at the time couples are generated,
		// and contains an element for every dancer in the dancer model at that time.  if a dancer was
		// not eligible for selection (not present, or deleted), the corresponding element in 
		// participatingDancer is set to -1.  if a dancer was eligible for selection, the corresponding 
		// element in participatingDancer is set to 0. 
		
		// now we traverse the couple array.  that array is all the couples who were assembled for the
		// tip, and it has a flag to indicate whether a given couples was actually selected for the tip.
		// we use that flag to mark the element in participatingDancer -- 0 means the dancer was not in 
		// a square, 1 means the dancers *was* in a square.
		
		// we also adjust partner counts (how many times two singles have danced together)
		// as we traverse the couple array.
		
		// traverse couple array to flag participatingDancer elements and adjust partner counts
		for(int ix = 0; ix < couples.getNoOfCouples(); ix++)
		{
			// copy couple selected flag to participatingDancer 
			participatingDancer[couples.getDancer0(ix)] = (short)(couples.getSelectedForSquare(ix) ? 1 : 0);
			participatingDancer[couples.getDancer1(ix)] = (short)(couples.getSelectedForSquare(ix) ? 1 : 0);
			
			// we only adjust partnerCt if we're going backwards, which happens when
			// we're regenerating a square.  partnerCt will be incremented upwards by the
			// makeCouples process as single dancers are selected and paired, so we don't
			// handle that case here
			
			if(incr < 0)	// decrementing partnerCt?
			{
				short d0 = couples.getDancer0(ix);
				short d1 = couples.getDancer1(ix);
				// adjust partnerCt only if at least one dancer is a single whi has been paired
				// for this tip with another dancer.  note that it's possible for one of the
				// dancers to be in a couple that's been broken apart, so we check both dancers
				// to see if at least one is a single.
				if((Integer)dancerData.get(d0).get(Dancer.PARTNER_IX) < 0 ||
				   (Integer)dancerData.get(d1).get(Dancer.PARTNER_IX) < 0 )
				{
					partnerCt.increment(d0, d1, incr);
				}
			}
		}
		
		// go through all dancers and adjust their out count if appropriate
		for(int ix = 0; ix < participatingDancer.length; ix++)
		{
			// if a dancer was selected to dance in this tip, participatingDancer[ix] will be 1.
			// if a dancer was not eligible to dance in this tip (not present or deleted),
			//    participatingDancer[ix] will be set to -1.
			// if a dancer was eligible but not selected to dance, participatingDancer[ix] will be 0,
			// indicating their out counts need to be adjusted.
			if(participatingDancer[ix] == 0  ||										// this dancer was out (not selected), so he/she is a dancer whose
			  (participatingDancer[ix] == -1 && Globals.getCountVountaryOuts()))	// 'out count' needs to be adjusted.	
			{																		
				
				// whether or not a dancer was present and not deleted is a factor that was considered when
				// building the couple array when the tip was generated.  although the status may have changed
				// after tip was generated but before it was regenerated, we're only concerned with the status
				// when the tip was generated, which is effectively captured in the participatingDancer array.  
				// we therefore do not check whether the dancer is present or has been deleted here.
				
				dancerData.get(ix).set(Dancer.DANCER_OUTS_IX, (Integer)dancerData.get(ix).get(Dancer.DANCER_OUTS_IX) + incr);
			}
		}
		
		// incrementing and decrementing the dancer counts, which are the number
		// of times one dancer has danced with another, is handled during square
		// generation.  that's because dancer counts can go up or down depending
		// on whether a couple is being moved into or out of a square, and for that
		// process to work, counts have to be accurate as the process unfolds.
		// this method (adjustCounts) happens after the squares are completely formed.
		// if adjustCounts is called to increment the counts, we don't need to do that
		// for the dancer counts, since that's been happening during square formation  
		// as noted.  if we're decrementing, however, it means we're going to toss out
		// the current square and regenerate a new one, so we need to go through the 
		// squares and decrement the dancer counts from the tossed-out square.
		
		if(incr < 0)
		{
		    for(short sx = 0; sx < getNoOfSquares(); sx++) computeDanceCounts(sx, incr);
		}
	}
	
	public void computeDanceCounts(short sx, short incr)
	{
		// go around one square (sx), and either increment or decrement
	    // the number of times each dancer in the square has danced with
	    // every other dancer in the square, based on the value of incr.

	    for(short c0 = 0; c0 < 3; c0++)
	    {
	    	// note that partnerCt -- the number of times one single has been
	    	// partnered with another -- is computed separately, and is not
	    	// handled here.

	        // d00 and d01 are the dancers in the first couple

	        short d00 = this.couples.getDancer0(this.couplesInSquare.getCoupleNo(sx, c0));
	        short d01 = this.couples.getDancer1(this.couplesInSquare.getCoupleNo(sx, c0));

	        for(short c1 = (short)(c0+1); c1 < 4; c1++)
	        {
	            if(c0 == c1) continue;

	            // this counts how many times the partners in a couple
	            // have danced with dancers from other couples in the square

	            // d10 and d11 are the dancers in the second couple
	            // System.out.println("computeDanceCounts, tx = " + tx + ", sx = " + sx + ", c1 = " + c1);
	            // System.out.println("this.couplesInSquare size:  " + this.couplesInSquare.size() + " / " + this.couplesInSquare.get(0).size() + " / " +
	            //                     this.couplesInSquare.get(0).get(0).size());
	            short d10 = this.couples.getDancer0(this.couplesInSquare.getCoupleNo(sx, c1));
	            short d11 = this.couples.getDancer1(this.couplesInSquare.getCoupleNo(sx, c1));
	            
	           	dancerCt.increment(d00, d10, incr);
	           	dancerCt.increment(d00, d11, incr);
	            dancerCt.increment(d01, d10, incr);
	            dancerCt.increment(d01, d11, incr);
	        }
	    }
	    this.resetMaxActual();	// recalculate maxActual
	}
	
	public void resetMaxActual()
	{
		this.maxActual = 0;
		
		for(int d0 = 0; d0 < DancersJTable.getInstance().getRowCount(); d0++) {
		    for(int d1 = 0; d1 < DancersJTable.getInstance().getRowCount(); d1++) 
		    	if(dancerCt.get(d0, d1) > this.maxActual) this.maxActual = dancerCt.get(d0, d1);
		}	
		
	}
}
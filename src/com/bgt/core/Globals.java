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

import java.awt.Color;

import com.bgt.frame.TipFrame;

public class Globals 
{
	// we provide three ways to handle singles rotation:
	//
	// 1.  SINGLES_ROTATE_ONLY_WITH_SINGLES
	//
	// in this mode, singles only rotate in with other singles.  this might
	// be an issue if the singles are heavy in one role, and there are no
	// or few singles who dance the "either" role.  singles will be out a
	// disproportionate number of times, since there will not be enough
	// singles in the lightly-populated role to match with the singles in the
	// heavily-populated role.
	//
	// 2.  SINGLES_ROTATE_WITH_OUT_COUPLES
	//
	// this option will break up a couple to dance with a single, but only
	// under the following conditions:
	//
	// a.  the algorithm to build the squares has marked the couple as out; and
	//
	// b.  the couple has agreed that one or both is willing to dance single; and
	//
	// c.  the willing party can be role-matched to a single that is out; and
	//
	// d.  the single has been out more times than both dancers in at least
	//     one couple already selected to dance, which means it would be fair 
	//     to take that couple out and replace them with a new couple formed 
	//     from the single and the dancer from the first out couple.
	//
	// 3.  SINGLES_ROTATION_CAN_TAKE_COUPLES_OUT  !! Deprecated as not useful !!
	//
	// if this option is in effect, people are selected to dance based on how
	// many times they have been out.  if a single has been out more than both
	// dancers in a couple that has indicated their willingness to dance single,
	// and a dancer in the couple can be role-matched to the out single, then the
	// couple can be split to make it possible for the single to dance.
	//
	// if only one (or very few) couples has indicated a willingness to dance as 
	// singles, and the singles-rotation is heavy in one role so as to always need 
	// a single from the couple, this can have the unpleasant effect of splitting 
	// the couple up every other tip.
	
	public static final int HEADER_HEIGHT = 32;
	
	public static final int SINGLES_ROTATE_ONLY_WITH_SINGLES			= 0;
	public static final int SINGLES_ROTATE_WITH_COUPLES_THAT_ARE_OUT	= 1;
	public static final int SINGLES_ROTATION_CAN_TAKE_COUPLES_OUT		= 2;
	
	public static final int NO_HIGHLIGHT      = 0;
	public static final int HIGHLIGHT_ON_BTN  = 1;
	public static final int HIGHLIGHT_OFF_BTN = 2;
	
	public static final String OUT            = "Out";
	public static final String REQUESTED_OUT  = "Out*";
	
	public final static Color VERY_LIGHT_RED  = new Color(255,   0,   0,  30);	
	public final static Color VERY_LIGHT_GREY = new Color(235, 235, 235     );
	public final static Color VERY_LIGHT_BLUE = new Color(  0,   0, 255,  30);
	public final static Color MEDIUM_BLUE     = new Color(  0,   0, 255,  36);
	public final static Color LIGHT_YELLOW    = new Color(255, 255,   0,  60);
	
	private static int selectedOption		  = SINGLES_ROTATE_ONLY_WITH_SINGLES;
	private static boolean countVountaryOuts  = true;
	private static boolean loadSerializedData = false;
	private static TipFrame tipFrame  		  = null;
	private static int tipFrameHeight  		  = 0;
	
	private static Globals instance = null;
	
	protected Globals() {}
	
	public static Globals getInstance()
	{
		if(instance == null)
		{
			instance = new Globals();
		}
		return instance;
	}

	public static void setSelectedOption(int pSelectedOption)
	{
		selectedOption = pSelectedOption;
	}
	
	public static int getSelectedOption()
	{
		return selectedOption;
	}

	public static boolean singlesRotateOnlyWithSingles()
	{
		return selectedOption == SINGLES_ROTATE_ONLY_WITH_SINGLES;
	}

	public static boolean singlesRotateWithCouplesThatAreOut()
	{
		return selectedOption == SINGLES_ROTATE_WITH_COUPLES_THAT_ARE_OUT;
	}

	public static boolean singlesRotationCanTakeCouplesOut()
	{
		return selectedOption == SINGLES_ROTATION_CAN_TAKE_COUPLES_OUT;
	}
	
	public static void setCountVountaryOuts(boolean pCountVountaryOuts)
	{
		countVountaryOuts = pCountVountaryOuts;
	}
	
	public static boolean getCountVountaryOuts()
	{
		return countVountaryOuts;
	}
	
	public static void setLoadSerializedData(boolean pLoadSerializedData)
	{
		loadSerializedData = pLoadSerializedData;
	}
	
	public static boolean getLoadSerializedData()
	{
		return loadSerializedData;
	}
	
	public static TipFrame getTipFrame()
	{
		return tipFrame;
	}

	public static void setTipFrame(TipFrame pTipFrame)
	{
		if(tipFrame != null)
		{
			tipFrame.dispose();
			tipFrame = null;
		}
		tipFrame = pTipFrame;
	}
	
	public static int getTipFrameHeight()
	{
		return tipFrameHeight;
	}

	public static void setTipFrameHeight(int pTipFrameHeight)
	{
		tipFrameHeight = pTipFrameHeight;
	}
}

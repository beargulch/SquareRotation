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

@SuppressWarnings("rawtypes")
public class Dancer extends Vector
{
	private static final long serialVersionUID = 1L;
	
	public static final int    NAME_IX			   =  0;
	public static final int    ROLE_IX			   =  1;
	public static final int    PARTNER_IX		   =  2;
	public static final int    DANCING_IX		   =  3;
	public static final int    MUST_DANCE_IX	   =  4;
	public static final int    WILLING_SINGLE_IX   =  5;
	public static final int    DANCER_OUTS_IX      =  6;
	public static final int    DANCER_AT_DANCE_IX  =  7;
	public static final int    DANCER_DANCED_IX    =  8;
	public static final int    DANCER_SELECTED_IX  =  9;

	public static final String NAME_STR		   	   = "Name";
	public static final String ROLE_STR		   	   = "Beau/Belle";
	public static final String PARTNER_STR		   = "Partner";
	public static final String DANCING_STR		   = "Dancing";
	public static final String MUST_DANCE_STR	   = "Must Dance";
	public static final String WILLING_SINGLE_STR  = "Willing Single";
	public static final String DANCER_OUTS_STR     = "Dancer Outs";
	public static final String DANCER_AT_DANCE_STR = "At the Dance";
	public static final String DANCER_DANCED_STR   = "Has Danced";
	public static final String DANCER_SELECTED_STR = "Selected This Tip";
	
	public static final int    BEAU_IX    = 0;
	public static final int    BELLE_IX   = 1;
	public static final int    EITHER_IX  = 2;
	
	public static final String BEAU_STR   = "BEAU";
	public static final String BELLE_STR  = "BELLE";
	public static final String EITHER_STR = "EITHER";
	
	public static final String[] beauBelleOptions = {BEAU_STR, BELLE_STR, EITHER_STR};
	
	public final static
	String dancerCol[] = { NAME_STR,        	// "Name"
					   	   ROLE_STR,			// "Beau/Belle"
					   	   PARTNER_STR, 		// "Partner"
					   	   DANCING_STR, 		// "Dancing"
					   	   MUST_DANCE_STR, 		// "Must Dance"
					   	   WILLING_SINGLE_STR,	// "Willing Single"
					   	   DANCER_OUTS_STR,		// "Dancer Outs"
					   	   DANCER_AT_DANCE_STR, // "At the Dance"
					   	   DANCER_DANCED_STR,	// "Has Danced"
					   	   DANCER_SELECTED_STR, // "Selected This Tip"
					 	 };
	
	@SuppressWarnings("unchecked")
	public Dancer(Vector v)
	{
		super(v);
	}

	public Dancer()
	{
		super(dancerCol.length);
	}
	
    public static Class getColumnClass(int column) 
    {
        switch (column) 
        {
        	case NAME_IX:
        		return String.class;
                
            case DANCING_IX:
            case MUST_DANCE_IX:
            case WILLING_SINGLE_IX:
            case DANCER_DANCED_IX:
            case DANCER_SELECTED_IX:
            case DANCER_AT_DANCE_IX:
            	return Boolean.class;
            	
        	case ROLE_IX:
        	case PARTNER_IX:
            case DANCER_OUTS_IX:
            	return Integer.class;
            	
        	default:
        		System.out.println("DancersTableModel.getColumnClass, invalid column " + column);
        		return null;
        }
    }
    
    public static boolean isCellEditable(int row, int column) 
	{    
        switch (column) 
        {
        	case NAME_IX:
        	case DANCER_OUTS_IX:
        		return false;

        	case ROLE_IX:
        	case PARTNER_IX:  
            case DANCING_IX:
            case MUST_DANCE_IX:
            case WILLING_SINGLE_IX:
            case DANCER_AT_DANCE_IX:
            	return true;
        	default:
        		System.out.println("DancersTableModel.isCellEditable, invalid column " + column);
        		return false;
        }
    }
    
    public static int getColumnCount()
    {
    	return dancerCol.length;
    }
}
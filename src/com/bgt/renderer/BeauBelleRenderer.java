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

package com.bgt.renderer;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import com.bgt.core.Dancer;

public class BeauBelleRenderer implements TableCellRenderer, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private final static Color VERY_LIGHT_GREY = new Color(245, 245, 245);
	private final static Color VERY_LIGHT_BLUE = new Color(0, 0, 255, 30);
    
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		// this method has 2 functions:  turn off the grid around the text field, which gets turned on
		// by adding a grid to the enclosing jTable; and set the alternating gray/blue backgrounds.
		
		JTextField jTextField = new JTextField();
	
		jTextField.setBorder(new EmptyBorder(jTextField.getBorder().getBorderInsets(jTextField)));
		
		if(isSelected)
			jTextField.setBackground(VERY_LIGHT_BLUE);
		else
			if(row%2 == 0)
				jTextField.setBackground(VERY_LIGHT_GREY);
			else
				jTextField.setBackground(Color.white);
		
		int beauBelleIx = -1;
		try
		{
			beauBelleIx = (Integer)value;
		}
		catch(Exception e) {}	// if it's not an integer, leave beauBelleIx at -1
		
		switch (beauBelleIx)
		{
			case Dancer.BEAU_IX:
				jTextField.setText(Dancer.BEAU_STR);
				break;
			case Dancer.BELLE_IX:
				jTextField.setText(Dancer.BELLE_STR);
				break;
			case Dancer.EITHER_IX:
				jTextField.setText(Dancer.EITHER_STR);
				break;
			default:
				jTextField.setText("");
		}
		return jTextField;
	}
}

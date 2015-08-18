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
import javax.swing.table.TableCellRenderer;

import com.bgt.core.Dancer;
import com.bgt.core.Globals;

public class BeauBelleComboBoxRenderer implements TableCellRenderer, Serializable
{
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		// turn "value", which should be an integer, into a name retrieved from the table model,
		// and set the alternating gray/blue backgrounds.
		
		System.out.println("BeauBelleComboBoxRenderer, value = " + value + ", row = " + row + ", column = " + column);
		
		JTextField jTextField = new JTextField();

		if(isSelected)
			jTextField.setBackground(Globals.VERY_LIGHT_BLUE);
		else
			if(row%2 == 0)
				jTextField.setBackground(Globals.VERY_LIGHT_GREY);
			else
				jTextField.setBackground(Color.white);
		
		int beauBelleIx = Dancer.beauBelleOptions.length - 1;	// default is last value in array
		try
		{
			beauBelleIx = (Integer)value;
		}
		catch(Exception e) {}	// if it's not an integer, leave beauBelleIx at last value in array

		jTextField.setText((String)Dancer.beauBelleOptions[beauBelleIx]);
		
		return jTextField;
	}
}

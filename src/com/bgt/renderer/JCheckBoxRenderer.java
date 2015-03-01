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

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import com.bgt.core.Globals;

public class JCheckBoxRenderer implements TableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		// this method has 2 functions:  turn off the grid around the text field, which gets turned on
		// by adding a grid to the enclosing jTable; and set the alternating gray/blue backgrounds.
		
		JCheckBox jCheckBox = new JCheckBox();
		
		if(isSelected)
			jCheckBox.setBackground(Globals.VERY_LIGHT_BLUE);
		else
			if(row%2 == 0)
				jCheckBox.setBackground(Globals.VERY_LIGHT_GREY);
			else
				jCheckBox.setBackground(Color.white);
		
		jCheckBox.setSelected((Boolean)value);
		jCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		
		return jCheckBox;
	}
}

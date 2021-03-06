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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import com.bgt.core.Dancer;
import com.bgt.core.Globals;
import com.bgt.model.DancersTableModel;

public class JTextFieldRenderer implements TableCellRenderer, Serializable
{
	private static final long serialVersionUID = 1L;
	
	private boolean centerText;
	private boolean redHighLight;
	
	public JTextFieldRenderer()
	{
		super();
		this.centerText   = false;
		this.redHighLight = true;
	}
	
	public JTextFieldRenderer(boolean centerText, boolean redHighLight)
	{
		super();
		this.centerText	  = centerText;
		this.redHighLight = redHighLight;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		// this method has 2 functions:  turn off the grid around the text field, which gets turned on
		// by adding a grid to the enclosing jTable; and set the alternating gray/blue backgrounds.
		
		JTextField jTextField = new JTextField();
		jTextField.setBorder(new EmptyBorder(jTextField.getBorder().getBorderInsets(jTextField)));
		
		if(value == null) return jTextField;
		
		DancersTableModel dancersTmdl = (DancersTableModel)jTable.getModel();
		Boolean dancing = true;
		
		if(!(Boolean)dancersTmdl.getValueAt(jTable.convertRowIndexToModel(row), Dancer.DANCER_AT_DANCE_IX) || 
		   !(Boolean)dancersTmdl.getValueAt(jTable.convertRowIndexToModel(row), Dancer.DANCING_IX))
		{
			dancing = false;	
		}
		
		if(!dancing && this.redHighLight) 
			jTextField.setBackground(Globals.VERY_LIGHT_RED);
		else
		if(isSelected)
			jTextField.setBackground(Globals.VERY_LIGHT_BLUE);
		else
			if(row%2 == 0)
				jTextField.setBackground(Globals.VERY_LIGHT_GREY);
			else
				jTextField.setBackground(Color.white);
				
		jTextField.setText(value.toString());

		if(this.centerText) jTextField.setHorizontalAlignment(SwingConstants.CENTER);
				
		return jTextField;
	}
}

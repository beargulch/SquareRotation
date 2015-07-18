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

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import com.bgt.core.Globals;
import com.bgt.jtable.TipJTable;

public class JTipTextFieldRenderer implements TableCellRenderer
{
	private boolean centerText;
	
	public JTipTextFieldRenderer()
	{
		super();
		this.centerText = false;
	}
	
	public JTipTextFieldRenderer(boolean centerText)
	{
		super();
		this.centerText = centerText;
	}

	@Override
	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		// this method has the following major functions:  
		// -- turn off the grid around the text field, which gets turned on by adding a grid to the enclosing jTable; 
		// -- set the alternating gray/blue backgrounds;
		// -- highlight dancers who are out with a yellow background;
		// -- highlight single dancers with blue text;
		// -- scale the text according to the size of the parent container;
		// -- set the row height so the rows precisely fit into the container.
				
		JTextField jTextField = new JTextField();
		jTextField.setBorder(new EmptyBorder(jTextField.getBorder().getBorderInsets(jTextField)));

		if(value == null) value = "";

		float scale;
		int   rowHeight = (Globals.getTipFrame().getContentPane().getSize().height-jTable.getTableHeader().getHeight())/
	             jTable.getRowCount();
		
		//System.out.println("calculated rowHeight = " + rowHeight);
		if(rowHeight <= 50)
		{
			rowHeight = 50;
			scale     = 1.0f;
		}
		else
		{
			scale = (float)((TipJTable)jTable).getTipFrame().getHeight() / (float)Globals.getTipFrameHeight();
			if(scale < 1.0f) scale = 1.0f;
		}
		jTextField.setFont(jTable.getFont().deriveFont(24.0f * scale));
		jTable.setRowHeight(rowHeight);
			
		if(value.toString().length() > 1 && value.toString().substring(0,2).equals("y~"))
		{
			jTextField.setText(value.toString().substring(2));
			jTextField.setBackground(Globals.LIGHT_YELLOW);
		}		
		else
		if(value.toString().equals(Globals.OUT) || value.toString().equals(Globals.REQUESTED_OUT))
		{
			jTextField.setText(value.toString());
			jTextField.setBackground(Globals.LIGHT_YELLOW);
		}		
		else
		{
			if(value.toString().length() > 1 && value.toString().substring(0,2).equals("s~"))
			{
				jTextField.setText(value.toString().substring(2));
				jTextField.setForeground(Color.BLUE);
			}
			else
				jTextField.setText(value.toString());
		
			if(row%2 == 0)
				jTextField.setBackground(Globals.VERY_LIGHT_GREY);
			else
				jTextField.setBackground(Color.white);
		}

		if(this.centerText) jTextField.setHorizontalAlignment(SwingConstants.CENTER);

		return jTextField;
	}
}
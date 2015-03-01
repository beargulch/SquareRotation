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

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

public class JComboBoxRenderer extends BasicComboBoxRenderer
{
	private static final long serialVersionUID = 1L;
    
	@Override
	public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		// turn "value", which should be an integer, into a name retrieved from the table model,
		// and set the alternating gray/blue backgrounds.
		
		//JTextField jTextField = new JTextField();
		JComboBox<String>jComboBox = new JComboBox<String>();
		
		int partnerIx = -1;
		try
		{
			partnerIx = (Integer)value;
		}
		catch(Exception e) {}	// if it's not an integer, leave partnerIx at -1

		if(partnerIx > -1)
		{
			//TableModel mdl = (DancersTableModel)table.getModel();
			//jComboBox.setText((String)mdl.getValueAt(partnerIx, Dancer.NAME_IX));
			jComboBox.setSelectedIndex(partnerIx);
		}
		return jComboBox;
	}
}
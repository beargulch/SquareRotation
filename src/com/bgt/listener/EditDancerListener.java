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

package com.bgt.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.bgt.dialog.EditDancerDialog;
import com.bgt.jtable.DancersJTable;

public class EditDancerListener implements ActionListener 
{	
	private boolean edit;
	
	public EditDancerListener(boolean edit)
	{
		this.edit = edit;
	}

	@Override 
	public void actionPerformed(ActionEvent e) 
	{	
		DancersJTable jTable = DancersJTable.getInstance();
		
		int row = -1;
		if(this.edit && jTable.getSelectedRow() > -1) row = jTable.convertRowIndexToModel(jTable.getSelectedRow());
		
	    new EditDancerDialog(row);
	}
}
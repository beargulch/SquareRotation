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
import java.util.Vector;

import com.bgt.core.Dancer;
import com.bgt.core.Globals;
import com.bgt.core.SquareGenerator;
import com.bgt.frame.TipFrame;
import com.bgt.jtable.DancersJTable;
import com.bgt.model.DancersTableModel;

public class GenerateSquareListener implements ActionListener 
{
	public GenerateSquareListener(){}
	
	@Override 
	public void actionPerformed(ActionEvent e) 
	{	
		// do we have at least a square?
		if(((DancersTableModel)DancersJTable.getInstance().getModel()).getRowCount() < 8) return;

		if(!SquareGenerator.getInstance().generateNextTip()) return;

		Globals.setTipFrame(new TipFrame());

		// clear the Must Dance flag
		for(Vector<Object>dancer : DancersJTable.getInstance().getDancerData()) 
			dancer.set(Dancer.MUST_DANCE_IX,(Boolean)false);
	}
}
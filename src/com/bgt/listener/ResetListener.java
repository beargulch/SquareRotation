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

import javax.swing.JOptionPane;

import com.bgt.core.Dancer;
import com.bgt.core.Globals;

public class ResetListener implements ActionListener 
{
	public ResetListener(){}
	
	@Override 
	public void actionPerformed(ActionEvent e) 
	{	
		int returnVal = JOptionPane.showConfirmDialog(null, "This will set the tip back to 1, and reset all counts to zero.  Proceed?", "Reset Tipst", 
						JOptionPane.OK_CANCEL_OPTION);
		if(returnVal == JOptionPane.CANCEL_OPTION) return;
		
		if(Globals.getInstance().getCoupleGenerator() != null)
		{
			Globals.getInstance().getCoupleGenerator().allocateArrays();

			Vector<Vector<Object>>dancerData = Globals.getInstance().getDancersTableModel().getDataVector();
			for(Vector<Object>data : dancerData) data.set(Dancer.DANCER_OUTS_IX, 0);
			
			Globals.getInstance().getMainFrame().setTipNo();
		}
	}
}
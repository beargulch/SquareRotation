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
import com.bgt.frame.EditDancerFrame;

public class DeleteDancerListener implements ActionListener 
{	
	private EditDancerFrame frame;
	private int dancer;
	
	public DeleteDancerListener(EditDancerFrame frame, int dancer)
	{
		this.frame  = frame;
		this.dancer = dancer;
	}

	@Override 
	public void actionPerformed(ActionEvent e) 
	{
		if(dancer == 1)
			doTheDelete(frame.getDancer1RowIX());
		else
		if(dancer == 2)
			doTheDelete(frame.getDancer2RowIX());
	}
	
	private void doTheDelete(int row)
	{
		int returnVal = JOptionPane.showConfirmDialog(null, "Are you certain you wish to delete dancer " + 
						Globals.getInstance().getDancersTableModel().getDataVector().get(row).get(Dancer.NAME_IX) + 
						"?  This will delete all data associated with this dancer (outs, etc.), and is unrecoverable.", 
						"Delete dancer " + dancer, JOptionPane.OK_CANCEL_OPTION);
		if(returnVal != JOptionPane.OK_OPTION) return;
		
		Vector<Vector<Object>>dancerData = Globals.getInstance().getDancersTableModel().getDataVector();
		
		String deletedDancerName = (String)Globals.getInstance().getDancersTableModel().getDataVector().get(row).get(Dancer.NAME_IX);
		int partnerRow = (Integer)dancerData.get(row).get(Dancer.PARTNER_IX);
		if(partnerRow > row) partnerRow -= 1;
		
		dancerData.remove(row);
		Globals.getInstance().getDancersTableModel().fireTableRowsDeleted(row, row);
		Globals.getInstance().getCoupleGenerator().deleteDancer(row);
		for(int ix = 0; ix < dancerData.size(); ix++)
		{
			if((Integer)dancerData.get(ix).get(Dancer.PARTNER_IX) > row)
				dancerData.get(ix).set(Dancer.PARTNER_IX, (Integer)dancerData.get(ix).get(Dancer.PARTNER_IX)-1);
		}
		clearEditFrameFields(dancer);

		if(partnerRow > -1)
		{
			// if there is a partner, break the link between them regardless of whether
			// the partner will also be deleted.
			
			dancerData.get(partnerRow).set(Dancer.PARTNER_IX, -1);
			frame.getJPartners().setSelected(false);
			
			returnVal = JOptionPane.showConfirmDialog(null, "Do you wish to also delete dancer " + deletedDancerName + "'s partner, " + 
			            Globals.getInstance().getDancersTableModel().getDataVector().get(partnerRow).get(Dancer.NAME_IX) +
			            "?  This will delete all data associated with this dancer (outs, etc.), and is unrecoverable.", 
			            "Delete dancer " + dancer + "'s partner", JOptionPane.OK_CANCEL_OPTION);
			
			if(returnVal == JOptionPane.OK_OPTION) {
				
				// delete the partner as well
				dancerData.remove(partnerRow);
				Globals.getInstance().getDancersTableModel().fireTableRowsDeleted(partnerRow, partnerRow);
				Globals.getInstance().getCoupleGenerator().deleteDancer(partnerRow);
				for(int ix = 0; ix < dancerData.size(); ix++)
				{
					if((Integer)dancerData.get(ix).get(Dancer.PARTNER_IX) > partnerRow)
						dancerData.get(ix).set(Dancer.PARTNER_IX, (Integer)dancerData.get(ix).get(Dancer.PARTNER_IX)-1);
				}
				clearEditFrameFields(3-dancer);	// change 1 to 2, or 2 to 1.
			}
		}
		
		Globals.getInstance().getDancersTableModel().fireTableDataChanged();
		frame.dispose();
	}
	
	private void clearEditFrameFields(int whichDancer)
	{
		if(whichDancer == 1)
		{
			frame.getDancer1().setText("");
			frame.getOuts1().setText("");
			frame.getJPresent1().setSelected(false);       
			frame.getJMustDance1().setSelected(false);     
			frame.getJWillingSingle1().setSelected(false); 
			frame.getBeauBelleBox1().setSelectedIndex(-1);
		}
		else
		{
			frame.getDancer2().setText("");
			frame.getOuts2().setText("");
			frame.getJPresent2().setSelected(false);       
			frame.getJMustDance2().setSelected(false);     
			frame.getJWillingSingle2().setSelected(false); 
			frame.getBeauBelleBox2().setSelectedIndex(-1);
		}
	}
}
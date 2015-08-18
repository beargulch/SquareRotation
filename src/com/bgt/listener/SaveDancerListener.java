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
import com.bgt.dialog.EditDancerDialog;
import com.bgt.jtable.DancersJTable;
import com.bgt.model.DancersTableModel;

public class SaveDancerListener implements ActionListener 
{	
	private EditDancerDialog frame;

	public SaveDancerListener(EditDancerDialog frame)
	{
		this.frame = frame;
	}
	
	@Override 
	public void actionPerformed(ActionEvent e) 
	{	
		DancersTableModel tblModel = (DancersTableModel)DancersJTable.getInstance().getModel();
		
		this.frame.clearDancer1Error();
		Vector<Vector<Object>>dancerData = tblModel.getDataVector();
		boolean err1 = false;

		if(this.frame.getDancer1RowIX() > -1)	// editing existing dancer 1
		{
			String name = this.frame.getDancer1().getText();
			for(int rowCt = 0; rowCt < dancerData.size(); rowCt++)
			{
				if(rowCt == this.frame.getDancer1RowIX()) continue;
				if(name.equals((String)dancerData.get(rowCt).get(Dancer.NAME_IX)))
				{
					err1 = true;
					break;
				}
			}
			if(err1)
			{
				this.frame.dancer1Error();
				this.frame.validate();
				this.frame.repaint();
			}
			else
			{
				tblModel.setValueAt(this.frame.getDancer1().getText(), 				  this.frame.getDancer1RowIX(), Dancer.NAME_IX);
				tblModel.setValueAt(this.frame.getOuts1Value(), 				  	  this.frame.getDancer1RowIX(), Dancer.DANCER_OUTS_IX);
				tblModel.setValueAt(this.frame.getJPresent1().isSelected(), 		  this.frame.getDancer1RowIX(), Dancer.DANCING_IX);
				tblModel.setValueAt(this.frame.getJMustDance1().isSelected(), 		  this.frame.getDancer1RowIX(), Dancer.MUST_DANCE_IX);
				tblModel.setValueAt(this.frame.getJWillingSingle1().isSelected(), 	  this.frame.getDancer1RowIX(), Dancer.WILLING_SINGLE_IX);
				tblModel.setValueAt(this.frame.getBeauBelleBox1().getSelectedIndex(), this.frame.getDancer1RowIX(), Dancer.ROLE_IX);
				tblModel.setValueAt(this.frame.getJAtDance1().isSelected(), 		  this.frame.getDancer1RowIX(), Dancer.DANCER_AT_DANCE_IX);
			}
		}
		else	// adding new dancer 1
		{
			String name = this.frame.getDancer1().getText();
			if(name != null && !name.equals(""))
			{
				Vector<Object>v = new Vector<Object>(Dancer.getColumnCount());
				v.add(Dancer.NAME_IX, 				(String)this.frame.getDancer1().getText());
				v.add(Dancer.ROLE_IX, 				new Integer(this.frame.getBeauBelleBox1().getSelectedIndex()));
				v.add(Dancer.PARTNER_IX, 			new Integer(-1));
				v.add(Dancer.DANCING_IX, 			new Boolean(this.frame.getJPresent1().isSelected()));
				v.add(Dancer.MUST_DANCE_IX, 		new Boolean(this.frame.getJMustDance1().isSelected()));
				v.add(Dancer.WILLING_SINGLE_IX,		new Boolean(this.frame.getJWillingSingle1().isSelected()));
				v.add(Dancer.DANCER_OUTS_IX,		new Integer(0));
				v.add(Dancer.DANCER_AT_DANCE_IX, 	new Boolean(this.frame.getJAtDance1().isSelected()));
				v.add(Dancer.DANCER_DANCED_IX,		new Boolean(false));
				v.add(Dancer.DANCER_SELECTED_IX,	new Boolean(false));
				tblModel.addRow(v);
				this.frame.setDancer1RowIX(tblModel.getLastRow());
			}
		}

		// the following block of code is (or should be) identical to the block above, but it
		// references dancer 2 in the editDancerFrame instead of dancer 1.  is there some way
		// to consolidate the repetitive code into a single function called once for each dancer?  
		// if there is, i don't see it, since they each reference specific elements (e.g., 
		// getDancer1() vs getDancer2(), or getJPresent1() vs getJPresent2()).
		
		boolean err2 = false;
		
		if(this.frame.getDancer2RowIX() > -1)	// editing existing dancer 2
		{	
			String name = this.frame.getDancer2().getText();
			if(this.frame.isDancer2TextBox())	// if dancer 2 entered in text box, make sure the name
			{								// is not used elsewhere
				for(int rowCt = 0; rowCt < dancerData.size(); rowCt++)
				{
					if(rowCt == this.frame.getDancer2RowIX()) continue;
					if(name.equals((String)dancerData.get(rowCt).get(Dancer.NAME_IX)))
					{
						err2 = true;
						break;
					}
				}
			}
			if(!err2)
			{
				// only update dancer 2 name if it's a text box.  if it's from a drop-down, we know the name
				// cannot have been changed.
				if(this.frame.isDancer2TextBox()) 
					tblModel.setValueAt(this.frame.getDancer2().getText(),			  this.frame.getDancer2RowIX(), Dancer.NAME_IX);
				tblModel.setValueAt(this.frame.getOuts2Value(), 					  this.frame.getDancer2RowIX(), Dancer.DANCER_OUTS_IX);
				tblModel.setValueAt(this.frame.getJPresent2().isSelected(), 		  this.frame.getDancer2RowIX(), Dancer.DANCING_IX);
				tblModel.setValueAt(this.frame.getJMustDance2().isSelected(), 		  this.frame.getDancer2RowIX(), Dancer.MUST_DANCE_IX);
				tblModel.setValueAt(this.frame.getJWillingSingle2().isSelected(), 	  this.frame.getDancer2RowIX(), Dancer.WILLING_SINGLE_IX);
				tblModel.setValueAt(this.frame.getBeauBelleBox2().getSelectedIndex(), this.frame.getDancer2RowIX(), Dancer.ROLE_IX);
				tblModel.setValueAt(this.frame.getJAtDance2().isSelected(), 		  this.frame.getDancer2RowIX(), Dancer.DANCER_AT_DANCE_IX);
			}
		}
		else	// adding new dancer 2
		{
			String name = this.frame.getDancer2().getText();
			if(name != null && !name.equals(""))
			{
				Vector<Object>v = new Vector<Object>(Dancer.getColumnCount());
				v.add(Dancer.NAME_IX, 				(String)this.frame.getDancer2().getText());
				v.add(Dancer.ROLE_IX, 				new Integer(this.frame.getBeauBelleBox2().getSelectedIndex()));
				v.add(Dancer.PARTNER_IX, 			new Integer(-1));
				v.add(Dancer.DANCING_IX, 			new Boolean(this.frame.getJPresent2().isSelected()));
				v.add(Dancer.MUST_DANCE_IX, 		new Boolean(this.frame.getJMustDance2().isSelected()));
				v.add(Dancer.WILLING_SINGLE_IX,		new Boolean(this.frame.getJWillingSingle2().isSelected()));
				v.add(Dancer.DANCER_OUTS_IX,		new Integer(0));
				v.add(Dancer.DANCER_AT_DANCE_IX, 	new Boolean(this.frame.getJAtDance2().isSelected()));
				v.add(Dancer.DANCER_DANCED_IX,		new Boolean(false));
				v.add(Dancer.DANCER_SELECTED_IX,	new Boolean(false));
				tblModel.addRow(v);
				this.frame.setDancer2RowIX(tblModel.getLastRow());
			}
		}
		
		if(!err1 && !err2)
		{
			connectDancers();
			frame.dispose();
		}
	}
	
	private void connectDancers()
	{
		if(this.frame.getJPartners().isSelected())
		{
			if(this.frame.getDancer1Row() != null && this.frame.getDancer2Row() != null)
			{
				this.frame.getDancer1Row().set(Dancer.PARTNER_IX, this.frame.getDancer2RowIX());
				this.frame.getDancer2Row().set(Dancer.PARTNER_IX, this.frame.getDancer1RowIX());
				this.frame.getDancer2Row().set(Dancer.DANCING_IX, this.frame.getDancer1Row().get(Dancer.DANCING_IX));	// partners must both be in or out
			}
			else
				clearPartners();
		}
		else
		{
			clearPartners();
		}
	}

	private void clearPartners()
	{
		if(this.frame.getDancer1Row() != null) this.frame.getDancer1Row().set(Dancer.PARTNER_IX, -1);
		if(this.frame.getDancer2Row() != null) this.frame.getDancer2Row().set(Dancer.PARTNER_IX, -1);
		frame.setDancer2RowIX(-1);
	}
}
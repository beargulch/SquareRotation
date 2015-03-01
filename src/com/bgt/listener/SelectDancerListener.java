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

import javax.swing.JComboBox;

import com.bgt.core.Dancer;
import com.bgt.frame.EditDancerFrame;
import com.bgt.model.DanceComboBoxModel;

public class SelectDancerListener implements ActionListener 
{	
	private EditDancerFrame frame;
	
	public SelectDancerListener(EditDancerFrame frame)
	{
		this.frame = frame;
	}

	@Override 
	public void actionPerformed(ActionEvent e) 
	{	

		@SuppressWarnings("rawtypes")
		JComboBox partnerBox = (JComboBox)e.getSource();
		
		// this statement translates the index of the selected item in the partner drop-down to the index
		// of the item in the dancer table model.
		int partnerIx = (Integer)((DanceComboBoxModel)partnerBox.getModel()).getComboMap().get(partnerBox.getSelectedIndex());
		
		System.out.println("comboBox index " + partnerBox.getSelectedIndex() + " translates to dancer model index " + partnerIx);
		
		
		this.frame.setDancer2RowIX(partnerIx);
		Vector<Object>dancer2Row = frame.getDancer2Row();
		
		if(dancer2Row != null)
		{
			if((Boolean)dancer2Row.get(Dancer.PRESENT_IX)) 
				frame.getJPresent2().setSelected(true);       
			else 
				frame.getJPresent2().setSelected(false);
			if((Boolean)dancer2Row.get(Dancer.MUST_DANCE_IX)) 
				frame.getJMustDance2().setSelected(true);     
			else 
				frame.getJMustDance2().setSelected(false);
			if((Boolean)dancer2Row.get(Dancer.WILLING_SINGLE_IX)) 
				frame.getJWillingSingle2().setSelected(true); 
			else 
				frame.getJWillingSingle2().setSelected(false);
			if((Integer)dancer2Row.get(Dancer.ROLE_IX) > -1) 
				frame.getBeauBelleBox2().setSelectedIndex((Integer)dancer2Row.get(Dancer.ROLE_IX));
		}
		else
		{
			frame.getJPresent2().setSelected(false);
			frame.getJMustDance2().setSelected(false);
			frame.getJWillingSingle2().setSelected(false);
			frame.getBeauBelleBox2().setSelectedIndex(0);
		}
	}
}
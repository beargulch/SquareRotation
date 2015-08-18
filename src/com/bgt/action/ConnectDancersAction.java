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

package com.bgt.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.bgt.core.Dancer;
import com.bgt.dialog.EditDancerDialog;

public class ConnectDancersAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;
	private EditDancerDialog frame;
	
	public ConnectDancersAction(EditDancerDialog frame)
	{
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e)
    {
		actionPerformed();
	}

	// used to connect or disconnect 2 dancers as partners on the EditDancerFrame
	// pop-up, based on whether the partners check-box is checked (selected).
	public void actionPerformed()
	{
		if(frame.getJPartners().isSelected())
		{
			if(frame.getDancer1Row() != null && frame.getDancer2Row() != null)
			{
				frame.getDancer1Row().set(Dancer.PARTNER_IX, 		 frame.getDancer2RowIX());
				frame.getDancer2Row().set(Dancer.PARTNER_IX, 		 frame.getDancer1RowIX());
				frame.getDancer2Row().set(Dancer.DANCING_IX, 		 frame.getDancer1Row().get(Dancer.DANCING_IX));			// partners must both be in or out
				frame.getDancer2Row().set(Dancer.DANCER_AT_DANCE_IX, frame.getDancer1Row().get(Dancer.DANCER_AT_DANCE_IX));	// partners must both be at dance, or not
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
		if(frame.getDancer1Row() != null) frame.getDancer1Row().set(Dancer.PARTNER_IX, -1);
		if(frame.getDancer2Row() != null) frame.getDancer2Row().set(Dancer.PARTNER_IX, -1);
		frame.setDancer2RowIX(-1);
	}
}

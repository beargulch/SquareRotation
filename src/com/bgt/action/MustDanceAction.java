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
import com.bgt.jtable.DancersJTable;
import com.bgt.model.DancersTableModel;

public class MustDanceAction extends AbstractAction
{
	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent e)
	{
		DancersJTable jTable = (DancersJTable)e.getSource();
		DancersTableModel dancersTmdl = (DancersTableModel)jTable.getModel();

	    Boolean mustDance = (Boolean)dancersTmdl.getValueAt(jTable.convertRowIndexToModel(jTable.getSelectedRow()), Dancer.MUST_DANCE_IX);
	    mustDance = !mustDance;  // toggle value
	    dancersTmdl.setValueAt((Boolean)mustDance, jTable.convertRowIndexToModel(jTable.getSelectedRow()), Dancer.MUST_DANCE_IX);
	    
	    // if this dancer has a partner, set the partner present/absent value to match.
	    
	    int partnerIx = (Integer)dancersTmdl.getValueAt(jTable.convertRowIndexToModel(jTable.getSelectedRow()), Dancer.PARTNER_IX);
	    if (partnerIx > -1) dancersTmdl.setValueAt((Boolean)mustDance, partnerIx, Dancer.MUST_DANCE_IX);
	    
	    jTable.validate();
	    jTable.repaint();
	}
}

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

package com.bgt.frame;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.bgt.core.Globals;
import com.bgt.jtable.TipJTable;
import com.bgt.viewport.HeaderViewport;

public class TipFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPane;
	
	public static final String SQUARE_STR	= "Square";
	public static final String DANCER_STR	= "Dancers";
	public final static
	String tipCol[] = { SQUARE_STR,		// "Square"
						DANCER_STR,		// "Dancers"
						SQUARE_STR,		// "Square"
						DANCER_STR,		// "Dancers"
					  };
	
	public TipFrame()
	{	
		this.setLayout(new GridBagLayout());
		this.setTitle("Tip Number " + (Globals.getInstance().getCoupleGenerator().getCurrentTip()));
	    
		TipJTable jTable = new TipJTable(this);
		
		// generate the tip data, and add the data to the table model
        jTable.setUpTableModel(Globals.getInstance().getCoupleGenerator().generateTipDisplay(), tipCol);

	    this.setPreferredSize(new Dimension(900,(jTable.getModel().getRowCount()*60)+jTable.getTableHeader().getHeight()));
        
        System.out.println("after jTable init, rows = " + jTable.getRowCount());
        
        this.jScrollPane = new JScrollPane(jTable);
        jScrollPane.setColumnHeader(new HeaderViewport());
        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		GridBagConstraints c1 = new GridBagConstraints();
        c1.weightx = 1;
        c1.weighty = 1;
        c1.gridx   = 0;
        c1.gridy   = 0;
        c1.fill    = GridBagConstraints.BOTH;
        this.add(jScrollPane, c1);

	    // display the window
	    this.pack();
	    Globals.setTipFrameHeight(this.getHeight());
	    this.setVisible(true);
	}
	
	public JScrollPane getJScrollPane()
	{
		return this.jScrollPane;
	}
}
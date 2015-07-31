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

package com.bgt.jtable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JTable;

import com.bgt.core.Globals;
import com.bgt.frame.TipFrame;
import com.bgt.model.TipTableModel;
import com.bgt.renderer.HeaderCellRenderer;
import com.bgt.renderer.JTipTextFieldRenderer;

public class TipJTable extends JTable 
{
	private static final long serialVersionUID = 1L;
	private static boolean 	  adjustedWidth    = false;
	private TipTableModel     tipTmdl;
	private TipFrame          tipFrame;
	
	public static final int    SQUARE1_IX	= 0;
	public static final int    DANCER1_IX	= 1;
	public static final int    SQUARE2_IX	= 2;
	public static final int    DANCER2_IX	= 3;
	
	public TipJTable(TipFrame tipFrame) 
	{
		tipTmdl = new TipTableModel();
		this.setModel(tipTmdl);
		tipTmdl.setTable(this);
		this.tipFrame = tipFrame;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth()
    {
        return getPreferredSize().width < getParent().getWidth();
    }
	
	@Override
	public boolean getScrollableTracksViewportHeight()
    {
		if(!adjustedWidth && getParent().getHeight() > 900)
		{
			this.getColumnModel().getColumn(SQUARE1_IX).setMaxWidth(140);
			this.getColumnModel().getColumn(SQUARE1_IX).setMinWidth(140);
			this.getColumnModel().getColumn(SQUARE2_IX).setMaxWidth(140);
			this.getColumnModel().getColumn(SQUARE2_IX).setMinWidth(140);
			adjustedWidth = true;
		}
		//System.out.println("getPreferredSize().height/getParent().getHeight():  " + getPreferredSize().height + "/" + getParent().getHeight());
        return getPreferredSize().height < getParent().getHeight();
    }
	
	public void setUpTableModel(ArrayList<ArrayList<Object>>data, String[]col)
	{
		if(data == null) return;
		
		// map 2 columns onto 4 columns for display, so
		//
		// s1 d1                s1 d1 s3 d3
		// s2 d2    becomes:    s2 d2 s4 d4
		// s3 d3
		// s4 d4
		
		// the 2 columns are:  
		//   (s-) square no 
		//   (d-) names of dancers in the couple 
		//
		// third column, information-only; not mapped
		//	 boolean:  true means single, false means couple

		Object[][] newData = new Object[(data.size()/2)+1][4];
		int midway = (data.size() / 2) + (data.size() % 2) - 1; 
		for(int ix = 0, jx = 0, kx = 0; ix < data.size(); ix++)
		{
			if(ix > midway)	// fill left 3 columns first
			{
				jx = ix - midway - 1;
				kx = 2;		// filling out the second 2 columns
			}
			else			// halfway through, switch to right 2 columns
			{
				jx = ix;
				kx = 0;		// filling out the first 2 columns
			}
			
			String square = (String)data.get(ix).get(0);		// square no, or string indicating no square assigned (out)
			newData[jx][kx++] = square;							
			if((Boolean)data.get(ix).get(2))	
				newData[jx][kx] = "s~" + data.get(ix).get(1);	// names (e.g. "Dick & Jane"), flagged with "s~" for single
			else
			if(square.equals(Globals.OUT) || square.equals(Globals.REQUESTED_OUT))
				newData[jx][kx] = "y~" + data.get(ix).get(1);	// names (e.g. "Dick & Jane"), flagged with "y~" for yellow (out)
			else
				newData[jx][kx] = data.get(ix).get(1);			// names (e.g. "Dick & Jane"), normal, not flagged
		}

		Vector<Vector<Object>> mdlVector = new Vector<Vector<Object>>();
		int ix = 0;
		for(Object[] newDataRow : newData)
		{
			if(newDataRow != null && newDataRow[0] != null) mdlVector.add(ix++, new Vector<Object>(Arrays.asList(newDataRow)));
		}

		tipTmdl.setDataVector(mdlVector, new Vector<String>(Arrays.asList(col)));

		// set up editors, renderers, heights and widths for table columns
		this.setGridColor(Color.gray);
		this.getColumnModel().getColumn(SQUARE1_IX).setCellRenderer(new JTipTextFieldRenderer(true));
		this.getColumnModel().getColumn(SQUARE1_IX).setMaxWidth(130);
		this.getColumnModel().getColumn(SQUARE1_IX).setMinWidth(130);
		this.getColumnModel().getColumn(DANCER1_IX).setCellRenderer(new JTipTextFieldRenderer());
		this.getColumnModel().getColumn(SQUARE2_IX).setCellRenderer(new JTipTextFieldRenderer(true));
		this.getColumnModel().getColumn(SQUARE2_IX).setMaxWidth(130);
		this.getColumnModel().getColumn(SQUARE2_IX).setMinWidth(130);
		this.getColumnModel().getColumn(DANCER2_IX).setCellRenderer(new JTipTextFieldRenderer());
		this.getTableHeader().setDefaultRenderer(new HeaderCellRenderer(true, 24f));
		this.getTableHeader().setBackground(Globals.MEDIUM_BLUE);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setFillsViewportHeight(true);
		
		//System.out.println("jTable preferred size:  " + this.getPreferredSize() + ", jTable rowHeight:  " + this.getRowHeight());
	}
	
	public TipFrame getTipFrame()
	{
		return tipFrame;
	}
}
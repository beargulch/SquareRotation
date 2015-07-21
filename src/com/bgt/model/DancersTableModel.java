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

package com.bgt.model;

import java.io.Serializable;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.bgt.core.Dancer;
import com.bgt.core.Globals;

public class DancersTableModel extends DefaultTableModel implements Serializable
{ 
	private static final long serialVersionUID = 1L;
	private int lastRow;

	public DancersTableModel()
	{
		System.out.println("instantiate DancersTableModel");
		
		lastRow = -1;
		
		this.addTableModelListener(new TableModelListener() 
		{  
			public void tableChanged(TableModelEvent e) 
			{ 
				lastRow = e.getLastRow();
				if(Globals.getInstance().getCoupleGenerator() != null) Globals.getInstance().getCoupleGenerator().tableModelChanged(lastRow);
			}
		});
		
		for(String col : Dancer.dancerCol) this.addColumn(col);	// set up the columns in the model
	}
	
	public int getLastRow()
	{
		return lastRow;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Vector<Vector<Object>>getDataVector()
	{
		return (Vector<Vector<Object>>)dataVector;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public Class getColumnClass(int column) 
    {
		if(column < Dancer.getColumnCount()) return Dancer.getColumnClass(column);
		
		return getValueAt(0, column).getClass();	// backup plan; should never be reached
    }
	
	@Override
    public boolean isCellEditable(int row, int column) 
	{    
        return Dancer.isCellEditable(row, column);
    }
}
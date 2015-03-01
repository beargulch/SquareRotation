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

import javax.swing.table.DefaultTableModel;

import com.bgt.jtable.TipJTable;

public class TipTableModel extends DefaultTableModel
{ 
	private static final long serialVersionUID = 1L;
	private TipJTable jTable = null;

	public TipTableModel() {}
	
	public void setTable(TipJTable jTable)
	{
		this.jTable = jTable;
	}
	
	public TipJTable getTable()
	{
		return this.jTable;
	}
}
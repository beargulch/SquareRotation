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

package com.bgt.renderer;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

import com.bgt.core.Globals;

public class HeaderCellRenderer implements TableCellRenderer, Serializable
{
	private static final long serialVersionUID = 1L;

	private boolean centerText;
	private float   fontSize;
	
	public HeaderCellRenderer(float fontSize)
	{
		super();
		this.centerText = false;
		this.fontSize   = fontSize;
	}
	
	public HeaderCellRenderer(boolean centerText, float fontSize)
	{
		super();
		this.centerText = centerText;
		this.fontSize   = fontSize;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{			
		JLabel jLabel = new JLabel();
		
		if(column == 0)
			jLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		else
			jLabel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
		
		
		SortOrder sortOrder = getColumnSortOrder(jTable, column);
		if(sortOrder != SortOrder.UNSORTED)
		{
			jLabel.setHorizontalTextPosition(SwingConstants.LEADING);
		    jLabel.setIcon(SortOrder.ASCENDING == sortOrder ? UIManager.getIcon("Table.ascendingSortIcon") : UIManager.getIcon("Table.descendingSortIcon"));
		}
		if(value != null) jLabel.setText(value.toString());

		if(this.centerText) jLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		jLabel.setFont(jLabel.getFont().deriveFont(fontSize));
		jLabel.setBackground(Globals.MEDIUM_BLUE);
		jLabel.setOpaque(true);
				
		return jLabel;
	}

	private SortOrder getColumnSortOrder(JTable table, int column) 
	{
		if (table == null || table.getRowSorter() == null) {
			return SortOrder.UNSORTED;
		}
		List<? extends RowSorter.SortKey>keys = table.getRowSorter().getSortKeys();
		if (keys.size() > 0) {
			RowSorter.SortKey key = (RowSorter.SortKey)keys.get(0);
			if (key.getColumn() == table.convertColumnIndexToModel(column)) {
				return key.getSortOrder();
			}
		}
		return SortOrder.UNSORTED;
	}
}
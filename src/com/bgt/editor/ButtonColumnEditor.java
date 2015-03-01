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

package com.bgt.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.bgt.core.Globals;
import com.bgt.model.DancersTableModel;

/**
 *  The ButtonColumn class provides a renderer and an editor that looks like a
 *  JButton. The renderer and editor will then be used for a specified column
 *  in the table. The TableModel will contain the String to be displayed on
 *  the button.
 *
 *  The button can be invoked by a mouse click or by pressing the space bar
 *  when the cell has focus. Optionally a mnemonic can be set to invoke the
 *  button. When the button is invoked the provided Action is invoked. The
 *  source of the Action will be the table. The action command will contain
 *  the model row number of the button that was clicked.
 *
 */

public class ButtonColumnEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener
{
	private final static long serialVersionUID = 1L;
	
	private JTable jTable;
	private Action action;
	private int mnemonic;

	private JButton renderButton;
	private JButton editButton;
	private Object  editorValue;
	
	private int     highlightSpec;
	
	private String  on;  // text while button is "on"
	private String  off; // text while button is "off"
	
	/**
	 *  Create the ButtonColumn to be used as a renderer and editor. The
	 *  renderer and editor will automatically be installed on the TableColumn
	 *  of the specified column.
	 *
	 *  @param jTable the jTable containing the button renderer/editor
	 *  @param action the Action to be invoked when the button is invoked
	 *  @param column the column to which the button renderer/editor is added
	 */
	public ButtonColumnEditor(JTable jTable, Action action, int column, String on, String off, int highlightSpec)
	{
		this.jTable = jTable;
		this.action = action;
		this.on     = on;
		this.off    = off;
		this.highlightSpec = highlightSpec;

		renderButton = new JButton();
		editButton   = new JButton();
		editButton.setFocusPainted(false);
		editButton.addActionListener(this);

		TableColumnModel columnModel = jTable.getColumnModel();
		columnModel.getColumn(column).setCellRenderer(this);
		columnModel.getColumn(column).setCellEditor(this);
	}

	public int getMnemonic()
	{
		return mnemonic;
	}

	/**
	 *  The mnemonic to activate the button when the cell has focus
	 *
	 *  @param mnemonic the mnemonic
	 */
	public void setMnemonic(int mnemonic)
	{
		this.mnemonic = mnemonic;
		renderButton.setMnemonic(mnemonic);
		editButton.setMnemonic(mnemonic);
	}

	@Override
	public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column)
	{	
		editButton.setIcon(null);
		this.editorValue = value;
		return editButton;
	}

	@Override
	public Object getCellEditorValue()
	{
		return editorValue;
	}

	// Implement TableCellRenderer interface
	@SuppressWarnings({ "rawtypes" })
	public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		int modelRow = jTable.convertRowIndexToModel(row);
		Boolean colValue = (Boolean)((Vector)((DancersTableModel)jTable.getModel()).getDataVector().get(modelRow)).get(column);
		
		//System.out.println("colValue is " + colValue + " ,this.off is " + this.off + ", this.on is " + this.on + 
		//		           ", row = " + row + ", col = " + column + ", modelRow = " + modelRow);
		
		if(colValue == null || !colValue)
		{
			// System.out.println("1 setText off");
			
			if(highlightSpec == Globals.HIGHLIGHT_OFF_BTN)
				renderButton.setBackground(Globals.VERY_LIGHT_RED);
			else
				noHighLight(isSelected, row);
			
			renderButton.setText(this.off);
			renderButton.setOpaque(true);
		}
		else
		{
			// System.out.println("1 setText on");
			
            if(highlightSpec == Globals.HIGHLIGHT_ON_BTN)
            	renderButton.setBackground(Globals.VERY_LIGHT_RED);
            else
            	noHighLight(isSelected, row);
            
			renderButton.setText(this.on);
			renderButton.setOpaque(true);
		}
		
		return renderButton;
	}
	
	private void noHighLight(boolean isSelected, int row)
	{
		if(isSelected)
			renderButton.setBackground(Globals.VERY_LIGHT_BLUE);
		else
			if(row%2 == 0)
				renderButton.setBackground(Globals.VERY_LIGHT_GREY);
			else
				renderButton.setBackground(Color.white);
	}

	//  Implement ActionListener interface.  The button has been pressed. Stop editing and invoke the custom Action
	 
	public void actionPerformed(ActionEvent e)
	{
		int row = jTable.convertRowIndexToModel(jTable.getEditingRow());
		fireEditingStopped();

		//  Invoke the Action

		ActionEvent event = new ActionEvent(jTable, ActionEvent.ACTION_PERFORMED, Integer.toString(row));
		action.actionPerformed(event);
	}
}


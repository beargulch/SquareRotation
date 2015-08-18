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

import java.awt.Component;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

import com.bgt.core.Dancer;
import com.bgt.jtable.DancersJTable;
import com.bgt.model.DanceComboBoxModel;
import com.bgt.model.DancersTableModel;

public class PartnerComboBoxEditor extends DefaultCellEditor
{
	private static final long serialVersionUID = 1L;
	
	public PartnerComboBoxEditor(final JComboBox<String>comboBox) 
	{	
		super(comboBox);
		System.out.println("instantiate PartnerComboBoxEditor");
		
		// the delegate is used to translate the selectedIndex, retrieved from the comboBox,
		// to a pointer to the correct entry in the jtable's data model.  if this were not
		// done, the value returned from the comboBox select action would be a string value
		// painted by the comboBox renderer, which is responsible for turning integers into 
		// names.
		//
		// it's additionally responsible for the following updates:
		//
		// 1.  it sets the Partner column of the dancer selected to be the current dancer's 
		//     partner to the current dancer; and
		//
		// 2.  if the current dancer already had a partner, it removes the current dancer
		//     from the former partner's Partner column.
		
		delegate = new EditorDelegate() 
		{
			private static final long serialVersionUID = 1L;

			public Object getCellEditorValue() 
		    {
				System.out.println("PartnerComboBoxEditor EditorDelegate()");
				DanceComboBoxModel comboBoxModel = (DanceComboBoxModel)comboBox.getModel();
				ConcurrentHashMap<Integer, Integer>cmap = comboBoxModel.getComboMap();
				DancersTableModel dancersTmdl = (DancersTableModel)DancersJTable.getInstance().getModel();
				
				// cmap makes it possible to translate the selectedIndex to a pointer to 
				// the model (see comments in DanceComboBoxModel for more information).
				
				int selectedPartnerIx = (Integer)cmap.get(comboBox.getSelectedIndex());	
				
				if(comboBoxModel.getPreviousPartnerIx() >= 0)	// set in getTableCellEditor, below (gets called first)
				{
					int previousPartnerIx = comboBoxModel.getPreviousPartnerIx();
					dancersTmdl.setValueAt(-1, previousPartnerIx, Dancer.PARTNER_IX);
					comboBoxModel.setPreviousPartnerIx(-1);	// no more previous partner
				}
				
				// set the partner of the new partner to the current dancer.

				int row = comboBoxModel.getCurrentRow();
				if(selectedPartnerIx > -1 && (Integer)dancersTmdl.getValueAt(selectedPartnerIx, Dancer.PARTNER_IX) != row)
					dancersTmdl.setValueAt(row, selectedPartnerIx, Dancer.PARTNER_IX);	
				
				//System.out.println("editor delegate returning " + comboBox.getSelectedIndex() + " translated to " + partnerIx);
				return new Integer(selectedPartnerIx);	
		    }
		};
	}

	public Component getTableCellEditorComponent(JTable jTable, Object value, boolean isSelected, int row, int column) 
	{
		System.out.println("PartnerComboBoxEditor getTableCellEditorComponent, value = " + (value == null ? "null" : value.toString())  + 
				           ", isSelected: " + isSelected + ", row/col " + row + "/" + column + ", got jTable =  " + 
				           (jTable == null ? "No" : "Yes"));

		@SuppressWarnings("unchecked")
		JComboBox<String>comboBox = (JComboBox<String>)this.getComponent();
		DanceComboBoxModel comboBoxModel = (DanceComboBoxModel)comboBox.getModel();
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();

		int dancerIx = jTable.convertRowIndexToModel(row);
		
		//System.out.println("dancerIx =" + dancerIx + "partnerIx = " + (Integer)dancerData.get(dancerIx).get(Dancer.PARTNER_IX));
		
		// these items are needed for updates performed above in the delegate, and are passed in the comboBoxModel 
		//as a matter of convenience.	
		comboBoxModel.setCurrentRow(dancerIx);
		comboBoxModel.setPreviousPartnerIx((Integer)dancerData.get(dancerIx).get(Dancer.PARTNER_IX));
		comboBoxModel.setDancerIx(dancerIx);
		comboBoxModel.populateModel(row);
		
		return comboBox;
	}
}
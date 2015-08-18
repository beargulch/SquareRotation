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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.DefaultComboBoxModel;

import com.bgt.core.Dancer;
import com.bgt.jtable.DancersJTable;

public class DanceComboBoxModel extends DefaultComboBoxModel<String>
{
	// the model for the combo box is hijacked to communicate between the comboBoxEditor
	// and the comboBoxEditor delegate, which is the reason it stores additional values
	// (comboMap, previousPartner, currentRow, and tableModel).
	
	private static final long serialVersionUID = 1L;
	
	private ConcurrentHashMap<Integer, Integer>comboMap = null;
	
	private int previousPartnerIx	= -1;
	private int currentRow    		= -1;
	private int dancerIx        	= -1;

	public DanceComboBoxModel() 
	{
		// the names displayed in the comboBox (the one used to select partners)
		// come from the underlying table model, but knowing the index or position
		// of a partner displayed in the comboBox does not directly provide an
		// index to the table model to get the name.  this map, which is built in the
		// comboBoxEditor at the time the comboBox is populated with names from the
		// table model, relates a position or index in the comboBox to its associated 
		// element in the table model.
		
		this.comboMap = new ConcurrentHashMap<Integer, Integer>(100, 100);
	}

	private class ComboNameList implements Comparable<ComboNameList>
	{
		private String name;
		private int    modelIndex;
		
		public ComboNameList(String name, int modelIndex)
		{
			this.name = name;
			this.modelIndex = modelIndex;
		}
		
		public int compareTo(ComboNameList element) 
		{
		    return this.name.compareToIgnoreCase(element.name);	 
		}
	}

	public ConcurrentHashMap<Integer, Integer>getComboMap() 
	{
		return this.comboMap;
	}

	public int getDancerIx()
	{
		// System.out.println("combobox returning previous partner " + previousPartnerIx);
		return this.dancerIx;
	}
	
	public void setDancerIx(int dancerIx)
	{
		// System.out.println("combobox dancerIx set to " + dancerIx);
		this.dancerIx = dancerIx;
	}

	public int getPreviousPartnerIx()
	{
		// System.out.println("combobox returning previous partner " + previousPartnerIx);
		return this.previousPartnerIx;
	}
	
	public void setPreviousPartnerIx(int previousPartnerIx)
	{
		// System.out.println("combobox previous partner set to " + previousPartnerIx);
		this.previousPartnerIx = previousPartnerIx;
	}

	public int getCurrentRow()
	{
		// System.out.println("combobox returning currentRow " + currentRow);
		return this.currentRow;
	}
	
	public void setCurrentRow(int currentRow)
	{
		// System.out.println("combobox currentRow set to " + currentRow);
		this.currentRow = currentRow;
	}

	public void populateModel(int currentDancerRow)
	{
		DancersTableModel dancersTmdl = (DancersTableModel)DancersJTable.getInstance().getModel();
		List<ComboNameList>nameList = new ArrayList<ComboNameList>(dancersTmdl.getRowCount()+1);
		
		for(int ix = 0; ix < dancersTmdl.getRowCount(); ix++)
		{
			if(ix != currentDancerRow)
			{ 
				boolean alreadyPartnered = false;
				for(int kx = 0; kx < dancersTmdl.getRowCount(); kx++)	
				{
					// is this person already somebody's partner?
					if(kx == (Integer)dancersTmdl.getValueAt(ix, Dancer.PARTNER_IX))
					{
						alreadyPartnered = true;	// yes, already somebody's partner
						break;
					}
				}
				if(alreadyPartnered) continue;		// already partnered, so don't add to model or map.

				nameList.add(new ComboNameList((String)dancersTmdl.getValueAt(ix, Dancer.NAME_IX), ix));
			}
		}
		
		// when we populate the combo box model with dancers, we need a way to relate
		// the dancers back to the DancersTableModel they came from.  that's the purpose
		// of cmap, defined below.
		ConcurrentHashMap<Integer, Integer>cmap = this.getComboMap();
		cmap.clear();		// clear the translation map
		cmap.put(0, -1);	// first option is blank, which is used to remove a partner
		
		this.removeAllElements();			// clear the drop-down combo box
		this.addElement("(No Partner)");	// first option is blank, which is used to remove a partner
		
		Collections.sort(nameList);
		
		int comboIx = 1;
		for(ComboNameList element : nameList) 
		{
			cmap.put(comboIx, element.modelIndex);
			this.addElement(element.name);
			comboIx++;
		}
	}
}

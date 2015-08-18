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

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import com.bgt.core.Dancer;

public class BeauBelleComboBoxEditor extends DefaultCellEditor
{
	private static final long serialVersionUID = 1L;
	
	public BeauBelleComboBoxEditor(final JComboBox<String>comboBox) 
	{	
		super(comboBox);

		delegate = new EditorDelegate() 
		{
			private static final long serialVersionUID = 2L;

			public Object getCellEditorValue() 
		    {
				int rc = 2;
				String selectedItem = (String)comboBox.getModel().getSelectedItem();
				
				if(selectedItem.equals(Dancer.BEAU_STR))
					rc = 0;
				else
				if(selectedItem.equals(Dancer.BELLE_STR))
					rc = 1;
				else
					rc = 2;		// if it's not BEAU or BELLE, we default to EITHER.
		        
				return rc;	
		    }
		};
	}
}
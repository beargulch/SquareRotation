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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

import com.bgt.action.AtDanceAction;
import com.bgt.action.MustDanceAction;
import com.bgt.action.PresentAction;
import com.bgt.core.Dancer;
import com.bgt.core.Globals;
import com.bgt.editor.ButtonColumnEditor;
import com.bgt.editor.JComboBoxEditor;
import com.bgt.frame.EditDancerFrame;
import com.bgt.model.DanceComboBoxModel;
import com.bgt.model.DancersTableModel;
import com.bgt.renderer.BeauBelleRenderer;
import com.bgt.renderer.HeaderCellRenderer;
import com.bgt.renderer.JCheckBoxRenderer;
import com.bgt.renderer.JTableCellComboBoxRenderer;
import com.bgt.renderer.JTextFieldRenderer;

public class DancersJTable extends JTable implements MouseListener
{
	private static final long serialVersionUID = 1L;
	
	private static DancersJTable instance;

	protected DancersJTable() 
	{
		this.addMouseListener(this);
		this.setSize(2000, 500);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	
	public static DancersJTable getInstance()
	{
		if(instance == null)
		{
			instance = new DancersJTable();	
		}
		return instance;
	}

	public void setTableModel(DancersTableModel dancersTmdl)
	{
		this.setModel(dancersTmdl);
		this.setUpTableModel(null);
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth()
    {
        return getPreferredSize().width < getParent().getWidth();
    }
	
	@Override
	public boolean getScrollableTracksViewportHeight()
    {
        return getPreferredSize().height < getParent().getHeight();
    }
	
	public void setUpTableModel(Object[][]data)
	{
		Vector<Vector<Object>>dancerVector;
		if(data != null)
		{
			dancerVector = new Vector<Vector<Object>>(data.length, (data.length < 100 ? 100 : data.length));
			for(Object[]dancer : data) dancerVector.add(new Vector<Object>(Arrays.asList(dancer)));
		}
		else
			dancerVector = new Vector<Vector<Object>>(100, 100);

		((DancersTableModel)this.getModel()).setDataVector(dancerVector, new Vector<String>(Arrays.asList(Dancer.dancerCol)));
		this.setUpTablePresentation();
	}
	
	public Vector<Vector<Object>>getDancerData()
	{
		return ((DancersTableModel)getModel()).getDataVector();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setUpTablePresentation()
	{
		TableRowSorter<DancersTableModel>sorter = new TableRowSorter<DancersTableModel>(((DancersTableModel)this.getModel()));
		
		/*
		RowFilter<DancersTableModel, Integer> activeFilter = new RowFilter<DancersTableModel, Integer>() 
		{
			public boolean include(RowFilter.Entry entry)
			{
				if (!(Boolean)entry.getValue(Dancer.DANCER_AT_DANCE_IX)) return false;	// don't show dancer not at dance
				
				return true;	// do show active dancer
			}
		};
		sorter.setRowFilter(activeFilter);
		*/
		
		sorter.setSortable(Dancer.DANCING_IX, false);
		sorter.setSortable(Dancer.MUST_DANCE_IX, false);
		sorter.setSortable(Dancer.WILLING_SINGLE_IX, false);
		sorter.setSortable(Dancer.DANCER_OUTS_IX, false);
		sorter.setSortable(Dancer.DANCER_AT_DANCE_IX, false);
		sorter.setComparator(Dancer.PARTNER_IX, new Comparator()
		{
			@Override
			public int compare(Object o1, Object o2) 
			{
				// partners are stored by index, but when they are sorted for display
				// we need to sort them by the alpha name value pointed to by the index.
				// this instance of "compare" resolves an index to a name value before 
				// comparing
				
				int i1 = (Integer)o1;
				int i2 = (Integer)o2;
				
				if(i1 > -1 && i2 > -1)
				{
					String sx1 = (String)((DancersTableModel)getModel()).getDataVector().get(i1).get(Dancer.NAME_IX);
					String sx2 = (String)((DancersTableModel)getModel()).getDataVector().get(i2).get(Dancer.NAME_IX);
					return sx1.compareToIgnoreCase(sx2);
				}
				if(i1 < 0 && i2 < 0) return 0;
				
				if(i2 > -1) return -1;
				return 1;
			}
		});
        
		this.setRowSorter(sorter);
		
		this.setGridColor(Color.black);

		// set up editors and renderers for table columns
		
		// Name is a simple text field
		this.getColumn(Dancer.NAME_STR).setCellRenderer(new JTextFieldRenderer());
		
		// Beau/Belle requires translation from stored Integer value to String.
		this.getColumn(Dancer.ROLE_STR).setCellRenderer(new BeauBelleRenderer());

		// Partner is a drop-down
		this.getColumn(Dancer.PARTNER_STR).setCellRenderer(new JTableCellComboBoxRenderer());
		JComboBox partnerBox = new JComboBox();
		DanceComboBoxModel partnerBoxMdl = new DanceComboBoxModel();
		partnerBox.setModel(partnerBoxMdl);
		JComboBoxEditor jcEditor = new JComboBoxEditor(partnerBox);
		jcEditor.setClickCountToStart(2);
		this.getColumn(Dancer.PARTNER_STR).setCellEditor(jcEditor);
		
		// Present is a button
		this.getColumn(Dancer.DANCING_STR).setCellEditor(new ButtonColumnEditor(this, new PresentAction(), Dancer.DANCING_IX, "Dancing", "Out", Globals.HIGHLIGHT_OFF_BTN));
		
		// Must Dance is a radio button
		this.getColumn(Dancer.MUST_DANCE_STR).setCellEditor(new ButtonColumnEditor(this, new MustDanceAction(), Dancer.MUST_DANCE_IX, "Dance!", "Normal", Globals.HIGHLIGHT_ON_BTN));
		
		// Willing Single is a checkbox
		this.getColumn(Dancer.WILLING_SINGLE_STR).setCellRenderer(new JCheckBoxRenderer());
		
		// Dancer Outs is a simple text field, with text centered
		this.getColumn(Dancer.DANCER_OUTS_STR).setCellRenderer(new JTextFieldRenderer(true));
		
		// At Dance is a radio button
		this.getColumn(Dancer.DANCER_AT_DANCE_STR).setCellEditor(new ButtonColumnEditor(this, new AtDanceAction(), Dancer.DANCER_AT_DANCE_IX, "Yes", "No", Globals.HIGHLIGHT_OFF_BTN));
		
		// set custom row height; the default height does not accommodate radio buttons 
		this.setRowHeight(30);

		this.getTableHeader().setDefaultRenderer(new HeaderCellRenderer(true, 14f));
		
		// remove from the JTable (but not from the table model) things we don't want to display
		this.removeColumn(this.getColumn(Dancer.DANCER_DANCED_STR));
		this.removeColumn(this.getColumn(Dancer.DANCER_SELECTED_STR));
	}	
	
    public void mousePressed(MouseEvent e)
    {
    	if (e.getClickCount() == 2) 
    	{	
    		JTable target = (JTable)e.getSource();
    		
    		if(target.getSelectedColumn() < 2)
    		{    			
    			new EditDancerFrame(target.convertRowIndexToModel(target.getSelectedRow()));
    		}
        }
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked (MouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
    public void mouseExited  (MouseEvent e) {}
}

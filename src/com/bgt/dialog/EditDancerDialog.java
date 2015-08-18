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

package com.bgt.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.bgt.core.Dancer;
import com.bgt.frame.MainFrame;
import com.bgt.jtable.DancersJTable;
import com.bgt.listener.DeleteDancerListener;
import com.bgt.listener.SaveDancerListener;
import com.bgt.listener.SelectDancerListener;
import com.bgt.model.DanceComboBoxModel;
import com.bgt.model.DancersTableModel;

public class EditDancerDialog extends JDialog
{
	private static final long     serialVersionUID = 1L;
	
	private int dancer1RowIX = -1;
	private int dancer2RowIX = -1;
	
	private Vector<Object>dancer1Row;
	private Vector<Object>dancer2Row;
	
	private JPanel 	   mainPanel;
	private JTextField dancer1;
	private JCheckBox  jPresent1;
	private JCheckBox  jAtDance1;
	private JTextField outs1;
	private JCheckBox  jMustDance1;
	private JCheckBox  jWillingSingle1;
	private JCheckBox  jPartners;
	private JTextField dancer2;
	private JTextField outs2;
	private JCheckBox  jPresent2;
	private JCheckBox  jAtDance2;
	private JCheckBox  jMustDance2;
	private JCheckBox  jWillingSingle2;
	private JLabel 	   dancerError;
	private JPanel 	   pane1;
	private JPanel 	   pane6;
	
	private boolean    dancer2TextBox = true;

	private JComboBox<String>beauBelleBox1;
	private JComboBox<String>beauBelleBox2;
	private JComboBox<String>partnerBox;
	
	public EditDancerDialog()
	{
		super(MainFrame.getInstance(), "", Dialog.ModalityType.DOCUMENT_MODAL);
		this.setDancer1RowIX(-1);
		this.setDancer2RowIX(-1);
		setUpPanel();
	}
	
	public EditDancerDialog(int row) 
	{
		super(MainFrame.getInstance(), "", Dialog.ModalityType.DOCUMENT_MODAL);
		this.setDancer1RowIX(row);
		
		// note that in this constructor, the dancer in the row that is
		// selected is in the top of the edit frame, and controls what
		// dancer (if any) is in the bottom of the edit frame through the
		// value stored in the column identified by Dancer.PARTNER_IX.  
		
		// if everything is working properly, dancers who are partners 
		// should point to each other through the value stored in the 
		// column identified by Dancer.PARTNER_IX.  if they do not, it's
		// a bug that is not really handled.
		
		if(this.dancer1Row != null)
			this.setDancer2RowIX((Integer)dancer1Row.get(Dancer.PARTNER_IX));
		else
			this.setDancer2RowIX(-1);

		setUpPanel();
	}

	//@SuppressWarnings("unchecked")
	private void setUpPanel()
	{
		mainPanel 		= new JPanel(new GridBagLayout());
		
		dancer1  		= new JTextField("", 20);
		jPresent1 		= new JCheckBox("Dancing");
		jAtDance1 		= new JCheckBox("At the Dance");
		outs1  			= new JTextField(2);
		jMustDance1 	= new JCheckBox("Must Dance");
		jWillingSingle1 = new JCheckBox("Willing to fill in as single if couple is out");
		beauBelleBox1 	= new JComboBox<String>(Dancer.beauBelleOptions);
        JButton btnd1   = new JButton("Delete 1");
		jPartners 		= new JCheckBox("Are Dancer 1 and Dancer 2 dance partners?");
		dancer2 		= new JTextField(20);
		partnerBox		= new JComboBox<String>();
		jPresent2 		= new JCheckBox("Dancing");
		jAtDance2 		= new JCheckBox("At the Dance");
		outs2  			= new JTextField(2);
		jMustDance2 	= new JCheckBox("Must Dance");
		jWillingSingle2 = new JCheckBox("Willing to fill in as single if couple is out");
		beauBelleBox2 	= new JComboBox<String>(Dancer.beauBelleOptions);
        JButton btnd2   = new JButton("Delete 2");
		dancerError 	= new JLabel("Duplicate Name");
		dancerError.setForeground(Color.RED);
		
		Dimension minSize  = new Dimension(10, 18);
		Dimension prefSize = new Dimension(10, 18);
		Dimension maxSize  = new Dimension(10, 18);
		
		pane1 = new JPanel();
		pane1.setLayout(new BoxLayout(pane1, BoxLayout.LINE_AXIS));
		pane1.setAlignmentX(Component.LEFT_ALIGNMENT);
		dancer1.setMaximumSize  (new Dimension(300,30));
		dancer1.setPreferredSize(new Dimension(300,30));
		dancer1.setMinimumSize  (new Dimension(300,30));
		pane1.add(new Box.Filler(minSize, prefSize, maxSize));
		pane1.add(new JLabel("Dancer 1 Name: "));
		pane1.add(dancer1);
		GridBagConstraints c1 = new GridBagConstraints();
	    c1.gridy  = 1;
	    c1.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(pane1, c1);

		JPanel pane2 = new JPanel(new GridBagLayout());
		
		GridBagConstraints c2a = new GridBagConstraints();
	    c2a.insets = new Insets(0,0,0,100);  //right padding
		pane2.add(jPresent1, c2a);
		
		GridBagConstraints c2b = new GridBagConstraints();
	    c2b.gridx = 2;
	    pane2.add(new JLabel("Dancer 1 Outs: "), c2b);
	    
	    GridBagConstraints c2c = new GridBagConstraints();
	    c2c.gridx = 3;
	    outs1.setMaximumSize(new Dimension  (45,30));
	    outs1.setPreferredSize(new Dimension(45,30));
	    outs1.setMinimumSize(new Dimension  (45,30));
		pane2.add(outs1, c2c);
		
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridy   = 2;
	    c2.weightx = 1;
	    c2.anchor  = GridBagConstraints.LINE_START;
		mainPanel.add(pane2, c2);
		
		GridBagConstraints c3a = new GridBagConstraints();
	    c3a.gridy  = 3;
	    c3a.anchor = GridBagConstraints.LINE_START;
	    mainPanel.add(jAtDance1, c3a);
		
		GridBagConstraints c3 = new GridBagConstraints();
	    c3.gridy  = 4;
	    c3.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(jMustDance1, c3);
		
		GridBagConstraints c4 = new GridBagConstraints();
	    c4.gridy  = 5;
	    c4.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(jWillingSingle1, c4);
		
		JPanel pane5 = new JPanel();
		pane5.setLayout(new BoxLayout(pane5, BoxLayout.LINE_AXIS));
		pane5.setAlignmentX(Component.LEFT_ALIGNMENT);
		beauBelleBox1.setMinimumSize  (new Dimension(100,30));
		beauBelleBox1.setMaximumSize  (new Dimension(100,30));
		beauBelleBox1.setPreferredSize(new Dimension(100,30));
		pane5.add(new Box.Filler(minSize, prefSize, maxSize));
		pane5.add(new JLabel("Preferred position as single: "));
		pane5.add(beauBelleBox1);
		pane5.add(btnd1);
		GridBagConstraints c5 = new GridBagConstraints();
	    c5.gridy  = 6;
	    c5.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(pane5, c5);
		
		GridBagConstraints cS1 = new GridBagConstraints();
		cS1.gridy = 7;
		JSeparator sep1 = new JSeparator(JSeparator.HORIZONTAL);
		sep1.setMinimumSize(new Dimension  (400, 15));
		sep1.setMaximumSize(new Dimension  (400, 15));
		sep1.setPreferredSize(new Dimension(400, 15));
		sep1.setBackground(Color.LIGHT_GRAY);
	    mainPanel.add(sep1, cS1);

		// if partner is not currently set, display a drop-down to
		// allow user to choose a partner from that current list of 
		// un-partnered dancers.  if partner is set, populate a text 
		// box with the current partner name.
		pane6 = new JPanel();
		pane6.setLayout(new BoxLayout(pane6, BoxLayout.LINE_AXIS));
		pane6.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// this panel is used to both add and edit dancers.  
		//
		// if it's an add, then text boxes are used for both 
		// dancer 1 and dancer 2, so names can be entered for both.
		//
		// if it's an edit, dancer 1 is displayed in a text box so
		// it can be edited.  dancer 2, however, can be displayed
		// either in text box or a drop-down, depending on the
		// circumstances.
		//
		// if dancer 1 has an assigned partner when this frame is
		// invoked, then both dancer 1 and dancer 2 will be displayed
		// in text boxes, and both can be edited.
		//
		// if dancer 1 does not have an assigned partner when this 
		// frame is invoked, then should dancer 2 be a text box where
		// a new dancer can be added?  or should dancer 2 instead be a 
		// drop-down of existing dancers from which a partner can be 
		// selected for dancer 1?  for now, dancer 2 will be displayed 
		// as a drop-down in these circumstances, so instead of being 
		// able to enter a new dancer, dancer 2 must be chosen from 
		// among the dancers already entered.
		
		if(this.dancer1RowIX > -1 & this.dancer2RowIX < 0)
		{	
			DanceComboBoxModel partnerBoxMdl = new DanceComboBoxModel();
			partnerBoxMdl.populateModel(this.dancer1RowIX);
			partnerBox.setModel(partnerBoxMdl);
			pane6.add(this.partnerBox);
			this.dancer2TextBox = false;
		}
		else
		{
			dancer2.setMaximumSize  (new Dimension(300,30));
			dancer2.setPreferredSize(new Dimension(300,30));
			dancer2.setMinimumSize  (new Dimension(300,30));
			pane6.add(new Box.Filler(minSize, prefSize, maxSize));
			pane6.add(new JLabel("Dancer 2 Name: "));
			pane6.add(dancer2);
			this.dancer2TextBox = true;
		}
		GridBagConstraints c6 = new GridBagConstraints();
	    c6.gridy  = 8;
	    c6.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(pane6, c6);
		
		JPanel pane7 = new JPanel(new GridBagLayout());
		
		GridBagConstraints c7a = new GridBagConstraints();
		c7a.gridx  = 0;
	    c7a.insets = new Insets(0,0,0,100);
		pane7.add(jPresent2, c7a);	
		
		GridBagConstraints c7c = new GridBagConstraints();
	    c7c.gridx = 2;
	    pane7.add(new JLabel("Dancer 2 Outs: "), c7c);
	    
	    GridBagConstraints c7d = new GridBagConstraints();
	    c7d.gridx = 3;
	    outs2.setMaximumSize  (new Dimension(45,30));
	    outs2.setPreferredSize(new Dimension(45,30));
	    outs2.setMinimumSize  (new Dimension(45,30));
		pane7.add(outs2, c7d);
		
		GridBagConstraints c7 = new GridBagConstraints();
		c7.gridy   = 9;
	    c7.weightx = 1;
	    c7.anchor  = GridBagConstraints.LINE_START;
		
	    mainPanel.add(pane7, c7);
		
		GridBagConstraints c8a = new GridBagConstraints();
		c8a.gridy  = 10;
	    c8a.anchor = GridBagConstraints.LINE_START;
		pane7.add(jAtDance2, c8a);

		GridBagConstraints c8 = new GridBagConstraints();
	    c8.gridy  = 11;
	    c8.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(jMustDance2, c8);

		GridBagConstraints c9 = new GridBagConstraints();
	    c9.gridy  = 12;
	    c9.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(jWillingSingle2, c9);
		
		JPanel pane10 = new JPanel();
		pane10.setLayout(new BoxLayout(pane10, BoxLayout.LINE_AXIS));
		pane10.setAlignmentX(Component.LEFT_ALIGNMENT);
		beauBelleBox2.setMinimumSize  (new Dimension(100,30));
		beauBelleBox2.setMaximumSize  (new Dimension(100,30));
		beauBelleBox2.setPreferredSize(new Dimension(100,30));
		beauBelleBox2.setSelectedIndex(1);
		pane10.add(new Box.Filler(minSize, prefSize, maxSize));
		pane10.add(new JLabel("Preferred position as single: "));
		pane10.add(beauBelleBox2);
		pane10.add(btnd2);
		GridBagConstraints c10 = new GridBagConstraints();
	    c10.gridy  = 13;
	    c10.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(pane10, c10);
		
		GridBagConstraints cS2 = new GridBagConstraints();
		cS2.gridy = 14;
		JSeparator sep2 = new JSeparator(JSeparator.HORIZONTAL);
		sep2.setMinimumSize  (new Dimension(400, 15));
		sep2.setMaximumSize  (new Dimension(400, 15));
		sep2.setPreferredSize(new Dimension(400, 15));
		sep2.setBackground(Color.LIGHT_GRAY);
	    mainPanel.add(sep2, cS2);
	    
        JPanel pane5a = new JPanel();
        pane5a.setLayout(new BoxLayout(pane5a, BoxLayout.LINE_AXIS));
        pane5a.setAlignmentX(Component.LEFT_ALIGNMENT);
        pane5a.add(jPartners);
		GridBagConstraints c5a = new GridBagConstraints();
	    c5a.gridy = 15;
		mainPanel.add(pane5a, c5a);

	    GridBagConstraints cS3 = new GridBagConstraints();
		cS3.gridy = 16;
		JSeparator sep3 = new JSeparator(JSeparator.HORIZONTAL);
		sep3.setMinimumSize  (new Dimension(400, 30));
		sep3.setMaximumSize  (new Dimension(400, 30));
		sep3.setPreferredSize(new Dimension(400, 30));
		sep3.setBackground(Color.LIGHT_GRAY);
	    mainPanel.add(sep3, cS3);
		
		JPanel pane11 = new JPanel();
		pane11.setLayout(new BoxLayout(pane11, BoxLayout.LINE_AXIS));
		pane11.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton btn1 = new JButton("Save");
        JButton btn2 = new JButton("Cancel");
        pane11.add(btn1);
        pane11.add(btn2);
		GridBagConstraints c11 = new GridBagConstraints();
	    c11.gridy = 17; 
	    c11.anchor = GridBagConstraints.CENTER;
		mainPanel.add(pane11, c11);
		
        // populate fields with data
		
		jPresent1.setSelected(true);
		jAtDance1.setSelected(true);
		jPresent2.setSelected(true);
		jAtDance2.setSelected(true);       
        
		if(this.dancer1RowIX > -1)
		{
			dancer1.setText((String)dancer1Row.get(Dancer.NAME_IX));
			if((Boolean)dancer1Row.get(Dancer.DANCING_IX)) 
				jPresent1.setSelected(true);       
			else 
				jPresent1.setSelected(false);
			if((Boolean)dancer1Row.get(Dancer.DANCER_AT_DANCE_IX)) 
				jAtDance1.setSelected(true);       
			else 
				jAtDance1.setSelected(false);
			if((Boolean)dancer1Row.get(Dancer.MUST_DANCE_IX)) 
				jMustDance1.setSelected(true);     
			else 
				jMustDance1.setSelected(false);
			if((Boolean)dancer1Row.get(Dancer.WILLING_SINGLE_IX)) 
				jWillingSingle1.setSelected(true); 
			else 
				jWillingSingle1.setSelected(false);
			if((Integer)dancer1Row.get(Dancer.ROLE_IX) > -1) 
				beauBelleBox1.setSelectedIndex((Integer)dancer1Row.get(Dancer.ROLE_IX));
			outs1.setText(((Integer)dancer1Row.get(Dancer.DANCER_OUTS_IX)).toString());
		}

		if(this.dancer2RowIX > -1)
		{
			dancer2.setText((String)dancer2Row.get(Dancer.NAME_IX));
			if((Boolean)dancer2Row.get(Dancer.DANCING_IX)) 
				jPresent2.setSelected(true);       
			else 
				jPresent2.setSelected(false);
			if((Boolean)dancer2Row.get(Dancer.DANCER_AT_DANCE_IX)) 
				jAtDance2.setSelected(true);       
			else 
				jAtDance2.setSelected(false);
			if((Boolean)dancer2Row.get(Dancer.MUST_DANCE_IX)) 
				jMustDance2.setSelected(true);     
			else 
				jMustDance2.setSelected(false);
			if((Boolean)dancer2Row.get(Dancer.WILLING_SINGLE_IX)) 
				jWillingSingle2.setSelected(true); 
			else 
				jWillingSingle2.setSelected(false);
			if((Integer)dancer2Row.get(Dancer.ROLE_IX) > -1) 
				beauBelleBox2.setSelectedIndex((Integer)dancer2Row.get(Dancer.ROLE_IX));
			outs2.setText(((Integer)dancer2Row.get(Dancer.DANCER_OUTS_IX)).toString());
		}
		
		if(this.dancer1Row != null && (Integer)this.dancer1Row.get(Dancer.PARTNER_IX) > -1 &&
		   this.dancer2Row != null && (Integer)this.dancer2Row.get(Dancer.PARTNER_IX) > -1)
		{
			this.jPartners.setSelected(true);
		}
		else
		{
			this.jPartners.setSelected(false);
		}

		ActionListener selectDancer = new SelectDancerListener(this);
		partnerBox.addActionListener(selectDancer);

		ActionListener deleteDancer1 = new DeleteDancerListener(this, 1);
	    btnd1.addActionListener(deleteDancer1);

		ActionListener deleteDancer2 = new DeleteDancerListener(this, 2);
	    btnd2.addActionListener(deleteDancer2);

		ActionListener saveDancer = new SaveDancerListener(this);
	    btn1.addActionListener(saveDancer);
	    
	    btn2.addActionListener(new ActionListener() 
	    {
	    	@Override 
	    	public void actionPerformed(ActionEvent e) 
	    	{			
	    		EditDancerDialog.this.dispose();
	    	}
	    });

		this.setTitle("Edit Dancer");
		
	    // add content to the window.
	    this.add(mainPanel);
	    this.setPreferredSize(new Dimension(430,500));   
	    
	    // display the window
	    this.pack();
	    this.setVisible(true);
	}

    public int getDancer1RowIX()
    {
    	return this.dancer1RowIX;
    }
    public void setDancer1RowIX(int dancer1RowIX)
    {
    	DancersTableModel dancersTmdl = (DancersTableModel)DancersJTable.getInstance().getModel();
    	
    	this.dancer1RowIX = dancer1RowIX;
    	if(this.dancer1RowIX > -1 && dancersTmdl != null)
    	{
			this.dancer1Row = (Vector<Object>)dancersTmdl.getDataVector().get(this.dancer1RowIX);
    	}
		else
		{
    		this.dancer1Row = null;
		}
    }
    public int getDancer2RowIX()
    {
    	return this.dancer2RowIX;
    }
    public void setDancer2RowIX(int dancer2RowIX)
    {
    	DancersTableModel dancersTmdl = (DancersTableModel)DancersJTable.getInstance().getModel();
    	
    	this.dancer2RowIX = dancer2RowIX;
    	if(this.dancer2RowIX > -1 && dancersTmdl != null)
    	{
			this.dancer2Row = (Vector<Object>)dancersTmdl.getDataVector().get(this.dancer2RowIX);
    	}
		else
    	{
    		this.dancer2Row = null;
    	}
    }
    public Vector<Object>getDancer1Row()
    {
    	return this.dancer1Row;
    }
    public Vector<Object>getDancer2Row()
    {
    	return this.dancer2Row;
    }
    public JTextField getDancer1()
    {
    	return this.dancer1;
    }
    public JTextField getOuts1()
    {
    	return this.outs1;
    }
    public Integer getOuts1Value()
    {
    	return getInteger(this.outs1.getText());
    }
    public JCheckBox getJPresent1()
    {
    	return this.jPresent1;
    }
    public JCheckBox getJAtDance1()
    {
    	return this.jAtDance1;
    }
    public JCheckBox getJMustDance1()
    {
    	return this.jMustDance1;
    }
    public JCheckBox getJWillingSingle1()
    {
    	return this.jWillingSingle1;
    }
    public JComboBox<String> getBeauBelleBox1()
    {
    	return this.beauBelleBox1;
    }
    public JCheckBox getJPartners()
    {
    	return this.jPartners;
    }
    public JTextField getDancer2()
    {
    	return this.dancer2;
    }
    public JTextField getOuts2()
    {
    	return this.outs2;
    }
    public Integer getOuts2Value()
    {
    	return getInteger(this.outs2.getText());
    }
    public boolean isDancer2TextBox()
    {
    	return this.dancer2TextBox;
    }
    public JCheckBox getJPresent2()
    {
    	return this.jPresent2;
    }
    public JCheckBox getJAtDance2()
    {
    	return this.jAtDance2;
    }
    public JCheckBox getJMustDance2()
    {
    	return this.jMustDance2;
    }
    public JCheckBox getJWillingSingle2()
    {
    	return this.jWillingSingle2;
    }
    public JComboBox<String> getBeauBelleBox2() 
    {
    	return this.beauBelleBox2;
    }
    public void dancer1Error()
    {
    	pane1.add(this.dancerError);
    }
    public void clearDancer1Error()
    {
    	pane1.remove(this.dancerError);
    }
    public void dancer2Error()
    {
    	pane6.add(this.dancerError);
    }
    public void clearDancer2Error()
    {
    	pane6.remove(this.dancerError);
    }

    private Integer getInteger(String str)
	{
		Integer nbr;
		try
		{
			nbr = Integer.parseInt(str);
		}
		catch(NumberFormatException n)
		{
			nbr = null;
		}
		return nbr;
	}
}

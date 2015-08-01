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

package com.bgt.frame;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.bgt.core.CoupleGenerator;
import com.bgt.core.Dancer;
import com.bgt.jtable.DancersJTable;
import com.bgt.listener.DisplayTipListener;
import com.bgt.listener.EditDancerListener;
import com.bgt.listener.FileOpenListener;
import com.bgt.listener.FileSaveListener;
import com.bgt.listener.GenerateSquareListener;
import com.bgt.listener.ReGenerateSquareListener;
import com.bgt.listener.ResetListener;
import com.bgt.viewport.HeaderViewport;

public class MainFrame extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private JTextField txtTipNo            = null;
	private JTextField txtDancersPresent   = null;
	private JTextField txtDancersDancing   = null;
	private JTextField txtDancersOut       = null;
	private JTextField txtProjectedSquares = null;
	
	private static MainFrame instance;

	protected MainFrame()
    {
        this.setLayout(new GridBagLayout());
		
        // add the buttons to the left-hand side of the frame
		GridBagConstraints c1 = new GridBagConstraints();
        c1.weightx = 0;
        c1.weighty = 0;
        c1.gridx   = 0;
        c1.gridy   = 0;
        c1.anchor  = GridBagConstraints.FIRST_LINE_START;
        add(setupLeftHandPanel(), c1);
		
		// add the scrollPane with dancersJTable to the right-hand side of the frame
		GridBagConstraints c2 = new GridBagConstraints();
        c2.weightx = 1;
        c2.weighty = 1;
        c2.gridx   = 1;
        c2.gridy   = 0;
        c2.fill    = GridBagConstraints.BOTH;
        //JScrollPane pane = new JScrollPane(Globals.getInstance().getDancersJTable());
        JScrollPane pane = new JScrollPane(DancersJTable.getInstance());
		pane.setColumnHeader(new HeaderViewport()); 
        add(new JScrollPane(pane), c2);

        // add the menu bar along the top
       	setJMenuBar(createMenuBar());
       	
       	setTipNo();
       	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setSize(960, 800);
        setVisible(true);
    }
	
	public static MainFrame getInstance()
	{
		System.out.println("instantiate MainFrame 1");
		if(instance == null)
		{
			System.out.println("instantiate MainFrame 2");
			instance = new MainFrame();
			System.out.println("instantiate MainFrame 3");
		}
		System.out.println("instantiate MainFrame 4");
		return instance;
	}
	
	private JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		
        // create the file menu
        JMenu fileMenu  = new JMenu("File");
        
        JMenuItem menuOpen = new JMenuItem("Open");
        menuOpen.addActionListener(new FileOpenListener());
        menuOpen.setAlignmentY(Component.TOP_ALIGNMENT);
        menuOpen.setAlignmentX(Component.LEFT_ALIGNMENT);
        fileMenu.add(menuOpen);
        
        JMenuItem menuSave = new JMenuItem("Save");
        menuSave = new JMenuItem("Save");
        menuSave.addActionListener(new FileSaveListener());
        fileMenu.add(menuSave);
        
        // create the tools menu
        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem newTip     = new JMenuItem("Generate New Tip");
        newTip.addActionListener(new GenerateSquareListener());
        toolsMenu.add(newTip);
        
        JMenuItem reGenTip   = new JMenuItem("Regenerate Tip");
        reGenTip.addActionListener(new ReGenerateSquareListener());
        toolsMenu.add(reGenTip);
        
        JMenuItem displayTip = new JMenuItem("Display Current Tip");
        displayTip.addActionListener(new DisplayTipListener());
        toolsMenu.add(displayTip);
        
        JMenuItem editDancer = new JMenuItem("Edit Dancer");
        editDancer.addActionListener(new EditDancerListener(true));
        toolsMenu.add(editDancer);
        
        JMenuItem addDancer  = new JMenuItem("Add Dancer");
        addDancer.addActionListener(new EditDancerListener(false));
        toolsMenu.add(addDancer);
        
        JMenuItem setOptions  = new JMenuItem("Set Options");
        setOptions.addActionListener(new ActionListener() 
	    {
	    	@Override
	    	public void actionPerformed(ActionEvent evt) 
	    	{
	    		new OptionsFrame();
	    	}
	    });
        toolsMenu.add(setOptions);
        
        // create the help menu
        JMenu helpMenu  = new JMenu("Help");

        // add the options to the bar
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
	}
	
	private JPanel setupLeftHandPanel()
    {
        // Create an options pane
        final JPanel leftHandPanel = new JPanel(new GridBagLayout());
        
        JButton btn1 = new JButton("Open");
        JButton btn2 = new JButton("Save");
        JButton btn3 = new JButton("Generate New Tip");
        JButton btn4 = new JButton("Regenerate Tip");
        JButton btn5 = new JButton("Display Current Tip");
        JButton btn6 = new JButton("Edit Dancer");
        JButton btn7 = new JButton("Add Dancer");
        JButton btn8 = new JButton("Reset to Tip 1");
        
        txtTipNo = new JTextField();
        txtTipNo.setMinimumSize  (new Dimension(40,30));
        txtTipNo.setPreferredSize(new Dimension(40,30));
        txtTipNo.setMaximumSize  (new Dimension(40,30));
        txtTipNo.setHorizontalAlignment(JTextField.CENTER);
        JLabel tipLable = new JLabel("Current Tip");
        
    	txtDancersPresent = new JTextField();
    	txtDancersPresent.setMinimumSize  (new Dimension(40,30));
    	txtDancersPresent.setPreferredSize(new Dimension(40,30));
    	txtDancersPresent.setMaximumSize  (new Dimension(40,30));
    	txtDancersPresent.setHorizontalAlignment(JTextField.CENTER);
        JLabel dancersPresentLable = new JLabel("Dancers Present");
        
        txtDancersDancing   = new JTextField();
        txtDancersDancing.setMinimumSize  (new Dimension(40,30));
        txtDancersDancing.setPreferredSize(new Dimension(40,30));
        txtDancersDancing.setMaximumSize  (new Dimension(40,30));
        txtDancersDancing.setHorizontalAlignment(JTextField.CENTER);
        JLabel dancersDancingLable = new JLabel("Dancers Dancing");
        
    	txtDancersOut       = new JTextField();
    	txtDancersOut.setMinimumSize  (new Dimension(40,30));
    	txtDancersOut.setPreferredSize(new Dimension(40,30));
    	txtDancersOut.setMaximumSize  (new Dimension(40,30));
    	txtDancersOut.setHorizontalAlignment(JTextField.CENTER);
        JLabel dancersOutLable = new JLabel("Requested Outs");
        
    	txtProjectedSquares = new JTextField();
    	txtProjectedSquares.setMinimumSize  (new Dimension(40,30));
    	txtProjectedSquares.setPreferredSize(new Dimension(40,30));
    	txtProjectedSquares.setMaximumSize  (new Dimension(40,30));
    	txtProjectedSquares.setHorizontalAlignment(JTextField.CENTER);
        JLabel projectedSquaresLable = new JLabel("Projected Squares");
        
        btn1.setPreferredSize(new Dimension(160,30));
        btn2.setPreferredSize(new Dimension(160,30));
        btn3.setPreferredSize(new Dimension(160,30));
        btn4.setPreferredSize(new Dimension(160,30));
        btn5.setPreferredSize(new Dimension(160,30));
        btn6.setPreferredSize(new Dimension(160,30));
        btn7.setPreferredSize(new Dimension(160,30));
        btn8.setPreferredSize(new Dimension(160,30));

        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 2;
        leftHandPanel.add(btn1, c1);       
        
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.gridx = 0;
        c2.gridy = 1;
        c2.gridwidth = 2;
        c2.anchor = GridBagConstraints.PAGE_START;
        leftHandPanel.add(btn2, c2);
        
        GridBagConstraints c3 = new GridBagConstraints();
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.gridx = 0;
        c3.gridy = 3;
        c3.gridwidth = 2;
        c3.insets = new Insets(10,0,0,0);  //top padding
        leftHandPanel.add(btn3, c3);
        
        GridBagConstraints c4 = new GridBagConstraints();
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.gridx = 0;
        c4.gridy = 4;
        c4.gridwidth = 2;
        leftHandPanel.add(btn4, c4);        
        
        GridBagConstraints c5 = new GridBagConstraints();
        c5.fill = GridBagConstraints.HORIZONTAL;
        c5.gridx = 0;
        c5.gridy = 5;
        c5.gridwidth = 2;
        leftHandPanel.add(btn5, c5);        
        
        GridBagConstraints c6 = new GridBagConstraints();
        c6.fill = GridBagConstraints.HORIZONTAL;
        c6.gridx = 0;
        c6.gridy = 7;
        c6.gridwidth = 2;
        //c6.anchor = GridBagConstraints.PAGE_END;
        c6.insets = new Insets(10,0,0,0);  //top padding
        leftHandPanel.add(btn6, c6);        
        
        GridBagConstraints c7 = new GridBagConstraints();
        c7.fill = GridBagConstraints.HORIZONTAL;
        c7.gridx = 0;
        c7.gridy = 8;
        c7.gridwidth = 2;
        leftHandPanel.add(btn7, c7);       
        
        GridBagConstraints c8 = new GridBagConstraints();
        c8.fill = GridBagConstraints.HORIZONTAL;
        c8.gridx = 0;
        c8.gridy = 10;
        c8.gridwidth = 2;
        c8.insets = new Insets(20,0,0,0);  //top padding
        leftHandPanel.add(btn8, c8);
        
        GridBagConstraints c9 = new GridBagConstraints();
        //c9.fill = GridBagConstraints.WEST;
        c9.gridx = 0;
        c9.gridy = 13;
        c9.gridwidth = 1;
        c9.weightx = .8;
        c9.anchor  = GridBagConstraints.LINE_START;
        c9.insets = new Insets(40,8,0,0);  //top padding
        leftHandPanel.add(tipLable, c9);
        
        GridBagConstraints c10 = new GridBagConstraints();
        //c10.fill = GridBagConstraints.EAST;
        c10.gridx = 1;
        c10.gridy = 13;
        c10.gridwidth = 1;
        c10.weightx = .2;
        c10.anchor  = GridBagConstraints.LINE_START;
        c10.insets = new Insets(40,0,0,0);  //top padding
        leftHandPanel.add(txtTipNo, c10);
               
        GridBagConstraints c11 = new GridBagConstraints();
        //c11.fill = GridBagConstraints.WEST;
        c11.gridx = 0;
        c11.gridy = 14;
        c11.gridwidth = 1;
        c11.weightx = .8;
        c11.anchor  = GridBagConstraints.LINE_START;
        c11.insets = new Insets(10,8,0,0);  //top padding
        leftHandPanel.add(dancersPresentLable, c11);
        
        GridBagConstraints c12 = new GridBagConstraints();
        //c12.fill = GridBagConstraints.EAST;
        c12.gridx = 1;
        c12.gridy = 14;
        c12.gridwidth = 1;
        c12.weightx = .2;
        c12.anchor  = GridBagConstraints.LINE_START;
        c12.insets = new Insets(10,0,0,0);  //top padding
        leftHandPanel.add(txtDancersPresent, c12);
       
        GridBagConstraints c13 = new GridBagConstraints();
        //c13.fill = GridBagConstraints.WEST;
        c13.gridx = 0;
        c13.gridy = 15;
        c13.gridwidth = 1;
        c13.weightx = .8;
        c13.anchor  = GridBagConstraints.LINE_START;
        c13.insets = new Insets(10,8,0,0);  //top padding
        leftHandPanel.add(dancersOutLable, c13);
        
        GridBagConstraints c14 = new GridBagConstraints();
        //c14.fill = GridBagConstraints.EAST;
        c14.gridx = 1;
        c14.gridy = 15;
        c14.gridwidth = 1;
        c14.weightx = .2;
        c14.anchor  = GridBagConstraints.LINE_START;
        c14.insets = new Insets(10,0,0,0);  //top padding
        leftHandPanel.add(txtDancersOut, c14);
        
        GridBagConstraints c15 = new GridBagConstraints();
        //c1.fill = GridBagConstraints.WEST;
        c15.gridx = 0;
        c15.gridy = 16;
        c15.gridwidth = 1;
        c15.weightx = .8;
        c15.anchor  = GridBagConstraints.LINE_START;
        c15.insets = new Insets(10,8,0,0);  //top padding
        leftHandPanel.add(dancersDancingLable, c15);
        
        GridBagConstraints c16 = new GridBagConstraints();
        //c16.fill = GridBagConstraints.EAST;
        c16.gridx = 1;
        c16.gridy = 16;
        c16.gridwidth = 1;
        c16.weightx = .2;
        c16.anchor  = GridBagConstraints.LINE_START;
        c16.insets = new Insets(10,0,0,0);  //top padding
        leftHandPanel.add(txtDancersDancing, c16);
        
        GridBagConstraints c17 = new GridBagConstraints();
        //c17.fill = GridBagConstraints.WEST;
        c17.gridx = 0;
        c17.gridy = 17;
        c17.gridwidth = 1;
        c17.weightx = .8;
        c17.anchor  = GridBagConstraints.LINE_START;
        c17.insets = new Insets(10,8,0,0);  //top padding
        leftHandPanel.add(projectedSquaresLable, c17);
        
        GridBagConstraints c18 = new GridBagConstraints();
        //c18.fill = GridBagConstraints.EAST;
        c18.gridx = 1;
        c18.gridy = 17;
        c18.gridwidth = 1;
        c18.weightx = .2;
        c18.anchor  = GridBagConstraints.LINE_START;
        c18.insets = new Insets(10,0,0,0);  //top padding
        leftHandPanel.add(txtProjectedSquares, c18);
        
        btn1.addActionListener(new FileOpenListener());
        btn2.addActionListener(new FileSaveListener());
        btn3.addActionListener(new GenerateSquareListener());
        btn4.addActionListener(new ReGenerateSquareListener());
        btn5.addActionListener(new DisplayTipListener());
        btn6.addActionListener(new EditDancerListener(true));
        btn7.addActionListener(new EditDancerListener(false));
        btn8.addActionListener(new ResetListener());

        return leftHandPanel;
    }
	
	public void setTipNo()
	{
		//System.out.println("mainframe, setting tip to " + Globals.getInstance().getTip().getCurrentTip());
		short tipNo = CoupleGenerator.getInstance().getCurrentTip();
		if(tipNo < 0) tipNo = 0;
		//System.out.println("tipNo is now " + tipNo);
		txtTipNo.setText(Short.toString(tipNo));
		txtTipNo.validate();
		txtTipNo.repaint();
	}
	
	public void setDancerStatistics()
	{
		Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
		//System.out.println("mainframe, setting dancer statistics");

		int dancersPresent = 0;
		int dancersDancing = 0;
		int dancersOut     = 0;
		
		setTipNo();
		
		for(int ix = 0; ix < dancerData.size(); ix++)
		{
			if(!(Boolean)dancerData.get(ix).get(Dancer.DANCER_AT_DANCE_IX)) continue;
			
			dancersPresent += 1;
			//System.out.println("mainframe, dancersPresent = " + dancersPresent);
			if((Boolean)dancerData.get(ix).get(Dancer.PRESENT_IX))
			{
				dancersDancing += 1;
				//System.out.println("mainframe, dancersDancing = " + dancersDancing);
			}
			else
			{
				dancersOut += 1;
				//System.out.println("mainframe, dancersOut = " + dancersOut + ", name " + (String)dancerData.get(ix).get(Dancer.NAME_IX));
			}
		}
		
		txtDancersPresent.setText(Integer.toString(dancersPresent));
		txtDancersPresent.validate();
		txtDancersPresent.repaint();
		txtDancersDancing.setText(Integer.toString(dancersDancing));
		txtDancersDancing.validate();
		txtDancersDancing.repaint();
		txtDancersOut.setText(Integer.toString(dancersOut));
		txtDancersOut.validate();
		txtDancersOut.repaint();
		txtProjectedSquares.setText(Integer.toString(dancersDancing/8));
		txtProjectedSquares.validate();
		txtProjectedSquares.repaint();
	}
}

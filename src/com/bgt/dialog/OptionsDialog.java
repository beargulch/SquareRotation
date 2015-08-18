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

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bgt.core.Globals;
import com.bgt.frame.MainFrame;

public class OptionsDialog extends JDialog
{
	private static final long     serialVersionUID = 1L;
	
	private static final String[] singleOptions = {	"Singles rotate only with Singles", 
													"Singles can rotate with Out Couples",
													"Singles can rotate with Any Couple"
												  };
	
	private JComboBox<String>optionsBox = new JComboBox<String>(singleOptions);
	private JCheckBox jLoadSerializedData;
	
	public OptionsDialog()
	{
		super(MainFrame.getInstance(), "", Dialog.ModalityType.DOCUMENT_MODAL);
		setUpPanel();
	}

	private void setUpPanel()
	{
		jLoadSerializedData = new JCheckBox("Load serialized data (mostly for debugging)");
		
		JPanel mainPanel   = new JPanel(new GridBagLayout());
		Dimension minSize  = new Dimension(10, 18);
		Dimension prefSize = new Dimension(10, 18);
		Dimension maxSize  = new Dimension(10, 18);
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(new Box.Filler(minSize, prefSize, maxSize));

		JPanel pane1 = new JPanel();
		pane1.setLayout(new BoxLayout(pane1, BoxLayout.LINE_AXIS));
		pane1.setAlignmentX(Component.LEFT_ALIGNMENT);
		optionsBox.setMaximumSize(new Dimension(280,120));
		pane1.add(new Box.Filler(minSize, prefSize, maxSize));
		pane1.add(new JLabel("Singles Handling"));
		pane1.add(optionsBox);
		mainPanel.add(pane1);
		
		GridBagConstraints c2 = new GridBagConstraints();
	    c2.gridy  = 3;
	    c2.anchor = GridBagConstraints.LINE_START;
		mainPanel.add(jLoadSerializedData, c2);
		
		JPanel pane2 = new JPanel();
		pane2.setLayout(new BoxLayout(pane2, BoxLayout.LINE_AXIS));
		pane2.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton savBtn  = new JButton("Save");
        JButton cnclBtn = new JButton("Cancel");
		pane2.add(new Box.Filler(new Dimension(190, 120), new Dimension(190, 120), new Dimension(190, 120)));
        pane2.add(savBtn);
        pane2.add(cnclBtn);
        mainPanel.add(pane2);
		
		optionsBox.setSelectedIndex(Globals.getSelectedOption());
		
		if(Globals.getLoadSerializedData())
			jLoadSerializedData.setSelected(true);
		else
			jLoadSerializedData.setSelected(false);

	    savBtn.addActionListener(new ActionListener() 
	    {
	    	@Override 
	    	public void actionPerformed(ActionEvent e) 
	    	{	
	    		Globals.setSelectedOption(getFrame().getOptionsBox().getSelectedIndex());
	    		Globals.setLoadSerializedData(getFrame().getLoadSerializedData().isSelected());
	    		getFrame().dispose();
	    	}
	    });

	    cnclBtn.addActionListener(new ActionListener() 
	    {
	    	@Override
	    	public void actionPerformed(ActionEvent evt) 
	    	{
	    		getFrame().dispose();
	    	}
	    });
	    
		this.setTitle("Set Options");
		
	    // add content to the window.
	    this.add(mainPanel);
	    this.setPreferredSize(new Dimension(430,200));   
	    
	    // display the window
	    this.pack();
	    this.setVisible(true);
	}
	
	public JComboBox<String>getOptionsBox()
    {
    	return this.optionsBox;
    }
	
    public JCheckBox getLoadSerializedData()
    {
    	return this.jLoadSerializedData;
    }
    
    public OptionsDialog getFrame()
    {
    	return this;
    }
}

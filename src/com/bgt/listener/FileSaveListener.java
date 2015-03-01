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

package com.bgt.listener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;

import javax.swing.JFileChooser;

import com.bgt.core.Globals;
import com.bgt.model.DancersTableModel;

public class FileSaveListener implements ActionListener 
{
	public FileSaveListener() {}
	
	@Override 
	public void actionPerformed(ActionEvent e) 
	{	
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog((Component)e.getSource());

		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			BufferedWriter bout = null;
			try 
			{
				bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fc.getSelectedFile())));
				
				DancersTableModel mdl = (DancersTableModel)Globals.getInstance().getDancersJTable().getModel();
        		
        		for(int r = 0; r < mdl.getRowCount(); r++)
        		{
        			String colData = "A";
        			
        			for (int c = 0; c < mdl.getColumnCount(); c++)
        			{
        				colData += "\t" + mdl.getValueAt(r, c).toString();
        				
        			}
        			colData = colData + "\n";
        			bout.write(colData);
        		}
			} 
			catch(FileNotFoundException e1) 
			{
				System.out.println("FileNotFoundException on output file " + fc.getSelectedFile() + ":  " + e1);
			}

			catch(IOException e2) 
			{
				System.out.println("IOException on output file " + fc.getSelectedFile() + ":  " + e2);
			}
			finally
			{
				try 
				{
					bout.close();
				} 
				catch(IOException e3) 
				{
					System.out.println("IOException on output file " + fc.getSelectedFile() + ":  " + e3);
				}
			}

			// save tip status as well
			try
			{
				FileOutputStream fileOut = new FileOutputStream(fc.getSelectedFile() + ".ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(Globals.getInstance().getTip());
				out.close();
				fileOut.close();
				System.out.printf("Serialized data is saved in " + fc.getSelectedFile() + "ser");
			}
			catch(IOException i)
			{
				i.printStackTrace();
			}
		}
	}
}

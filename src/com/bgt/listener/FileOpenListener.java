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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.bgt.core.CoupleGenerator;
import com.bgt.core.Dancer;
import com.bgt.core.Globals;
import com.bgt.frame.MainFrame;
import com.bgt.jtable.DancersJTable;
import com.bgt.model.DancersTableModel;

public class FileOpenListener implements ActionListener
{
	public FileOpenListener(){}
	
	@Override 
	public void actionPerformed(ActionEvent e) 
	{	
		int returnVal = 1;

		if(((DancersTableModel)DancersJTable.getInstance().getModel()).getRowCount() > 0)
		{
			returnVal = JOptionPane.showConfirmDialog(null, "This will overlay your existing dancer list.  Proceed?", "Load new Dancers List", 
						JOptionPane.OK_CANCEL_OPTION);
			if(returnVal == JOptionPane.CANCEL_OPTION) return;
		}
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("SquareRotation files", "dnc"));
		returnVal = fc.showOpenDialog(null);
		
		if(returnVal != JFileChooser.APPROVE_OPTION) return;
		
		//CoupleGenerator.getInstance().setCurrentTip((short)0);
		List<Object[]> records = new ArrayList<Object[]>(100);
		
		BufferedReader bin = null;
		try 
		{
			bin = new BufferedReader(new InputStreamReader(new FileInputStream(fc.getSelectedFile())));

			int    jx;
			String buf = null;
			while( (buf = bin.readLine()) != null)
			{
				String[] cols   = buf.split("\t");
				Object recObj[] = new Object[cols.length];

				if(cols[0].toString().equals("A"))
				{
					for(int ix = 1; ix < cols.length; ix++)
					{
						jx = ix - 1;
						switch(jx)
						{
							case Dancer.NAME_IX:
								recObj[jx] = cols[ix].toString();
								break;
					
							case Dancer.DANCING_IX:
							case Dancer.MUST_DANCE_IX:
							case Dancer.WILLING_SINGLE_IX:
							case Dancer.DANCER_DANCED_IX:
							case Dancer.DANCER_SELECTED_IX:
							case Dancer.DANCER_AT_DANCE_IX:
								recObj[jx] = new Boolean(cols[ix]);
								break;
							
							case Dancer.ROLE_IX:
							case Dancer.PARTNER_IX:
							case Dancer.DANCER_OUTS_IX:
								recObj[jx] = new Integer(cols[ix]);
								break;
								
							default:
								recObj[jx] = cols[ix];			
						}
					}
					records.add(recObj);
				}
			}
			DancersJTable.getInstance().setUpTableModel((Object[][])records.toArray(new Object[records.size()][]));
		} 
		catch(FileNotFoundException e1) 
		{
			System.out.println("FileNotFoundException on input file " + fc.getSelectedFile() + ":  " + e1);
		}
		catch(IOException e2) 
		{
			System.out.println("IOException on input file " + fc.getSelectedFile() + ":  " + e2);
		}
		finally
		{
			try 
			{
				bin.close();
			} 
			catch(IOException e3) 
			{
				System.out.println("IOException on input file " + fc.getSelectedFile() + ":  " + e3);
			}
		}
		
		// loaded dancers; now load persisted Tip, if one can be found.

		if(Globals.getLoadSerializedData())
		{
			String fileName = "";
			try
			{
				fileName = fc.getSelectedFile().getAbsolutePath().replace(".dnc", ".ser");
				System.out.println("Loading serialized file " + fileName);
				FileInputStream fileIn = new FileInputStream(fileName);
				ObjectInputStream   in = new ObjectInputStream(fileIn);
				CoupleGenerator.loadSerializedInstance((CoupleGenerator)in.readObject());
				in.close();
				fileIn.close();
				MainFrame.getInstance().setTipNo();
			}
			catch(FileNotFoundException f)
			{
				System.out.println("Could not find file " + fileName + ".  Will start from tip 1.");
			}
			catch(IOException i)
			{
				System.out.println("IOException");
				i.printStackTrace();
				return;
			}
			catch(ClassNotFoundException c)
			{
				System.out.println("CoupleGenerator class not found");
				c.printStackTrace();
				return;
			}
		}
		else
		{
			// if we are not loading the serialized object, then we are starting fresh,
			// with no outs.  we therefore zero out the out counts which may have been
			// present (non-zero) in the input data.
			Vector<Vector<Object>>dancerData = DancersJTable.getInstance().getDancerData();
			for(Vector<Object>data : dancerData) data.set(Dancer.DANCER_OUTS_IX, 0);
		}

		CoupleGenerator.getInstance().allocateArrays();
	}
}
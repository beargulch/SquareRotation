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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.bgt.core.Globals;
import com.bgt.frame.MainFrame;

public class SquareRotation
{
    public static void main(String[] args) 
    {
        // Schedule a job for the event dispatch thread: create and show this application's GUI.
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
        		try 
        		{	
        			String lcOSName = System.getProperty("os.name").toLowerCase();
        			if(lcOSName.startsWith("mac os x"))
        			{	
        				System.setProperty("apple.laf.useScreenMenuBar", "true");
        				// System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Rotate");
        				// System.setProperty("apple.awt.brushMetalLook", "true");
        			}
        			//UIManager.setLookAndFeel(new MetalLookAndFeel());
        			
        			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        		} 
        		catch (Exception e) {}
        		
        		// Turn off metal's use of bold fonts
                // UIManager.put("swing.boldMetal", Boolean.FALSE); 
                
        		/**
        	     * Create the GUI and show it.  For thread safety, this method should be invoked from 
        	     * the event dispatch thread.
        	     */
        		Globals.getInstance().setMainFrame(new MainFrame());
            }
        });
    }
}
package fr.curie.DeDaL;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

public class SelectColumnsDialog extends JDialog /*implements ActionListener*/ {


	private static final int SCROLL_WIDTH = 210;
	private static final int SCROLL_HEIGHT = 180;
	private static Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

	private JPanel panel;
	private JButton okB, okAndSaveB, cancelB, loadB,expEVB;
	private JList sourceList;
	private JScrollPane scrollPane;
	public ArrayList<String> myselList = new ArrayList<String>();
	public boolean savePreprocessedData = false;
	public boolean saveEigenVectors = false;

	public File file = null;
	public File fs = null;


	static String pcaString = "PCA";
	static String nlpcaString = "Elastic map (non-linear PCA)";
	
	JCheckBox dcenter;
	JCheckBox nsmoothing;
	JSlider smoothingvalue;
	
	JCheckBox pc1;
    JCheckBox pc2;
	JCheckBox pc3;
	JCheckBox pc4;
	JCheckBox pc5;
	JCheckBox pc6;
	JCheckBox pc7;
	JCheckBox pc8;
	JCheckBox pc9;
	JCheckBox pc10;
	JRadioButton nlpcaButton;
	JRadioButton pcaButton;
	JLabel components;
	SelectColumnsDialog(JFrame frame, String mess, boolean modal){

		// call JDialog constructor
		super(frame, mess, modal);
		createElements();
	}
	
	public SelectColumnsDialog(){
		
	}

	private void createElements() {

		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c;

		JLabel label;

		int y = 1;
		int x = 1;

		sourceList = new JList();
		sourceList.setBackground(new Color(0xeeeeee));
		scrollPane = new JScrollPane(sourceList);
		c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y + 14;
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,10,10);
		scrollPane.setPreferredSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
		panel.add(scrollPane, c);
		JPanel buttonPanel = new JPanel();

		
		loadB = new JButton("Load eigen vectors");
		
		loadB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				

				JFileChooser fileopen = new JFileChooser();


				int ret = fileopen.showDialog((new SelectColumnsDialog()), "Select");

			    if (ret == JFileChooser.APPROVE_OPTION) {
			      file = fileopen.getSelectedFile();
			      System.out.println(file);
			      
			    
			    }
				
			}});
		buttonPanel.add(loadB);
		
		okB = new JButton("OK");
		okB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selList[] = sourceList.getSelectedValues();
				for (int i=0;i<selList.length;i++) {
					String item = (String) selList[i];
					myselList.add(item);
					
					
					//System.out.println(item);
				}
				
				if (myselList.size()==0){
					JOptionPane.showMessageDialog(null, "Load data first","ERROR", JOptionPane.WARNING_MESSAGE);
				
				}
				savePreprocessedData = false;
				setVisible(false);
				dispose();
			}
		});
		buttonPanel.add(okB);
		
		
		okAndSaveB = new JButton("OK and Save data...");


		okAndSaveB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selList[] = sourceList.getSelectedValues();
				for (int i=0;i<selList.length;i++) {
					String item = (String) selList[i];
					myselList.add(item);
					
					
					//System.out.println(item);
				}
				
				if (myselList.size()==0){
					JOptionPane.showMessageDialog(null, "Load data first","ERROR", JOptionPane.WARNING_MESSAGE);
				
				}
				savePreprocessedData = true;
				setVisible(false);
				dispose();
			}
		});
		buttonPanel.add(okAndSaveB);
		
		expEVB = new JButton("Save eigen vectors");
		expEVB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveEigenVectors=true;

				

				JFileChooser fileopen = new JFileChooser();


				int ret = fileopen.showDialog((new SelectColumnsDialog()), "Select");

			    if (ret == JFileChooser.APPROVE_OPTION) {
			      fs = fileopen.getSelectedFile();
			      System.out.println(fs);
			      
			    
			    }
				
			
				
			}
		});
		buttonPanel.add(expEVB);
		
		cancelB = new JButton("Cancel");
		cancelB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				
			}
		});
		buttonPanel.add(cancelB);

		getContentPane().setLayout(new BorderLayout());
		JScrollPane jpane = new JScrollPane(panel);
		getContentPane().add(jpane, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		///
		
	    dcenter = new JCheckBox("Double center data");
        dcenter.setMnemonic(KeyEvent.VK_C);
        dcenter.setSelected(true);
        c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
	    c.gridwidth = 3;
        c.anchor = GridBagConstraints.WEST;
        panel.add(dcenter, c);
        y++;

        nsmoothing = new JCheckBox("Network-smooth data (0 - no smoothing, 100 - complete smoothing)");
        nsmoothing.setMnemonic(KeyEvent.VK_N);
        nsmoothing.setSelected(false);
        c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
	    c.gridwidth = 3;
        c.anchor = GridBagConstraints.WEST;
        panel.add(nsmoothing, c);
        //nsmoothing.setSelected(false);
        nsmoothing.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		if(nsmoothing.isSelected()){
        			smoothingvalue.setEnabled(true);
        			validate();
        		}else{
        			smoothingvalue.setEnabled(false);
        			validate();
        		}
        	}
        });
        y++;
        
        smoothingvalue = new JSlider(JSlider.HORIZONTAL,
                0, 100, 50);
        /*smoothingvalue.addChangeListener(new ChangeListener(){
        	public void 
        });*/

        //Turn on labels at major tick marks.
        smoothingvalue.setMajorTickSpacing(20);
        smoothingvalue.setMinorTickSpacing(5);
        smoothingvalue.setPaintTicks(true);
        smoothingvalue.setPaintLabels(true);
        smoothingvalue.setEnabled(false);
        smoothingvalue.setBorder(
        		BorderFactory.createEmptyBorder(0,0,0,10));
        		Dimension d = smoothingvalue.getSize();
        		d.width=420;
        		d.height=50;
        		smoothingvalue.setPreferredSize(d);
        		validate();
    		    c = new GridBagConstraints();
    		    c.gridx = x;
    		    c.gridy = y;
    		    c.gridwidth = 3;
    		    c.anchor = GridBagConstraints.LINE_START;
    		    c.anchor = GridBagConstraints.WEST;
    		    panel.add(smoothingvalue, c);
    	y++;
        
        JLabel separator = new JLabel("----------------------------------------------------------------------------");
        c = new GridBagConstraints();
        c.gridx = x;
        c.gridy = y;
	    c.gridwidth = 3;
        c.anchor = GridBagConstraints.WEST;
        panel.add(separator, c);
        y++;
        

		 JLabel algoPL = new JLabel("Choose data-driven layout algorithm:");
		    c = new GridBagConstraints();
		    c.gridx = x;
		    c.gridy = y;
		    c.gridwidth = 3;
		    c.anchor = GridBagConstraints.LINE_START;
		    c.anchor = GridBagConstraints.WEST;
		    panel.add(algoPL, c);
		    
		    y++;
		  
		    pcaButton = new JRadioButton(pcaString);
		    pcaButton.setMnemonic(KeyEvent.VK_B);
		    pcaButton.setActionCommand(pcaString);
		    pcaButton.setSelected(true);
		    pcaButton.addActionListener(new ActionListener(){
	    		public void actionPerformed(ActionEvent e) {
	    			//if (nlpcaButton.getActionCommand().equals(pcaString)) {
	    				components.setEnabled(true);
	    				pc1.setEnabled(true);
	    				pc2.setEnabled(true);
	    				pc3.setEnabled(true);
	    				pc4.setEnabled(true);
	    				pc5.setEnabled(true);
	    				pc6.setEnabled(true);
	    				pc7.setEnabled(true);
	    				pc8.setEnabled(true);
	    				pc9.setEnabled(true);
	    				pc10.setEnabled(true);
	    	    }
	    		//}
	    });
		    
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
	        c.gridwidth = 3;
	        c.anchor = GridBagConstraints.LINE_START;
	       
	        panel.add(pcaButton, c);
	        

			  
		    nlpcaButton = new JRadioButton(nlpcaString);
		    nlpcaButton.setMnemonic(KeyEvent.VK_B);
		    nlpcaButton.setActionCommand(nlpcaString);
		    nlpcaButton.setActionCommand("desactivate");
		    nlpcaButton.addActionListener(new ActionListener(){
		    		public void actionPerformed(ActionEvent e) {
		    			if (nlpcaButton.getActionCommand().equals("desactivate")) {
		    				components.setEnabled(false);
		    				pc1.setEnabled(false);
		    				pc2.setEnabled(false);
		    				pc3.setEnabled(false);
		    				pc4.setEnabled(false);
		    				pc5.setEnabled(false);
		    				pc6.setEnabled(false);
		    				pc7.setEnabled(false);
		    				pc8.setEnabled(false);
		    				pc9.setEnabled(false);
		    				pc10.setEnabled(false);
		    	    }
		    		}
		    });
		    
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
	        c.gridwidth = 3;
	        c.anchor = GridBagConstraints.LINE_START;
	        c.anchor = GridBagConstraints.CENTER;
	        panel.add(nlpcaButton, c);
	        
	        ButtonGroup group = new ButtonGroup();
	        group.add(pcaButton);
	        group.add(nlpcaButton);
	  
	        //
	        /*JLabel center = new JLabel("Center data:");
		    c = new GridBagConstraints();
		    c.gridx = x;
		    c.gridy = y;
		    c.gridwidth = 3;
		    c.anchor = GridBagConstraints.WEST;
		    panel.add(center, c);*/
		    
		    y++;
		    
	        
	        
	        //
	        //if (pcaButton.isSelected()){
	        
	        //JLabel components = new JLabel("Project components (Check two):");
	        components = new JLabel("Project onto components (Check two):");
		    c = new GridBagConstraints();
		    c.gridx = x;
		    c.gridy = y;
		    c.gridwidth = 3;
		    c.anchor = GridBagConstraints.WEST;
		    panel.add(components, c);
		    
		    y++;
		    
		    pc1 = new JCheckBox("PC1");
	        pc1.setMnemonic(KeyEvent.VK_C);
	        pc1.setSelected(true);
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.WEST;
	        panel.add(pc1, c);
	        
	        pc6 = new JCheckBox("PC 6");
	        pc6.setMnemonic(KeyEvent.VK_C);
	   
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.CENTER;
	        panel.add(pc6, c);
y++;
		    
		    pc2 = new JCheckBox("PC2");
	        pc2.setMnemonic(KeyEvent.VK_C);
	        pc2.setSelected(true);
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.WEST;
	        panel.add(pc2, c);
	        
	        pc7 = new JCheckBox("PC 7");
	        pc7.setMnemonic(KeyEvent.VK_C);
	   
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.CENTER;
	        panel.add(pc7, c);
y++;
		    
		    pc3 = new JCheckBox("PC3");
	        pc3.setMnemonic(KeyEvent.VK_C);
	        
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.WEST;
	        panel.add(pc3, c);
	        
	        pc8 = new JCheckBox("PC 8");
	        pc8.setMnemonic(KeyEvent.VK_C);
	   
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.CENTER;
	        panel.add(pc8, c);
y++;
		    
		    pc4 = new JCheckBox("PC4");
	        pc4.setMnemonic(KeyEvent.VK_C);
	   
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.WEST;
	        panel.add(pc4, c);
	
	        pc9 = new JCheckBox("PC 9");
	        pc9.setMnemonic(KeyEvent.VK_C);
	   
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.CENTER;
	        panel.add(pc9, c);
	        y++;
		    
		    pc5 = new JCheckBox("PC5");
	        pc5.setMnemonic(KeyEvent.VK_C);
	   
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.WEST;
	        panel.add(pc5, c);
	
	      	        
	        pc10 = new JCheckBox("PC10");
	        pc10.setMnemonic(KeyEvent.VK_C);
	   
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.CENTER;
	        panel.add(pc10, c);

	        y++;
	        
	        separator = new JLabel("----------------------------------------------------------------------------");
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
		    c.gridwidth = 3;
	        c.anchor = GridBagConstraints.WEST;
	        panel.add(separator, c);
	        y++;	        
	        
			label = new JLabel("Select columns:");
			c = new GridBagConstraints();
			c.gridx = x;
			c.gridy = y;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.NONE;
			panel.add(label, c);
	        
	        
	        //}
	        
	        
	        
	        /*partialRB.setActionCommand("enum");
partialRB.addActionListener(this);

et ensuite d'intercepter les actions avec cette fonction:

        public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("berge")) {
                        maxSetSize.setText("inf");
                        maxSetNb.setText("");
                        maxSetNb.setEnabled(false);
                }
                if (e.getActionCommand().equals("enum")) {
                        maxSetSize.setText("10"); // ici on change les propriétés d un autre
objet
                        maxSetNb.setText("50");
                        maxSetNb.setEnabled(true);
                }
        }*/
	        
	        
	        
	        
	        
	        
	}

	public void setDialogData(ArrayList<String> dataList) {

		Vector v = new Vector();
		for (String n : dataList)
			v.add(n);

		sourceList.setListData(v);

		setSize(500, 650);
		setLocation((screenSize.width - getSize().width) / 2,
				(screenSize.height - getSize().height) / 2);
	}

	public void itemStateChanged(ItemEvent e) {}
	
	// add these check boxes to the container...
	 
	// add an action listener
	
    
		
	
}

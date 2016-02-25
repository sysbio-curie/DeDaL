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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

public class PreSliderDialog extends JDialog implements ActionListener {

private Double pourcentage;
	private static final int SCROLL_WIDTH = 210;
	private static final int SCROLL_HEIGHT = 200;
	private static Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

	private JPanel panel;
	
	public JButton okB, cancelB;
	private JList sourceList;
	
	private JScrollPane scrollPane;
	
	public String mysel = null;
	
	static String alignString = "align";
	static String nalignString = "don't align";
	JRadioButton nalignButton;
	JRadioButton alignButton;

	PreSliderDialog(JFrame frame, String mess, boolean modal){

		// call JDialog constructor
		super(frame, mess, modal);
		createElements();
	}

	private void createElements() {

		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c;

		JLabel label;

		int y = 1;
		int x = 1;
		label = new JLabel("Select second network");
		c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		panel.add(label, c);

		sourceList = new JList();
		sourceList.setBackground(new Color(0xeeeeee));
	 y++;
		scrollPane = new JScrollPane(sourceList);
		c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y;
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,10,10);
		scrollPane.setPreferredSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
		panel.add(scrollPane, c);
		y++;
		 JLabel algoPL = new JLabel("");
		    c = new GridBagConstraints();
		    c.gridx = x;
		    c.gridy = y;
		    c.gridwidth = 3;
		    c.anchor = GridBagConstraints.LINE_START;
		    c.anchor = GridBagConstraints.WEST;
		    panel.add(algoPL, c);
		    
		    y++;
		  
		    alignButton = new JRadioButton(alignString);
		    alignButton.setMnemonic(KeyEvent.VK_B);
		    alignButton.setActionCommand(alignString);
		    alignButton.setSelected(true);
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
	        c.gridwidth = 3;
	        c.anchor = GridBagConstraints.LINE_START;
	       
	        panel.add(alignButton, c);
	        

			  
		    nalignButton = new JRadioButton(nalignString);
		    nalignButton.setMnemonic(KeyEvent.VK_B);
		    nalignButton.setActionCommand(nalignString);
		    nalignButton.setActionCommand("desactivate");
		    nalignButton.addActionListener(this);
		    
	        c = new GridBagConstraints();
	        c.gridx = x;
	        c.gridy = y;
	        c.gridwidth = 3;
	        c.anchor = GridBagConstraints.LINE_START;
	        c.anchor = GridBagConstraints.CENTER;
	        panel.add(nalignButton, c);
	        
	        ButtonGroup group = new ButtonGroup();
	        group.add(alignButton);
	        group.add(nalignButton);
		
		
		
		JPanel buttonPanel = new JPanel();
		okB = new JButton("OK");


		okB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selList[] = sourceList.getSelectedValues();
				for (int i=0;i<selList.length;i++) {
					String item = (String) selList[i];
					mysel=item;
					
					
					//System.out.println("item"+item);
				}
				
				if (mysel==null){
					JOptionPane.showMessageDialog(null, "Select one target network","ERROR", JOptionPane.WARNING_MESSAGE);
					
					
				}else{
					
					
					//System.out.println(item);
				
				setVisible(false);
				dispose();}
			}
		});
		buttonPanel.add(okB);
		
		
		

		cancelB = new JButton("Cancel");
		cancelB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				setVisible(false);
			
				
			}
			
		});
		buttonPanel.add(cancelB);

		getContentPane().setLayout(new BorderLayout());
		
		JScrollPane jpane = new JScrollPane(panel);
	
		getContentPane().add(jpane, BorderLayout.PAGE_START);
		
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		
	        
	      
	}

	public void setDialogData(ArrayList<String> dataList) {

		Vector v = new Vector();
		for (String n : dataList)
			v.add(n);

		sourceList.setListData(v);

		setSize(400, 360);
		setLocation((screenSize.width - getSize().width) / 2,
				(screenSize.height - getSize().height) / 2);
		
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}
}

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

public class SelectNetworksDialog extends JDialog implements ActionListener {


	private static final int SCROLL_WIDTH = 210;
	private static final int SCROLL_HEIGHT = 90;
	private static Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

	private JPanel panel;
	private JPanel pane2;
	private JButton okB, cancelB;
	private JList sourceList;
	private JList sourceList2;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane2;
	public ArrayList<String> myselList = new ArrayList<String>();
	public ArrayList<String> myselNet = new ArrayList<String>();
	

	SelectNetworksDialog(JFrame frame, String mess, boolean modal){

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
		label = new JLabel("Select reference network (one):");
		c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		panel.add(label, c);

		sourceList = new JList();
		sourceList.setBackground(new Color(0xeeeeee));
		scrollPane = new JScrollPane(sourceList);
		c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y+1;
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,10,10);
		scrollPane.setPreferredSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
		panel.add(scrollPane, c);
		
		
		pane2 = new JPanel(new GridBagLayout());
		JLabel label2;

		
		label2 = new JLabel("Select networks to align:");
		c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y+5;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		pane2.add(label2, c);
		
		sourceList2 = new JList();
		sourceList2.setBackground(new Color(0xeeeeee));
		scrollPane2 = new JScrollPane(sourceList2);
		c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y+6;
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,10,10);
		scrollPane2.setPreferredSize(new Dimension(SCROLL_WIDTH, SCROLL_HEIGHT));
		pane2.add(scrollPane2, c);
		
		
		JPanel buttonPanel = new JPanel();
		okB = new JButton("OK");


		okB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object selList[] = sourceList.getSelectedValues();
				for (int i=0;i<selList.length;i++) {
					String item = (String) selList[i];
					myselList.add(item);
					
					
					//System.out.println("item"+item);
				}
				
				Object selNet[] = sourceList2.getSelectedValues();
				for (int i=0;i<selNet.length;i++) {
					String item = (String) selNet[i];
					myselNet.add(item);
					
					
					//System.out.println(item);
				}
				//System.out.println(myselNet);
				
				if(myselNet.size()==0){
					JOptionPane.showMessageDialog(null, "Select at least one network to adjust to your reference network","ERROR", JOptionPane.WARNING_MESSAGE);

				}
				else if (myselList.size()!=1){
					JOptionPane.showMessageDialog(null, "Select one reference network","ERROR", JOptionPane.WARNING_MESSAGE);

				}
				else{
					setVisible(false);
					dispose();
				}
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
		JScrollPane jpane2 = new JScrollPane(pane2);
		getContentPane().add(jpane, BorderLayout.PAGE_START);
		getContentPane().add(jpane2, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		
	        
	      
	}

	public void setDialogData(ArrayList<String> dataList) {

		Vector v = new Vector();
		for (String n : dataList)
			v.add(n);

		sourceList.setListData(v);

		setSize(400, 300);
		setLocation((screenSize.width - getSize().width) / 2,
				(screenSize.height - getSize().height) / 2);
		
	}
	
	public void setDialogData2(ArrayList<String> dataList) {

		Vector v = new Vector();
		for (String n : dataList)
			v.add(n);

		sourceList2.setListData(v);

		setSize(400, 350);
		setLocation((screenSize.width - getSize().width) / 2,
				(screenSize.height - getSize().height) / 2);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}

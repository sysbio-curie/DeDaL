package fr.curie.DeDaL;

import java.awt.*;
import java.awt.event.*;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.*;

/*
 * SliderDemo.java requires all the files in the images/doggy
 * directory.
 */
public class SliderPCA extends JPanel
                        implements ActionListener,
                                   WindowListener,
                                   ChangeListener {
    //Set up animation parameters.
    static final int FPS_MIN = 0;
    static final int FPS_MAX = 100;
    static final int FPS_INIT = 50;    //initial frames per second
    int frameNumber = 0;
    private JSlider source;
	private static Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
	JSlider framesPerSecond;
   
    int delay;
    Timer timer;
    boolean frozen = false;
   

    //This label uses ImageIcon to show the doggy pictures.
    //JLabel picture;

    public SliderPCA() {
       // setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

      

        //Create the label.
        JLabel sliderLabel = new JLabel("Percentage of target layout", JLabel.CENTER);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the slider.
       framesPerSecond = new JSlider(JSlider.HORIZONTAL,
                                              FPS_MIN, FPS_MAX, FPS_INIT);
        framesPerSecond.addChangeListener(this);

        //Turn on labels at major tick marks.
        framesPerSecond.setMajorTickSpacing(20);
        framesPerSecond.setMinorTickSpacing(5);
        framesPerSecond.setPaintTicks(true);
        framesPerSecond.setPaintLabels(true);
        framesPerSecond.setBorder(
                BorderFactory.createEmptyBorder(0,0,0,10));
        Dimension d = framesPerSecond.getSize();
        d.width=300;
        d.height=50;
    framesPerSecond.setPreferredSize(d);
    validate();
        //Create the label table.
        Hashtable<Integer, JLabel> labelTable = 
            new Hashtable<Integer, JLabel>();
        //PENDING: could use images, but we don't have any good ones.
        labelTable.put(new Integer( 0 ),
                       new JLabel("Initial") );
                     //new JLabel(createImageIcon("images/stop.gif")) );
        labelTable.put(new Integer( 20 ),
                new JLabel("20") );
        labelTable.put(new Integer( 40 ),
                new JLabel("40") );
        labelTable.put(new Integer( 60 ),
                new JLabel("60") );
        labelTable.put(new Integer( 80 ),
                new JLabel("80") );
        JLabel target= new JLabel("Target");
        target.setSize(10,10);
        labelTable.put(new Integer( FPS_MAX ),
                       target );
                     //new JLabel(createImageIcon("images/fast.gif")) );
        framesPerSecond.setLabelTable(labelTable);

        //TransitionalLayout.getInstance().doMyLayout (0.5);

        //Put everything together.
        add(sliderLabel);
        add(framesPerSecond);
       
        

     
    }
//public int getValue(){return framesPerSecond.getValue();}
//public void setValue(int value){framesPerSecond.setValue(value);}
    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

    //React to window events.
    public void windowIconified(WindowEvent e) {
        
    }
    public void windowDeiconified(WindowEvent e) {
        
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}

    /** Listen to the slider. 
     * @return */
    
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
       
      //System.out.println("state change "+ source.getValue());
        if ( source.getValueIsAdjusting()) {
        	
         Double s = (double)source.getValue()/100;
           TransitionalLayout.getInstance().doMyLayout (s);
            
          
        }
    }

    

    //Called when the Timer fires.
    public void actionPerformed(ActionEvent e) {
      
    }

 

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
	
	public  void createAndShowGUI() {
		 

        //Create and set up the window.
        JFrame frame = new JFrame("Slider");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        //Create and set up the content pane.
        SliderPCA animator = new SliderPCA();
        animator.setOpaque(true); //content panes must be opaque
        frame.setContentPane(animator);
        frame.setLocation((screenSize.width - getSize().width) / 2,
				(screenSize.height - getSize().height) / 2);
frame.setSize(350, 135);
        //Display the window.
        //frame.pack();
        frame.setVisible(true);
        

    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
    	 
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	
                SliderPCA slider= new SliderPCA();
                slider.createAndShowGUI();
            }
            
            
        });
        
       
    }
}

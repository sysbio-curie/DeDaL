package fr.curie.DeDaL;


import java.util.List;
import java.util.Map;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.undo.AbstractCyEdit;
import org.cytoscape.work.undo.UndoSupport;

public class UndoSlider extends AbstractCyEdit {



	private double pourcentage;
	private int sliderValue;
	public UndoSlider() {
		super("Slider");
		//System.out.println("SLIDER3");
		this.pourcentage = 0.00;
	}
	public void setPourcentage (double pourcentage ){this.pourcentage=pourcentage;}
	public void redo() {
		//System.out.println("REDO SLIDER");
		System.out.println("pourcenage" + pourcentage);
		//TransitionalLayout.getInstance().doMyLayout(pourcentage);
		//TransitionalLayout.getInstance().setSliderValue(sliderValue);
	}
	public void undo() {
		//System.out.println("UNDO SLIDER");
		 double pourcentage=this.pourcentage;
		// System.out.println("pourcenage" + pourcentage);
		 TransitionalLayout.getInstance().doMyLayout(0.0);
		this.pourcentage=pourcentage;
		//sliderValue= TransitionalLayout.getInstance().getSliderValue();
		//TransitionalLayout.getInstance().setSliderValue(0);

	}
} 
package fr.curie.DeDaL;

import org.cytoscape.app.swing.AbstractCySwingApp;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.swing.CySwingApplication;

public class DedalApp extends AbstractCySwingApp 
{
	public DedalApp (CySwingAppAdapter adapter)
	{
		super(adapter);
		CySwingApplication application = adapter.getCySwingApplication();
		application.addAction(new MenuAction(adapter));
		application.addAction(new Adjust(adapter));
		application.addAction(new TransitionalLayout(adapter));
	}
}

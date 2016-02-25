package fr.curie.DeDaL;


import org.cytoscape.work.undo.AbstractCyEdit;



public class UndoAlign extends AbstractCyEdit {





	public UndoAlign() {
		super("Align");
	}

	
	public void undo() {
		System.out.println("UNDO");
		Adjust.getInstance().applyStart();	
	
	}
	
	public void redo() {
		System.out.println("REDO");
		Adjust.getInstance().doAlign();

	}
} 


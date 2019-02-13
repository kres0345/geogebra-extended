/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SelectionManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * InlineLabelTreeItem for the openable tree nodes of the algebra view
 *
 * File created by Arpad Fekete
 */

public class GroupNameLabel extends Label
	implements ClickHandler, MouseOverHandler, MouseOutHandler {

	private TreeItem par;
	private SelectionManager selection;

	/**
	 * @param selection
	 *            selection manager
	 * @param parent
	 *            parent
	 * @param strlab
	 *            text
	 */
	public GroupNameLabel(SelectionManager selection, TreeItem parent,
			String strlab) {
		super(strlab);
		this.selection = selection;
		par = parent;
		addClickHandler(this);
		addMouseOverHandler(this);
		addMouseOutHandler(this);

		this.setStyleName("elemHeadingName");
	}

	@Override
	public void onClick(ClickEvent evt) {
		Object uo;
		ArrayList<GeoElement> groupedGeos = new ArrayList<>();
		for (int i = 0; i < par.getChildCount(); i++) {
			//par.getChild(i).setSelected(true);
			uo = par.getChild(i).getUserObject();
			if (uo instanceof GeoElement) {
				groupedGeos.add((GeoElement) uo);
			}
		}
		par.setSelected(false);

		//if (!AppD.isControlDown(e) && !e.isShiftDown())
		if (!evt.isControlKeyDown() && !evt.isShiftKeyDown()) {
			selection.clearSelectedGeos();
		}

		selection.addSelectedGeos(groupedGeos, true);
	}

	@Override
	public void onMouseOver(MouseOverEvent evt) {
		// for (int i = 0; i < par.getChildCount(); i++)
		// if (par.getChild(i).getUserObject() instanceof GeoElement)
		// //
		// ((GeoElement)par.getChild(i).getUserObject()).setHighlighted(true);
		// par.getChild(i).getWidget()
		// .addStyleName("gwt-TreeItem-selected");
		//
	}

	@Override
	public void onMouseOut(MouseOutEvent evt) {
		// for (int i = 0; i < par.getChildCount(); i++)
		// if (par.getChild(i).getUserObject() instanceof GeoElement) {
		// //
		// ((GeoElement)par.getChild(i).getUserObject()).setHighlighted(false);
		// par.getChild(i).getWidget()
		// .removeStyleName("gwt-TreeItem-selected");
		// }
	}
}

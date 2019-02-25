package org.geogebra.web.html5.gui.view.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;

public interface BrowseViewI {

	public void setMaterialsDefaultStyle();

	public void loadAllMaterials();

	public void clearMaterials();

	public void disableMaterials();

	public void onSearchResults(final List<Material> response,
	        final ArrayList<Chapter> chapters);

	public void close();

	public void displaySearchResults(final String query);

	public void refreshMaterial(final Material material, final boolean isLocal);

	public void rememberSelected(final MaterialListElementI materialElement);

	public void setLabels();

	public void addMaterial(Material material);

	public void removeMaterial(Material material);

	public void setHeaderVisible(boolean visible);

	public void closeAndSave(AsyncOperation<Boolean> callback);

}

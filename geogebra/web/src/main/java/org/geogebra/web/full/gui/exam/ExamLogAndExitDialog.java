package org.geogebra.web.full.gui.exam;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.ExamLogBuilder;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author csilla
 * 
 *         exam exit dialog with the whole information
 *
 */
public class ExamLogAndExitDialog extends DialogBoxW
		implements FastClickHandler, SetLabels {
	private AppW appW;
	private AsyncOperation<String> returnHandler;
	private FlowPanel dialog;
	// components of title panel
	private FlowPanel titlePanel;
	private Label calcType;
	private NoDragImage alertImg;
	private Label examTitle;
	// components of content panel
	private ScrollPanel scrollPanel;
	private FlowPanel contentPanel;
	private Label teacherText = new Label("");
	private Label durationLbl = new Label("");
	private Label duration = new Label("");
	private Label dateLbl = new Label("");
	private Label date = new Label("");
	private Label startTimeLbl = new Label("");
	private Label startTime = new Label("");
	private Label endTimeLbl = new Label("");
	private Label endTime = new Label("");
	private Label activityLbl = new Label("");
	private FlowPanel activityPanel;
	// components of button panel
	private FlowPanel buttonPanel;
	private StandardButton okBtn;
	private StandardButton exitBtn;
	private Widget anchor;

	/**
	 * @param app
	 *            application
	 * @param isLogDialog
	 *            true if need to build log dialog
	 * @param returnHandler
	 *            return handler
	 * @param anchor
	 *            anchor
	 */
	public ExamLogAndExitDialog(AppW app, boolean isLogDialog,
			AsyncOperation<String> returnHandler, Widget anchor) {
		super(app.getPanel(), app);
		this.appW = app;
		this.returnHandler = returnHandler;
		this.anchor = anchor;
		this.addStyleName(isLogDialog ? "examLogDialog" : "examExitDialog");
		setGlassEnabled(false);
		buildGUI(isLogDialog);
	}

	private void buildGUI(boolean isLogDialog) {
		dialog = new FlowPanel();
		// build title panel
		buildTitlePanel();
		// build content panel
		scrollPanel = new ScrollPanel();
		contentPanel = new FlowPanel();
		contentPanel.setStyleName(appW.getExam().isCheating() && isLogDialog
				? "contentPanel cheating" : "contentPanel");
		buildContent(isLogDialog);
		scrollPanel.add(contentPanel);
		// build button panel
		buttonPanel = new FlowPanel();
		buttonPanel.setStyleName("DialogButtonPanel");
		if ((appW.getExam().isCheating() && !isLogDialog)
				|| (isLogDialog && activityPanel != null
						&& activityPanel.getWidgetCount() > 7)) {
			buttonPanel.addStyleName("withDivider");
		}
		okBtn = new StandardButton("", appW);
		okBtn.addFastClickHandler(this);
		exitBtn = new StandardButton("", appW);
		exitBtn.addFastClickHandler(this);
		buttonPanel.add(isLogDialog ? okBtn : exitBtn);
		// build whole dialog
		dialog.add(titlePanel);
		dialog.add(scrollPanel);
		dialog.add(buttonPanel);
		this.add(dialog);
		setLabels();
	}

	private void buildTitlePanel() {
		titlePanel = new FlowPanel();
		titlePanel.setStyleName("titlePanel");
		calcType = new Label("");
		calcType.setStyleName("calcType");
		examTitle = new Label("");
		examTitle.setStyleName("examTitle");
		titlePanel.add(calcType);
		if (appW.getExam().isCheating()) {
			titlePanel.addStyleName("cheating");
			alertImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.exam_error(), 24);
			titlePanel.add(LayoutUtilW.panelRowIndent(alertImg, examTitle));
		} else {
			titlePanel.add(examTitle);
		}
	}

	private void buildContent(boolean isLogDialog) {
		teacherText.setStyleName("textStyle");
		if (!isLogDialog) {
			contentPanel.add(teacherText);
			contentPanel.add(buildBlock(durationLbl, duration));
		}
		contentPanel.add(buildBlock(dateLbl, date));
		contentPanel.add(buildBlock(startTimeLbl, startTime));
		if (!isLogDialog) {
			contentPanel.add(buildBlock(endTimeLbl, endTime));
		}
		if (appW.getExam().isCheating()) {
			activityPanel = buildActivityPanel(isLogDialog);
			contentPanel.add(buildBlock(activityLbl, activityPanel));
		}
	}

	private FlowPanel buildActivityPanel(boolean isLogDialog) {
		activityPanel = new FlowPanel();
		appW.getExam()
				.appendLogTimes(appW.getLocalization(), new ExamLogBuilder() {
					@Override
					public void addLine(StringBuilder sb) {
						addActivity(new Label(sb.toString()));
					}
				}, !isLogDialog);
		return activityPanel;
	}

	/**
	 * @param label
	 *            activity row
	 */
	protected void addActivity(Label label) {
		label.setStyleName("textStyle");
		activityPanel.add(label);
	}

	private static FlowPanel buildBlock(Widget caption, Widget text) {
		FlowPanel block = new FlowPanel();
		caption.setStyleName("captionStyle");
		text.setStyleName("textStyle");
		block.add(caption);
		block.add(text);
		return block;
	}

	@Override
	public void setLabels() {
		// title panel
		calcType.setText("GeoGebra "
				+ appW.getLocalization().getMenu("exam_calctype_graphing"));
		examTitle.setText(ExamUtil.status(appW));
		// content panel
		teacherText.setText(appW.getLocalization()
				.getMenu("exam_log_show_screen_to_teacher"));
		durationLbl.setText(appW.getLocalization().getMenu("Duration"));
		duration.setText(appW.getExam().getElapsedTimeLocalized());
		dateLbl.setText(appW.getLocalization().getMenu("exam_start_date"));
		date.setText(appW.getExam().getDate());
		startTimeLbl.setText(appW.getLocalization().getMenu("exam_start_time"));
		startTime.setText(appW.getExam().getStartTime());
		endTimeLbl.setText(appW.getLocalization().getMenu("exam_end_time"));
		endTime.setText(appW.getExam().getEndTime());
		activityLbl.setText(appW.getLocalization().getMenu("exam_activity"));
		// button panel
		exitBtn.setText(appW.getLocalization().getMenu("Exit"));
		okBtn.setText(appW.getLocalization().getMenu("OK"));
	}

	@Override
	public void onClick(Widget source) {
		if (source == okBtn) {
			hide();
		} else if (source == exitBtn) {
			onCancel();
		}
	}

	@Override
	public void show() {
		super.show();
		super.center();
		if (anchor != null) {
			anchor.addStyleName("selected");
		}
	}

	@Override
	public void onCancel() {
		if (appW.getGuiManager() instanceof GuiManagerW
				&& ((GuiManagerW) appW.getGuiManager())
						.getUnbundledToolbar() != null) {
			((GuiManagerW) appW.getGuiManager()).getUnbundledToolbar()
					.resetHeaderStyle();
		}
		appW.getLAF().toggleFullscreen(false);
		hide();
		returnHandler.callback("exit");
	}

	/**
	 * remove selected style of anchor btn
	 */
	public void removeSelection() {
		if (anchor != null) {
			anchor.removeStyleName("selected");
		}
	}

	@Override
	public void hide() {
		super.hide();
		removeSelection();
	}
}

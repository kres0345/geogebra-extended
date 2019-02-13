package org.geogebra.common.util;

import java.util.ArrayList;
import java.util.TreeSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;

/**
 * An exercise containing the assignments
 * 
 * @author Christoph
 *
 */
public class Exercise {

	private ArrayList<Assignment> assignments;
	private Kernel kernel;
	private App app;

	/**
	 * Create a new Exercise
	 * 
	 * @param app
	 *            application
	 */
	public Exercise(App app) {
		this.app = app;
		kernel = app.getKernel();
		assignments = new ArrayList<>();
	}

	/**
	 * Resets the Exercise to contain no user defined tools.
	 */
	public void reset() {
		assignments = new ArrayList<>();
	}

	/**
	 * Resets the Exercise and adds all user defined tools to the Exercise.
	 */
	public void initStandardExercise() {
		reset();
		GeoElement correct = app.getKernel().lookupLabel("correct");
		if (correct instanceof GeoBoolean) {
			addAssignment((GeoBoolean) correct);
		}
		else if (app.getKernel().hasMacros()) {
			ArrayList<Macro> macros = app.getKernel().getAllMacros();
			for (Macro macro : macros) {
				addAssignment(macro);
			}
		}
	}

	/**
	 * Checks all Assignments in this exercise.
	 * 
	 * Use getResult() and getHints
	 */
	public void checkExercise() {
		if (assignments.isEmpty()) {
			initStandardExercise();
		}
		// ArrayList<String> addListeners = app.getScriptManager()
		// .getAddListeners();
		// ArrayList<String> tmpListeners = new ArrayList<String>();
		// for (String addListener : addListeners) {
		// tmpListeners.add(addListener);
		// app.getScriptManager().unregisterAddListener(addListener);
		// }
		app.getScriptManager().disableListeners();
		for (Assignment assignment : assignments) {
			// if (assignment.isValid()) {
			assignment.checkAssignment();
			// }
		}
		app.getScriptManager().enableListeners();
		// for (String addListener : tmpListeners) {
		// app.getScriptManager().registerAddListener(addListener);
		// }
	}

	/**
	 * The overall fraction of the Exercise
	 * 
	 * If one Assignment has 100%, the overall fraction will be 1 minus the sum
	 * of the fractions of the Assignments having negative Fractions.<br>
	 * Otherwise the overall fraction will be the sum of all positive fractions
	 * capped at 1 minus all negative fractions and then capped at 0.
	 * 
	 * @return the sum of fractions for all assignments
	 */
	public double getFraction() {
		double fractionsumplus = 0;
		double fractionsumminus = 0;
		Assignment singleCorrect = null;
		double stdPrecision = Kernel.STANDARD_PRECISION;
		for (Assignment assignment : assignments) {
			double assignmenFraction = assignment.getFraction();

			if (assignmenFraction >= 0) {
				if (assignmenFraction >= 1 - stdPrecision) {
					singleCorrect = assignment;
				}
				fractionsumplus += assignmenFraction;
			} else {
				fractionsumminus += assignmenFraction;
			}
		}
		double fraction = 0;
		if (singleCorrect != null || fractionsumplus >= 1 - stdPrecision) {
			fraction = 1;
		} else {
			fraction = fractionsumplus;
		}
		fraction += fractionsumminus;
		return fraction < 0 + stdPrecision ? 0 : fraction;
	}

	/**
	 * Creates a new Assignment and adds it to the Exercise.
	 * 
	 * @param macro
	 *            the user defined Tool corresponding to the Assignment
	 * @return the newly created Assignment
	 */
	public GeoAssignment addAssignment(Macro macro) {
		GeoAssignment a = getAssignment(macro);
		if (a == null) {
			a = new GeoAssignment(macro);
			addAssignment(a);
		}
		return a;
	}

	/**
	 * Creates a new Assignment and adds it to the Exercise.
	 * 
	 * @param geo
	 *            the GeoBoolean which should be used for the check
	 * @return the newly created Assignment
	 */
	public BoolAssignment addAssignment(GeoBoolean geo) {
		BoolAssignment a = getAssignment(geo);
		if (a == null) {
			a = new BoolAssignment(geo, app.getKernel());
			addAssignment(a);
		}
		return a;
	}

	/**
	 * Creates a new Assignment and adds it to the Exercise.
	 * 
	 * @param geoBooleanLabel
	 *            the label of the geoBoolean which should be used for the check
	 * @return the newly created Assignment
	 */
	public BoolAssignment addAssignment(String geoBooleanLabel) {
		BoolAssignment a = new BoolAssignment(geoBooleanLabel, app.getKernel());
		assignments.add(a);
		return a;
	}

	/**
	 * @return all assignments contained in the exercise
	 */
	public ArrayList<Assignment> getParts() {
		return assignments;
	}

	/**
	 * Check if a macro is already used by this exercise
	 * 
	 * @param macro
	 *            the user defined tool
	 * @return true if this exercise uses the macro
	 */
	public boolean usesMacro(Macro macro) {
		boolean uses = false;
		for (Assignment assignment : assignments) {
			if (assignment instanceof GeoAssignment) {
				uses = uses
						|| ((GeoAssignment) assignment).getTool().equals(macro);
			}
		}
		return uses;
	}

	/**
	 * Check if a macro is already used by this exercise
	 * 
	 * @param macroID
	 *            the id of the user defined tool
	 * @return {@link #usesMacro(Macro)}
	 */
	public boolean usesMacro(int macroID) {
		return usesMacro(kernel.getMacro(macroID));
	}

	/**
	 * @param geo
	 *            the GeoBoolean to check for
	 * @return true if the GeoBoolean is used by the Exercise
	 */
	public boolean usesBoolean(GeoBoolean geo) {
		boolean uses = false;
		for (Assignment assignment : assignments) {
			if (assignment instanceof BoolAssignment) {
				uses = uses
						|| ((BoolAssignment) assignment).usesGeoBoolean(geo);
			}
		}
		return uses;
	}

	/**
	 * @return false if there are no Macros or any change to the standard
	 *         behavior has been made with the ExerciseBuilder <br />
	 *         true if there are Macros which can be used for checking
	 * 
	 */
	private boolean isStandardExercise() {
		boolean res = true;
		if (assignments.size() > 0) {
			res = false;
		}
		for (int i = 0; i < assignments.size() && res; i++) {
			if (assignments.get(i) instanceof GeoAssignment) {
				res = ((GeoAssignment) assignments.get(i)).getTool()
						.equals(app.getKernel().getAllMacros().get(i))
						&& !(assignments.get(i).hasHint()
								|| assignments.get(i).hasFraction()
								|| !((GeoAssignment) assignments.get(i))
										.getCheckOperation()
										.equals("AreEqual"));
			} else {
				res = false;
			}
		}
		return res;
	}

	/**
	 * @return XML describing the Exercise. Will be empty if no changes to the
	 *         Exercise were made (i.e. if isStandardExercise).<br />
	 *         Only Elements and Properties which are set or not standard will
	 *         be included.
	 * 
	 * @see Assignment#getAssignmentXML()
	 */
	public String getExerciseXML() {
		StringBuilder sb = new StringBuilder();
		if (!isStandardExercise()) {
			for (Assignment a : assignments) {
				sb.append(a.getAssignmentXML());
			}
		}
		return sb.toString();
	}

	/**
	 * Check if the Exercise has assignments
	 * 
	 * @return true if there are no assignments in this Exercise
	 */
	public boolean isEmpty() {
		return assignments.isEmpty();
	}

	/**
	 * Remove an assignment from this Exercise
	 * 
	 * @param assignment
	 *            the assignment to be removed from the Exercise
	 */
	public void remove(Assignment assignment) {
		assignments.remove(assignment);
	}

	/**
	 * Should be used if Macro is deleted
	 * 
	 * @param macro
	 *            the macro being used by the assignment which should be removed
	 */
	public void removeAssignment(Macro macro) {
		Assignment assignmentToRemove = null;
		for (Assignment assignment : assignments) {
			if (assignment instanceof GeoAssignment) {
				if (((GeoAssignment) assignment).getTool().equals(macro)) {
					assignmentToRemove = assignment;
				}
			}
		}
		if (assignmentToRemove != null) {
			remove(assignmentToRemove);
		}
	}

	/**
	 * Removes all Assignments from the Exercise
	 */
	public void removeAllAssignments() {
		reset();
	}

	/**
	 * @param macro
	 *            the macro being used by the assignment which should be
	 *            retrieved
	 * @return the assignment being used by the macro or null if macro isn't
	 *         used by the Exercise
	 */
	public GeoAssignment getAssignment(Macro macro) {
		GeoAssignment assignmentToReturn = null;
		for (Assignment assignment : assignments) {
			if (assignment instanceof GeoAssignment) {
				if (((GeoAssignment) assignment).getTool().equals(macro)) {
					assignmentToReturn = (GeoAssignment) assignment;
				}
			}
		}
		return assignmentToReturn;
	}

	/**
	 * @param geo
	 *            the GeoBoolean being used by the assignment which should be
	 *            retrieved
	 * @return the assignment which uses the geo or null if not used
	 */
	public BoolAssignment getAssignment(GeoBoolean geo) {
		BoolAssignment assignmentToReturn = null;
		for (Assignment assignment : assignments) {
			if (assignment instanceof BoolAssignment) {
				if (((BoolAssignment) assignment).usesGeoBoolean(geo)) {
					assignmentToReturn = (BoolAssignment) assignment;
				}
			}
		}
		return assignmentToReturn;
	}

	/**
	 * Adds an assignment to the exercise
	 * 
	 * @param assignment
	 *            the assignment to add to the Exercise
	 */
	public void addAssignment(Assignment assignment) {
		addAssignment(assignments.size(), assignment);
	}

	private boolean isValid(Assignment assignment) {
		return !assignments.contains(assignment) && assignment.isValid();
	}

	/**
	 * Adds an assignment to the exercise at specified index
	 * 
	 * @param assignmentIndex
	 *            the index to be used for the assignment, shifts all Elments =>
	 *            assignmentIndex to the right
	 * @param assignment
	 *            the assignment to add to the Exercise
	 */
	public void addAssignment(int assignmentIndex, Assignment assignment) {
		if (isValid(assignment)) {
			assignments.add(assignmentIndex, assignment);
		}
	}

	/**
	 * After a student started the Exercise in an CMS/LMS, values on which the
	 * solution depends should not get changed anymore, when reopening the file.
	 * If the exercise gets saved so that the teacher can review it or the
	 * student can work on it later again, the teacher as well as the student
	 * should see the same values the student saw on the first time. <br>
	 * If it doesn't get saved, this doesn't do any harm since the Assignment
	 * will be randomized again on next try.<br>
	 * The current values for displaying in CMS/LMS are also returned, so that
	 * the CMS/LMS can display the values and not cluttering up the applet with
	 * text, which can be shown more pretty in the CMS/LMS (most of the times).
	 * 
	 * @return all random geos of type GeoNumeric on which a BoolAssignment is
	 *         dependent and stops randomizing all of these values.
	 */
	public ArrayList<GeoNumeric> stopRandomizeAndGetValuesForBoolAssignments() {
		// TODO Auto-generated method stub
		ArrayList<GeoNumeric> geos = new ArrayList<>();
		TreeSet<GeoElement> predecessorsOfUsedBooleans = new TreeSet<>();

		for (Assignment assignment : assignments) {
			if (assignment instanceof BoolAssignment) {
				predecessorsOfUsedBooleans.addAll(((BoolAssignment) assignment)
						.getGeoBoolean().getAllPredecessors());
			}
		}

		for (GeoElement geo : predecessorsOfUsedBooleans) {
			if (geo instanceof GeoNumeric && geo.isLabelSet()) {
				geos.add((GeoNumeric) geo);
				if (geo.isRandomGeo()) {
					((GeoNumeric) geo).setRandom(false);
				}
			}
			// If we also want to stop randomizing other values which were
			// randomized by some AlgoRandom the user would have to specify the
			// variables used by the assignment. I think this is not necessary
			// since if we only stop randomization of
			// sliders I think it's easier to use for the teacher.
		}
		return geos;
	}

	/**
	 * If undo happens we have to update the Exercise to the new GeoElements.
	 * Also used to remove invalid assignments before showing
	 * Exercisebuilderdialog
	 */
	public void notifyUpdate() {
		for (Assignment assignment : assignments) {
			if (assignment instanceof BoolAssignment) {
				if (!((BoolAssignment) assignment).update()) {
					assignments.remove(assignment);
				}
			}
		}
	}

}

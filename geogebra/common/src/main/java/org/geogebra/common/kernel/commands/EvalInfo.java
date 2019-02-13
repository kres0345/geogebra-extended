package org.geogebra.common.kernel.commands;

import java.util.TreeMap;

import org.geogebra.common.kernel.arithmetic.SymbolicMode;

/**
 * Flags and auxiliary information used for evaluation of an expression
 */
public class EvalInfo {

	private boolean labelOutput;
	private TreeMap<String, String> casMap;
	private boolean redefineIndependent = true;
	private boolean scripting = true;
	private boolean simplifyIntegers = true;
	private boolean useCAS = true;
	private boolean autocreateSliders = true;
	private boolean autoAddDegree = false;
	private boolean fractions = false;
	private boolean forceUserEquation;
	private boolean updateRandom = true;
	private SymbolicMode symbolicMode = SymbolicMode.NONE;

	/**
	 * @param labelOut
	 *            whether output should be labeled
	 * @param casMap
	 *            cas evaluation map
	 */
	public EvalInfo(boolean labelOut, TreeMap<String, String> casMap) {
		this.labelOutput = labelOut;
		this.casMap = casMap;
	}

	/**
	 * @param labelOut
	 *            whether to label output
	 */
	public EvalInfo(boolean labelOut) {
		this.labelOutput = labelOut;
	}

	/**
	 * 
	 * @param labelOutput
	 *            whether label should be labeled
	 * @param redefineIndependent
	 *            whether independent geos may be redefined by processing the
	 *            expression
	 * @param updateRandom
	 *            whether random numbers should be updated on a redefinition
	 */
	public EvalInfo(boolean labelOutput, boolean redefineIndependent,
			boolean updateRandom) {
		this(labelOutput, redefineIndependent);
		this.updateRandom = updateRandom;
	}

	/**
	 * 
	 * @param labelOutput
	 *            whether label should be labeled
	 * @param redefineIndependent
	 *            whether independent geos may be redefined by processing the
	 *            expression
	 */
	public EvalInfo(boolean labelOutput, boolean redefineIndependent) {
		this.labelOutput = labelOutput;
		this.redefineIndependent = redefineIndependent;
	}

	/**
	 * @return whether outputs should be labeled
	 */
	public boolean isLabelOutput() {
		return this.labelOutput;
	}

	/**
	 * @return CAS cache
	 */
	public TreeMap<String, String> getCASMap() {
		return casMap;
	}

	/**
	 * @return whether independent geos may be redefined by processing the
	 *         expression
	 */
	public boolean mayRedefineIndependent() {
		return redefineIndependent;
	}

	/**
	 * @param scripts
	 *            whether to allow execution of scripting commands
	 * @return copy of this with adjusted scripting
	 */
	public EvalInfo withScripting(boolean scripts) {
		EvalInfo ret = copy();
		ret.scripting = scripts;
		return ret;

	}

	/**
	 * 
	 * @param cas
	 *            whether to allow using CAS for computations
	 * @return copy of this with adjusted CAS flag
	 */
	public EvalInfo withCAS(boolean cas) {
		EvalInfo ret = copy();
		ret.useCAS = cas;
		return ret;
	}

	/**
	 * @return whether scripting commands may be executed
	 */
	public boolean isScripting() {
		return scripting;
	}

	private EvalInfo copy() {
		EvalInfo ret = new EvalInfo(this.labelOutput, this.redefineIndependent);
		ret.scripting = this.scripting;
		ret.casMap = this.casMap;
		ret.simplifyIntegers = this.simplifyIntegers;
		ret.useCAS = this.useCAS;
		ret.autocreateSliders = this.autocreateSliders;
		ret.autoAddDegree = this.autoAddDegree;
		ret.fractions = this.fractions;
		ret.forceUserEquation = this.forceUserEquation;
		ret.updateRandom = this.updateRandom;
		return ret;
	}

	/**
	 * @return whether subnodes such as 4/2 may be simplified
	 */
	public boolean isSimplifyingIntegers() {
		return simplifyIntegers;
	}

	/**
	 * @param simplify
	 *            whether subnodes such as 4/2 may be simplified
	 * @return copy of this with adjusted flag
	 */
	public EvalInfo withSimplifying(boolean simplify) {
		EvalInfo ret = copy();
		ret.simplifyIntegers = simplify;
		return ret;
	}

	/**
	 * 
	 * @param labeling
	 *            whether labels for output are allowed
	 * @return copy of this with adjusted labeling flag
	 */
	public EvalInfo withLabels(boolean labeling) {
		if (labeling == labelOutput) {
			return this;
		}
		EvalInfo ret = copy();
		ret.labelOutput = labeling;
		return ret;
	}

	/**
	 * @return whether CAS may be used
	 */
	public boolean isUsingCAS() {
		return useCAS;
	}

	/**
	 * @return slider autocreation flag
	 */
	public boolean isAutocreateSliders() {
		return this.autocreateSliders;
	}

	/**
	 * @param sliders
	 *            whether this may ceate sliders
	 * @return derived eval info
	 */
	public EvalInfo withSliders(boolean sliders) {
		if (sliders == autocreateSliders) {
			return this;
		}
		EvalInfo ret = copy();
		ret.autocreateSliders = sliders;
		return ret;
	}

	/**
	 * @param addDegree
	 *            whether this may automatically add degree symbol
	 * @return derived eval info
	 */
	public EvalInfo addDegree(boolean addDegree) {
		if (addDegree == autoAddDegree) {
			return this;
		}
		EvalInfo ret = copy();
		ret.autoAddDegree = addDegree;
		return ret;
	}

	/**
	 * @param symbFractions
	 *            whether to show symbolic fractionss
	 * @return derived eval info
	 */
	public EvalInfo withFractions(boolean symbFractions) {
		if (symbFractions == this.fractions) {
			return this;
		}
		EvalInfo ret = copy();
		ret.fractions = symbFractions;
		return ret;
	}

	/**
	 * @param userEquation
	 *            whether to show symbolic fractionss
	 * @return derived eval info
	 */
	public EvalInfo withUserEquation(boolean userEquation) {
		if (userEquation == this.forceUserEquation) {
			return this;
		}
		EvalInfo ret = copy();
		ret.forceUserEquation = userEquation;
		return ret;
	}

	/**
	 * @return whether to show symbolic fractions
	 */
	public boolean isFractions() {
		return fractions;
	}

	/**
	 * @return whether to replace eg 45 with 45deg
	 */
	public boolean autoAddDegree() {
		return autoAddDegree;
	}

	/**
	 * @return whether to force output = input
	 */
	public boolean isForceUserEquation() {
		return forceUserEquation;
	}

	/**
	 * 
	 * @return whether random numbers should be updated on a redefinition
	 */
	public boolean updateRandom() {
		return updateRandom;
	}

	/**
	 * @param symbolic
	 *            symbolic mode
	 * @return this or copy with given symbolic mode
	 */
	public EvalInfo withSymbolicMode(SymbolicMode symbolic) {
		if (symbolicMode == symbolic) {
			return this;
		}
		EvalInfo copy = copy();
		copy.symbolicMode = symbolic;
		return copy;
	}

	/**
	 * @return variable resolution mode
	 */
	public SymbolicMode getSymbolicMode() {
		return symbolicMode;
	}

}

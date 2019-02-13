/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * VarString.java
 *
 * Created on 18. November 2001, 14:49
 */

package org.geogebra.common.kernel.arithmetic;

import java.util.HashSet;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.parser.FunctionParser;
import org.geogebra.common.main.MyParseError;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.MyMath;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * 
 * @author Markus Hohenwarter
 * 
 */
public class Variable extends ValidExpression {

	private String name;
	private Kernel kernel;

	/**
	 * Creates new VarString
	 * 
	 * @param kernel
	 *            kernel
	 * @param name
	 *            variable name
	 **/
	public Variable(Kernel kernel, String name) {
		this.name = name;
		this.kernel = kernel;
	}

	@Override
	public Variable deepCopy(Kernel kernel1) {
		return new Variable(kernel1, name);
	}

	/**
	 * @param tpl
	 *            string template
	 * @return variable name
	 */
	public String getName(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object.
	 */
	private GeoElement resolve(boolean throwError, SymbolicMode mode) {
		return resolve(
				mode == SymbolicMode.NONE, throwError, mode);
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object.
	 * 
	 * @param allowAutoCreateGeoElement
	 *            true to allow creating new objects
	 * @param throwError
	 *            when true, error is thrown when geo not found. Otherwise null
	 *            is returned in such case.
	 * @param mode
	 *            symbolic mode
	 * @return GeoElement with same label
	 */
	protected GeoElement resolve(boolean allowAutoCreateGeoElement,
			boolean throwError, SymbolicMode mode) {
		// keep bound CAS variables when resolving a CAS expression
		if (mode != SymbolicMode.NONE) {
			// resolve unknown variable as dummy geo to keep its name and
			// avoid an "unknown variable" error message
			return new GeoDummyVariable(kernel.getConstruction(), name);
		}

		// lookup variable name, create missing variables automatically if
		// allowed
		GeoElement geo = kernel.lookupLabel(name, allowAutoCreateGeoElement,
				mode);
		if (geo != null || !throwError) {
			return geo;
		}

		// if we get here we couldn't resolve this variable name as a GeoElement
		throw new MyParseError(kernel.getApplication().getLocalization(),
				"UndefinedVariable", name);
	}

	/**
	 * Looks up the name of this variable in the kernel and returns the
	 * according GeoElement object. For absolute spreadsheet reference names
	 * like A$1 or $A$1 a special ExpressionNode wrapper object is returned that
	 * preserves this special name for displaying of the expression.
	 * 
	 * @param mode
	 *            symbolic mode
	 * 
	 * @return GeoElement whose label is name of this variable or ExpressionNode
	 *         wrapping spreadsheet reference
	 */
	final public ExpressionValue resolveAsExpressionValue(SymbolicMode mode) {
		GeoElement geo = resolve(false, mode);
		if (geo == null) {
			if (kernel.getConstruction().isRegistredFunctionVariable(name)) {
				return new FunctionVariable(kernel, name);
			}
			ExpressionValue ret = replacement(kernel, name);
			return ret instanceof Variable ? resolve(true, mode) : ret;
		}

		// spreadsheet dollar sign reference
		// need to avoid CAS cell references, eg $1 (see #3206)
		if (name.indexOf('$') > -1 && !(geo instanceof GeoCasCell)
				&& !(geo instanceof GeoDummyVariable)) {
			// row and/or column dollar sign present?
			boolean colDollar = name.indexOf('$') == 0;
			boolean rowDollar = name.length() > 2 && name.indexOf('$', 1) > -1;
			Operation operation = Operation.NO_OPERATION;
			if (rowDollar && colDollar) {
				operation = Operation.DOLLAR_VAR_ROW_COL;
			} else if (rowDollar) {
				operation = Operation.DOLLAR_VAR_ROW;
			} else {
				// if (col$)
				operation = Operation.DOLLAR_VAR_COL;
			}

			// build an expression node that wraps the resolved geo
			return new ExpressionNode(kernel, geo, operation, null);
		}

		// standard case: no dollar sign
		return geo;
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param name
	 *            variable name
	 * @return interpretation, eg axxx -> a*x*x
	 */
	public static ExpressionValue replacement(Kernel kernel, String name) {
		// holds powers of x,y,z: eg {"xxx","y","zzzzz"}
		if (name.endsWith("'")
				&& kernel.getAlgebraProcessor().enableStructures()) {

			ExpressionValue ret = asDerivative(kernel, name);
			if (ret != null) {
				return ret;
			}

		}
		int[] exponents = new int[] { 0, 0, 0, 0, 0 };
		int i;
		ExpressionValue geo2 = getProduct(name, kernel);
		if (geo2 != null) {
			return geo2;
		}
		String nameNoX = name;
		int degPower = 0;
		while (nameNoX.length() > 0 && (geo2 == null)
				&& nameNoX.endsWith("deg")) {
			int length = nameNoX.length();
			degPower++;
			nameNoX = nameNoX.substring(0, length - 3);
			if (length > 3) {
				geo2 = kernel.lookupLabel(nameNoX);
			}

		}
		for (i = nameNoX.length() - 1; i >= 0; i--) {
			char c = name.charAt(i);
			if ((c < 'x' || c > 'z') && c != Unicode.theta && c != Unicode.pi) {
				break;
			}
			exponents[c == Unicode.pi ? 4
					: (c == Unicode.theta ? 3 : c - 'x')]++;
			nameNoX = name.substring(0, i);
			geo2 = kernel.lookupLabel(nameNoX);
			if (geo2 == null && "i".equals(nameNoX)) {
				geo2 = kernel.getImaginaryUnit();
			}
			Operation op = kernel.getApplication().getParserFunctions()
					.get(nameNoX, 1);
			if (op != null && op != Operation.XCOORD && op != Operation.YCOORD
					&& op != Operation.ZCOORD) {
				return xyzPiDegPower(kernel, exponents, degPower).apply(op);
			} else if (nameNoX.startsWith("log_")) {
				ExpressionValue index = FunctionParser.getLogIndex(nameNoX,
						kernel);
				ExpressionValue arg = xyzPiDegPower(kernel, exponents,
						degPower);
				if (index != null) {
					return new ExpressionNode(kernel, index, Operation.LOGB,
							arg);
				}
			}
			if (geo2 == null) {
				geo2 = getProduct(nameNoX, kernel);
			}
			if (geo2 != null) {
				break;
			}
		}
		while (nameNoX.length() > 0 && geo2 == null && (nameNoX.startsWith("pi")
				|| nameNoX.charAt(0) == Unicode.pi)) {
			int chop = nameNoX.charAt(0) == Unicode.pi ? 1 : 2;
			exponents[4]++;
			nameNoX = nameNoX.substring(chop);
			if (i + 1 >= chop) {
				geo2 = kernel.lookupLabel(nameNoX);
				if (geo2 == null) {
					geo2 = getProduct(nameNoX, kernel);
				}
			}
			if (geo2 != null) {
				break;
			}
		}
		if (nameNoX.length() > 0 && geo2 == null) {
			return new Variable(kernel, nameNoX);
		}
		ExpressionNode powers = xyzPowers(kernel, exponents);
		if (geo2 == null) {
			return exponents[4] == 0 && degPower == 0 ? powers
					: powers.multiply(piDegTo(exponents[4], degPower, kernel));
		}
		return exponents[4] == 0 && degPower == 0 ? powers.multiply(geo2)
				: powers.multiply(geo2)
						.multiply(piDegTo(exponents[4], degPower, kernel));
	}

	private static ExpressionNode getProduct(String label, Kernel cons) {
		int length = label.length();
		if (length == 3 && label.charAt(2) == '\'') {
			return product(cons, label.charAt(0) + "",
					label.charAt(1) + "'");

		} else if (length == 3 && label.charAt(1) == '\'') {
			return product(cons, label.charAt(0) + "'",
					label.charAt(2) + "");

		} else if (length == 4 && label.charAt(1) == '\''
				&& label.charAt(3) == '\'') {
			return product(cons, label.charAt(0) + "'",
					label.charAt(2) + "'");

		} else if (length == 2) {
			return product(cons, label.charAt(0) + "",
					label.charAt(1) + "");

		}
		return null;
	}

	private static ExpressionNode product(Kernel kernel,
			String string, String string2) {
		GeoElement el1 = kernel.lookupLabel(string);
		GeoElement el2 = kernel.lookupLabel(string2);
		if (el1 != null && el2 != null && el1.isNumberValue()
				&& el2.isNumberValue()) {
			return el1.wrap().multiplyR(el2);
		}
		return null;
	}

	private static ExpressionNode xyzPiDegPower(Kernel kernel, int[] exponents,
			int degPower) {
		if (exponents[4] == 0 && degPower == 0) {
			return xyzPowers(kernel, exponents);
		}
		return xyzPowers(kernel, exponents)
				.multiply(piDegTo(exponents[4], degPower, kernel));
	}

	private static ExpressionValue asDerivative(Kernel kernel,
			String funcName) {
		int index = funcName.length() - 1;
		int order = 0;
		while (index >= 0 && funcName.charAt(index) == '\'') {
			order++;
			index--;
		}
		GeoElement geo = null;
		while (index < funcName.length()) {
			String label = funcName.substring(0, index + 1);
			geo = kernel.lookupLabel(label);
			// stop if f' is defined but f is not defined, see #1444
			if (geo != null
					&& (geo.isGeoFunction() || geo.isGeoCurveCartesian())) {
				break;
			}

			order--;
			index++;
		}

		if (geo != null && (geo.isGeoFunction() || geo.isGeoCurveCartesian())) {
			return FunctionParser.derivativeNode(kernel, geo, order,
					geo.isGeoCurveCartesian(), new FunctionVariable(kernel));
		}
		return null;
	}

	private static ExpressionNode xyzPowers(Kernel kernel, int[] exponents) {
		return new ExpressionNode(kernel, new FunctionVariable(kernel, "x"))
				.power(new MyDouble(kernel, exponents[0]))
				.multiplyR(new ExpressionNode(kernel,
						new FunctionVariable(kernel, "y"))
								.power(new MyDouble(kernel, exponents[1])))
				.multiplyR(new ExpressionNode(kernel,
						new FunctionVariable(kernel, "z"))
								.power(new MyDouble(kernel, exponents[2])))
				.multiplyR(new ExpressionNode(kernel,
						new FunctionVariable(kernel, Unicode.theta_STRING))
								.power(new MyDouble(kernel, exponents[3])));
	}

	private static ExpressionNode piDegTo(int piPower, int degPower,
			Kernel kernel2) {
		ExpressionNode piExp = piPower > 0
				? new MySpecialDouble(kernel2, Math.PI, Unicode.PI_STRING)
						.wrap().power(piPower)
				: null;
		ExpressionNode degExp = degPower > 0 ? new MyDouble(kernel2, MyMath.DEG)
				.setAngle().wrap().power(degPower) : null;
		return degExp == null ? piExp
				: (piExp == null ? degExp : piExp.multiply(degExp));
	}

	@Override
	public HashSet<GeoElement> getVariables(SymbolicMode mode) {
		HashSet<GeoElement> ret = new HashSet<>();
		ret.add(resolve(true, mode));
		return ret;
	}

	@Override
	public void resolveVariables(EvalInfo info) {
		// this has to be handled in ExpressionNode
	}

	@Override
	public String toString(StringTemplate tpl) {
		return tpl.printVariableName(name);
	}

	@Override
	public String toValueString(StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	final public String toLaTeXString(boolean symbolic, StringTemplate tpl) {
		return toString(tpl);
	}

	@Override
	public boolean isNumberValue() {
		return false;
	}

	@Override
	final public boolean isVariable() {
		return true;
	}

	@Override
	final public boolean contains(ExpressionValue ev) {
		return ev == this;
	}

	@Override
	public String toOutputValueString(StringTemplate tpl) {
		return toValueString(tpl);
	}

	/**
	 * @return kernel
	 */
	public Kernel getKernel() {
		return kernel;
	}

	@Override
	public boolean hasCoords() {
		GeoElement ge = kernel.lookupLabel(name, false, SymbolicMode.NONE);
		if (ge != null && !(ge instanceof GeoDummyVariable)) {
			return ge.hasCoords();
		}

		return false;
	}

	/**
	 * force the name to s, used by RelativeCopy
	 * 
	 * @param s
	 *            new name
	 */
	public void setName(String s) {
		name = s;
	}

	/**
	 * @return variable name
	 */
	public String getName() {
		return name;
	}

	@Override
	public ExpressionNode wrap() {
		return new ExpressionNode(kernel, this);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.UNKNOWN;
	}

}

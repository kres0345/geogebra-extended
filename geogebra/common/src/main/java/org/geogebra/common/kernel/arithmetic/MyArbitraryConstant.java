package org.geogebra.common.kernel.arithmetic;

import java.util.ArrayList;
import java.util.Map;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.plugin.Operation;

/**
 * Arbitrary constant comming from native CAS
 * 
 * Each scope (cas cell or CAS using algo) should have an own instance of this
 * class
 */
public class MyArbitraryConstant {
	/** arbitrary integer */
	public static final int ARB_INT = 0;
	/** arbitrary double */
	public static final int ARB_CONST = 1;
	/** arbitrary complex number */
	public static final int ARB_COMPLEX = 2;

	private ArrayList<GeoNumeric> consts = new ArrayList<>();
	private ArrayList<GeoNumeric> ints = new ArrayList<>();
	private ArrayList<GeoNumeric> complexNumbers = new ArrayList<>();

	private ConstructionElement ce;
	private int position = 0;
	private boolean blocking;

	/**
	 * Creates new arbitrary constant handler
	 * 
	 * @param ce
	 *            associated construction element
	 */
	public MyArbitraryConstant(ConstructionElement ce) {
		this.ce = ce;
	}

	/**
	 * @param myDouble
	 *            constant index (global)
	 * @return real constant
	 */
	public GeoNumeric nextConst(double myDouble) {
		return nextConst(consts, ce.getConstruction().constsM, "c", myDouble);
	}

	/**
	 * @param myDouble
	 *            constant index (global)
	 * @return integer constant
	 */
	public GeoNumeric nextInt(double myDouble) {
		return nextConst(ints, ce.getConstruction().intsM, "k", myDouble);
	}

	/**
	 * @param myDouble
	 *            constant index (global)
	 * @return complex constant
	 */
	public GeoNumeric nextComplex(double myDouble) {
		return nextConst(complexNumbers, ce.getConstruction().complexNumbersM,
				"c", myDouble);
	}

	/**
	 * Returns a number for this scope that corresponds to given native index
	 * 
	 * @param consts2
	 *            all constants of given type (integer / real / complex) in this
	 *            scope cached from last computation
	 * 
	 * @param map
	 *            maps geo labels to constants in the whole construction
	 * @param prefix
	 *            prefix fro this constant type: c for real / complex, k for
	 *            integer
	 * @param index
	 *            index we got from native CAS, we assume it's getting bigger
	 *            with each computation but two constants with the same name
	 *            always refer to the same number eg
	 *            {x+arbonst(10),2x+arbconst(10)+arbconst(9)}
	 * @return element of consts2; if one with a given index already exists in
	 *         map take that one, otherwise pick the next one from consts2 (or
	 *         create one if there are not enough)
	 */
	protected GeoNumeric nextConst(ArrayList<GeoNumeric> consts2,
			Map<Integer, GeoNumeric> map, String prefix, double index) {
		Integer indexInt = Integer.valueOf((int) Math.round(index));
		GeoNumeric found = map.get(indexInt);
		if (found != null) {
			return found;
		}
		Construction c = ce.getConstruction();
		if (position >= consts2.size() || consts2.get(position) == null) {
			GeoNumeric add = new GeoNumeric(c);
			add.setSendValueToCas(false);
			// GGB-810
			// don't use constants as auxiliary objects
			// add.setAuxiliaryObject(true);
			boolean oldLabeling = c.isSuppressLabelsActive();
			c.setSuppressLabelCreation(false);
			// let construction know that we need new constant
			// after geoCasCell update
			c.setNotXmlLoading(true);
			add.setLabel(c.getIndexLabel(prefix));
			c.setNotXmlLoading(false);
			c.setSuppressLabelCreation(oldLabeling);
			AlgoDependentArbconst algo = new AlgoDependentArbconst(c, add, ce);
			c.removeFromConstructionList(algo);
			consts2.add(position, add);
			position++;
			map.put(indexInt, add);
			add.setIsDependentConst(true);
			return add;
		}
		GeoNumeric ret = consts2.get(position);
		map.put(indexInt, ret);
		// put existent constant into construction
		// after geoCasCell update
		if (c.isFileLoading()) {
			c.addToConstructionList(ret, false);
			c.putLabel(ret);
		} else {
			c.putLabel(ret);
		}
		position++;
		return ret;
	}

	/**
	 * Resets the handler; must be called before the first next*() call in each
	 * update of the CAS algo that is creating arbconsts
	 */
	public void reset() {
		position = 0;
	}

	/**
	 * Gets arbconst
	 * 
	 * @param i
	 *            index of arbconst within this handler
	 * @return arbconst
	 */
	public GeoNumeric getConst(int i) {
		return consts.get(i);
	}

	/**
	 * Return the list of constants
	 * 
	 * @return consts
	 */
	public ArrayList<GeoNumeric> getConstList() {
		if (consts != null) {
			return consts;
		}
		return null;
	}

	/**
	 * @return cas cell that contains the constant
	 */
	public GeoCasCell getCasCell() {
		if (isCAS()) {
			return (GeoCasCell) ce;
		}
		return null;
	}

	/**
	 * @param ce
	 *            - geoCasCell
	 */
	public void setCasCell(GeoCasCell ce) {
		if (isCAS()) {
			this.ce = ce;
		}
	}

	/**
	 * @return number of arbconsts, arbcomplexes and arbints together
	 */
	public int getTotalNumberOfConsts() {
		return consts.size() + ints.size() + complexNumbers.size();
	}

	/**
	 * Ensures that update of the constant (if visualised as slider) triggers
	 * update of resulting geo. This is not meant to be contained in
	 * construction protocol.
	 *
	 */
	public static class AlgoDependentArbconst extends AlgoElement {
		private GeoElement constant;
		private ConstructionElement outCE;

		// private ArrayList<AlgoElement> updateList;
		/**
		 * @param c
		 *            construction
		 * @param constant
		 *            the constant as a (complex) number
		 * @param outCE
		 *            element that needs updating if the constant changes
		 */
		public AlgoDependentArbconst(Construction c, GeoElement constant,
				ConstructionElement outCE) {
			super(c, false);
			this.constant = constant;
			this.outCE = outCE;
			/**
			 * if(outCE instanceof AlgoElement){ updateList = new ArrayList
			 * <AlgoElement>(); updateList.add((AlgoElement)outCE); }
			 */

			setInputOutput();
		}

		@Override
		protected void setInputOutput() {
			input = new GeoElement[] { constant };
			setDependencies();
		}

		@Override
		public void compute() {
			if (outCE instanceof AlgoElement
					&& ((AlgoElement) outCE).getOutputLength() == 1) {
				((AlgoElement) outCE).getOutput(0).updateCascade();
			} else if (outCE instanceof GeoCasCell) {
				outCE.update();
				if (((GeoCasCell) outCE).getTwinGeo() != null) {
					((GeoCasCell) outCE).getTwinGeo().update();
				}
			} else if (outCE != null) {
				outCE.update();
			}
		}

		@Override
		public Algos getClassName() {
			return Algos.Expression;
		}

		/**
		 * For cas cells replace CAS cell with the right cell on given row
		 */
		public void replaceOutCE() {
			if (outCE instanceof GeoCasCell) {
				this.outCE = cons
						.getCasCell(((GeoCasCell) outCE).getRowNumber());
			}
		}

	}

	/**
	 * @return whether this handler is bound with CAS cell
	 */
	public boolean isCAS() {
		return ce instanceof GeoCasCell;
	}

	/**
	 * TODO having just one position assumes that only one of real / integer /
	 * complex is used
	 * 
	 * @return index of next constant
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Switch to blocking mode where no arb constants are treated as 0
	 */
	public void startBlocking() {
		this.blocking = true;
	}

	/**
	 * @return whether this replaces +2kp with 0
	 */
	protected boolean isBlocking() {
		return blocking;
	}

	/**
	 * Replaces arbconst(), arbint(), arbcomplex() by auxiliary numerics
	 */
	public static class ArbconstReplacer implements Traversing {
		private MyArbitraryConstant arbconst;
		private static ArbconstReplacer replacer = new ArbconstReplacer();

		@Override
		public ExpressionValue process(ExpressionValue ev) {
			if (!ev.isExpressionNode() || arbconst == null) {
				return ev;
			}

			ExpressionNode en = (ExpressionNode) ev;

			if (arbconst.isBlocking()) {
				return handleSpecialCase(en);
			}
			if (en.getOperation() == Operation.MULTIPLY) {
				if (en.getLeft() != null && en.getLeftTree()
						.getOperation() == Operation.ARBCONST) {
					GeoNumeric newLeft = arbconst.nextConst(
							en.getLeftTree().getLeft().evaluateDouble());
					newLeft.setValue(1);
					newLeft.update();
					en.getRight().traverse(this);
					en.setLeft(newLeft);
				}
				if (en.getRight() != null && en.getRightTree()
						.getOperation() == Operation.ARBCONST) {
					GeoNumeric newRight = arbconst.nextConst(
							en.getRightTree().getLeft().evaluateDouble());
					newRight.setValue(1);
					newRight.update();
					en.getLeft().traverse(this);
					en.setRight(newRight);
				}
				return en;
			}
			if (en.getOperation() == Operation.ARBCONST) {
				return arbconst.nextConst(en.getLeft().evaluateDouble());
			}
			if (en.getOperation() == Operation.ARBINT) {
				return arbconst.nextInt(en.getLeft().evaluateDouble());
			}
			if (en.getOperation() == Operation.ARBCOMPLEX) {
				return arbconst.nextComplex(en.getLeft().evaluateDouble());
			}
			return en;
		}

		private static ExpressionValue handleSpecialCase(ExpressionNode en) {
			if (en.getOperation() == Operation.PLUS
					|| en.getOperation() == Operation.MINUS) {
				if (isMultipleOfArbconst(en.getRight())) {
					return en.getLeft();
				}
				if (isMultipleOfArbconst(en.getLeft())) {
					ExpressionValue ret = en.getRight();
					if (en.getOperation() == Operation.MINUS) {
						ret = new ExpressionNode(en.getKernel(),
								new MyDouble(en.getKernel(), -1),
								Operation.MULTIPLY, ret);
					}
					return ret;
				}
			} else if (isMultipleOfArbconst(en)) {
				return new MyDouble(en.getKernel(), 0);
			}
			return en;
		}

		/**
		 * @param arbconst
		 *            arbitrary constant handler
		 * @return replacer
		 */
		public static ArbconstReplacer getReplacer(
				MyArbitraryConstant arbconst) {
			replacer.arbconst = arbconst;
			return replacer;
		}
	}

	/**
	 * @param right
	 *            expression
	 * @return whether expression is a/b*arbconst() in some arrangement
	 */
	public static boolean isMultipleOfArbconst(ExpressionValue right) {
		if (right.isExpressionNode()) {
			ExpressionNode en = right.wrap();
			switch (en.getOperation()) {
			case ARBCONST:
			case ARBCOMPLEX:
			case ARBINT:
				return true;
			case MULTIPLY:
				return isMultipleOfArbconst(en.getLeft())
						|| isMultipleOfArbconst(en.getRight());
			case DIVIDE:
				return isMultipleOfArbconst(en.getLeft());
			default:
				return false;
			}
		}
		return false;
	}

}

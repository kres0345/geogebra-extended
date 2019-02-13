package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.Operation;

/**
 * CAS command that gets rewritten as operation in input bar
 * 
 * @author zbynek
 *
 */
public class CmdCAStoOperation extends CommandProcessor {

	private Operation op;

	/**
	 * @param kernel
	 *            kernel
	 * @param op
	 *            operation this command should be rewritten to
	 */
	public CmdCAStoOperation(Kernel kernel, Operation op) {
		super(kernel);
		this.op = op;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		ExpressionNode en = null;
		GeoElement[] args = resArgs(c);
		switch (op) {
		case YCOORD:
		case XCOORD:
			if (args.length != 1) {
				throw argNumErr(c);
			}
			en = new ExpressionNode(kernel, args[0], op, null);
			break;
		case MULTIPLY:
		case VECTORPRODUCT:
		case NPR:
			if (args.length != 2) {
				throw argNumErr(c);
			}
			en = new ExpressionNode(kernel, args[0], op, args[1]);
			break;
		default:
			throw new Error("Unhandled operation " + op);
		}
		return kernel.getAlgebraProcessor().processExpressionNode(en, info);
	}

}

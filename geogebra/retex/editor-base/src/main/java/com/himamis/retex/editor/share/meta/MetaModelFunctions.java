package com.himamis.retex.editor.share.meta;

import java.util.ArrayList;
import java.util.List;

class MetaModelFunctions {

	private static MetaFunction createFunction(Tag name, String tex,
			char key, MetaParameter[] parameters) {
		return new MetaFunction(name, tex, key, parameters);
    }

	private static MetaFunction createFunction(Tag name, String tex,
			MetaParameter[] parameters) {
		char key = name.getKey();
		return createFunction(name, tex, key, parameters);
    }

	private static MetaFunction createFunction(Tag name) {
		return createFunction(name, new MetaParameter[] { MetaParameter.BASIC });
	}

	private static MetaFunction createFunctionInitial(Tag name, String tex,
			int initial, MetaParameter[] parameters) {
		MetaFunction metaFunction = createFunction(name, tex, parameters);
        metaFunction.setInitialIndex(initial);
        return metaFunction;
    }

	private static MetaFunction createFunctionInsert(Tag name, String tex,
			int insert, MetaParameter[] parameters) {
		MetaFunction metaFunction = createFunction(name, tex, parameters);
        metaFunction.setInsertIndex(insert);
        return metaFunction;
    }

	private static MetaFunction createFunction(Tag name,
			MetaParameter[] parameters) {
		return createFunction(name, name.toString().toLowerCase(), parameters);
    }

	private static MetaParameter createParameterUp(int up) {
		return new MetaParameter(up, -1);
    }

	private static MetaParameter createParameterDown(int down) {
		return new MetaParameter(-1, down);
    }

	ListMetaGroup createGeneralFunctionsGroup() {
		List<MetaComponent> functions = new ArrayList<>();

		functions.add(createFunction(Tag.SUBSCRIPT));
		functions.add(createFunction(Tag.SUPERSCRIPT));

		functions.add(createFunctionInitial(Tag.FRAC, "\\frac", 1,
				new MetaParameter[] {
						createParameterDown(1), createParameterUp(0)
        }));

		functions.add(createFunction(Tag.SQRT, "\\sqrt", new MetaParameter[] {
				MetaParameter.BASIC
        }));

		functions.add(
				createFunctionInsert(Tag.NROOT, "\\sqrt", 1,
						new MetaParameter[] {
								createParameterDown(0), createParameterUp(1)
        }));

		functions.add(createFunctionInsert(Tag.LOG, "log", 1,
				new MetaParameter[] { MetaParameter.BASIC, MetaParameter.BASIC }));

		functions.add(
				createFunctionInsert(Tag.SUM, "\\sum", 3, new MetaParameter[] {
						createParameterUp(2), createParameterUp(2), createParameterDown(0),
						MetaParameter.BASIC
        }));

		functions.add(createFunctionInsert(Tag.PROD, "\\prod", 3,
				new MetaParameter[] {
						createParameterUp(2), createParameterUp(2), createParameterDown(0),
						MetaParameter.BASIC
        }));

		functions.add(
				createFunctionInsert(Tag.INT, "\\int", 2,
						new MetaParameter[] {
								createParameterUp(1), createParameterDown(0),
								MetaParameter.BASIC, MetaParameter.BASIC
        }));

		functions
				.add(createFunctionInsert(Tag.LIM, "\\lim", 2,
						new MetaParameter[] {
								MetaParameter.BASIC, MetaParameter.BASIC, MetaParameter.BASIC
        }));

		functions.add(createFunction(Tag.ABS));
		functions.add(createFunction(Tag.FLOOR));
		functions.add(createFunction(Tag.CEIL));

		return new ListMetaGroup(functions);
    }
}

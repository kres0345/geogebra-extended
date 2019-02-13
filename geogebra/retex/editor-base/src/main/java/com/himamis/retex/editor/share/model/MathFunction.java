/* MathFunction.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */

package com.himamis.retex.editor.share.model;

import com.himamis.retex.editor.share.meta.MetaFunction;
import com.himamis.retex.editor.share.meta.Tag;

/**
 * Function. This class is part of model.
 * <p>
 * function(arguments)
 *
 * @author Bea Petrovicova
 */
public class MathFunction extends MathContainer {

    private MetaFunction meta;

    /**
     * Use MathFormula.newFunction(...)
     */
    public MathFunction(MetaFunction meta) {
        super(meta.size());
        this.meta = meta;
    }

    /**
     * Gets parent of this component.
     */
    @Override
	public MathSequence getParent() {
        return (MathSequence) super.getParent();
    }

    @Override
	public MathSequence getArgument(int i) {
        return (MathSequence) super.getArgument(i);
    }

    public void setArgument(int i, MathSequence argument) {
        super.setArgument(i, argument);
    }

    /**
     * Uid name.
     */
	public Tag getName() {
        return meta.getName();
    }

    /**
     * TeX name.
     */
    public String getTexName() {
        return meta.getTexName();
    }

    /**
     * Insert Index
     */
    @Override
	public int getInsertIndex() {
        return meta.getInsertIndex();
    }

    /**
     * Initial Index
     */
    @Override
	public int getInitialIndex() {
        return meta.getInitialIndex();
    }

    /**
     * Up Index for n-th argument
     */
    public int getUpIndex(int n) {
        return meta.getUpIndex(n);
    }

    /**
     * Down Index for n-th argument
     */
    public int getDownIndex(int n) {
        return meta.getDownIndex(n);
    }

    @Override
	public MathFunction copy() {
        MathFunction function = new MathFunction(meta);
        for (int i = 0; i < arguments.size(); i++) {
            MathContainer component = getArgument(i);
            component = component.copy();
            function.setArgument(i, component);
        }
        return function;
    }

	public String getOpeningBracket() {
		return meta.getOpeningBracket();
    }

	public String getClosingBracket() {
		return meta.getClosingBracket();
    }

	@Override
	protected String getSimpleName() {
		return "Fn" + meta.getName();
	}
}

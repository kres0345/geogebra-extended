/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of lib-gwt-svg.
 * 
 * libgwtsvg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * libgwtsvg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with libgwtsvg.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package org.geogebra.web.generator;

import java.net.URL;

import org.geogebra.web.resources.Base64Encoder;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.dev.util.Util;
import com.google.gwt.resources.ext.AbstractResourceGenerator;
import com.google.gwt.resources.ext.ResourceContext;
import com.google.gwt.resources.ext.ResourceGeneratorUtil;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.rebind.SourceWriter;
import com.google.gwt.user.rebind.StringSourceWriter;

/**
 * Provides implementations of SVGResource.
 */
public class SVGResourceGenerator extends AbstractResourceGenerator {

	@Override
	public String createAssignment(TreeLogger logger, ResourceContext context,
	        JMethod method) throws UnableToCompleteException {
		// Extract the SVG name from the @Source annotation
		URL[] resources = ResourceGeneratorUtil.findResources(logger, context,
		        method);
		if (resources.length != 1) {
			logger.log(TreeLogger.ERROR,
			        "Exactly one resource must be specified", null);
			throw new UnableToCompleteException();
		}
		URL resource = resources[0];

		// The SVGResource is implemented as an anonymous inner class
		// xxx = new SVGResource() {
		// public OMSVGSVGElement getSvg() {
		// return OMSVGParser.parse("...");
		// }
		// };
		String toWrite = Util.readURLAsString(resource);
		/*
		 * if (getValidated(method)) { SVGValidator.validate(toWrite,
		 * resource.toExternalForm(), logger, null); }
		 */

		SourceWriter sw = new StringSourceWriter();
		sw.println("new " + SVGResource.class.getName() + "() {");
		sw.indent();
		sw.println("private String svg=\"" + Generator.escape(toWrite) + "\";");

		// Convenience when examining the generated code.
		sw.println("// " + resource.toExternalForm());

		sw.println("@Override");
		sw.println("public String getName() {");
		sw.indent();
		sw.println("return \"" + method.getName() + "\";");
		sw.outdent();
		sw.println("}");

		sw.println("@Override");
		sw.println("public String getUrl() {");
		sw.indent();
		sw.println("return \"data:image/svg+xml;base64,\" + "
				+ Base64Encoder.class.getName() + ".encodeBase64(svg);");
		sw.outdent();
		sw.println("}");

		sw.println("@Override");
		sw.println("public " + SafeUri.class.getName() + " getSafeUri() {");
		sw.indent();
		sw.println("return " + UriUtils.class.getName()
		        + ".fromSafeConstant(\"data:image/svg+xml;base64,\" + "
				+ Base64Encoder.class.getName() + ".encodeBase64(svg));");
		sw.outdent();
		sw.println("}");

		sw.outdent();
		sw.println("}");

		return sw.toString();
	}

}

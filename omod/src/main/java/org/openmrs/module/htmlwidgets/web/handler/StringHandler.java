/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.htmlwidgets.web.handler;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang.StringUtils;
import org.openmrs.annotation.Handler;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.html.*;

/**
 * FieldGenHandler for String Types
 */
@Handler(supports={String.class, Character.class}, order=50)
public class StringHandler extends WidgetHandler {
	
	/** 
	 * @see WidgetHandler#render(WidgetConfig)
	 */
	@Override
	public void render(WidgetConfig config, Writer w) throws IOException {
		
		Widget widget;
		
		if (config.getType() == Character.class) {
			widget = WidgetFactory.getInstance(TextWidget.class, config);
			config.setConfiguredAttribute("size", "2");
			config.setConfiguredAttribute("maxLength", "1");
		} else if (StringUtils.isNotBlank(config.getAttributeValue("codedOptions"))) {
			CodedWidget codedWidget = WidgetFactory.getInstance(SelectWidget.class, config);
			String optionList = config.getAttribute("codedOptions").getValue();
			String optionSeparator = config.getAttributeValue("optionSeparator", ",");
			String itemSeparator = config.getAttributeValue("itemSeparator", ":");
			for (String o : optionList.split(optionSeparator)) {
				if(o.indexOf(itemSeparator) != -1){
					String[] items = o.split(itemSeparator);
					if(items.length > 1){
						codedWidget.addOption(new Option(items[0], items[1], items[1], items[0]), config);
					} else {
						codedWidget.addOption(new Option(items[0], items[0], items[0], items[0]), config);
					}
				} else {
					codedWidget.addOption(new Option(o, o, o, o), config);
				}
			}
			widget = codedWidget;
		} else {
			String rows = config.getAttributeValue("rows");
			String cols = config.getAttributeValue("cols");
			if (StringUtils.isNotEmpty(rows) || StringUtils.isNotEmpty(cols) || "textarea".equals(config.getFormat())) {
				widget = WidgetFactory.getInstance(TextAreaWidget.class, config);
			}
			else {
				widget = WidgetFactory.getInstance(TextWidget.class, config);
				config.setConfiguredAttribute("size", "40");
			}
		}
		widget.render(config, w);
	}

	/** 
	 * @see WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String input, Class<?> type) {
		if (StringUtils.isNotBlank(input)) {
			if (Character.class.isAssignableFrom(type)) {
				if (input.length() > 1) {
					throw new IllegalArgumentException("Unable to parse '" + input + "' into a Character");
				}
				return (input.length() == 0 ? null : Character.valueOf(input.charAt(0)));
			}
			return input;
		}
		return null;
	}
}

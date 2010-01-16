/*
 * Copyright 2009 - 2010 by Andre Bossert
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package de.anbos.eclipse.logviewer.plugin;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import de.anbos.eclipse.logviewer.plugin.action.FileOpenAction;

public class EditorPropertyTester extends PropertyTester {

	public EditorPropertyTester() {
		super();
	}

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if("hasResourceSelection".equals(property) && receiver instanceof IWorkbenchPart){
            return hasResourceSelection((IWorkbenchPart)receiver) != null;
        }
		return false;
	}
	
    static public FileOpenAction hasResourceSelection(IWorkbenchPart part) {
		ISelection selection = ResourceUtils.getResourceSelection(part);
		if (selection != null) {
			FileOpenAction action = new FileOpenAction();
			action.selectionChanged(null, selection);
			if (action.isEnabled())
				return action;
		}
    	return null;
    }

}

/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.toolbar;

import java.util.EnumMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorSectionTitleKeyWords;

public class InsertSectionTitleAction extends ToolbarAction implements IMenuCreator {

    private Menu menu;
    private HeadlineViewSupport headlineViewSupport = new HeadlineViewSupport();
    private AsciiDoctorSectionTitleKeyWords valueVisibleOnToolbar;

    public InsertSectionTitleAction(AsciiDoctorEditor asciiDoctorEditor) {
        super(asciiDoctorEditor);
        valueVisibleOnToolbar = AsciiDoctorSectionTitleKeyWords.H1;
        initUI();
    }

    @Override
    public void run() {
        headlineViewSupport.buildAction(valueVisibleOnToolbar).run();
    }

    private void initUI() {
        setMenuCreator(this);
        initImageAndText();
    }

    private void initImageAndText() {
        setImageDescriptor(headlineViewSupport.getDescriptor(valueVisibleOnToolbar));
        setText(headlineViewSupport.getText(valueVisibleOnToolbar));
    }

    public void dispose() {
        if (menu != null) {
            menu.dispose();
            menu = null;
        }
    }

    public Menu getMenu(Menu parent) {
        return null;
    }

    public Menu getMenu(Control parent) {
        if (menu != null) {
            menu.dispose();
        }
        menu = new Menu(parent);
        for (AsciiDoctorSectionTitleKeyWords keyword : AsciiDoctorSectionTitleKeyWords.values()) {
            addActionToMenu(menu, headlineViewSupport.buildAction(keyword));
        }
        return menu;
    }

    protected void addActionToMenu(Menu parent, Action action) {
        ActionContributionItem item = new ActionContributionItem(action);
        item.fill(parent, -1);
    }

    /**
     * Common helper class to handle section level parts
     * 
     * @author Albert Tregnaghi
     *
     */
    private class HeadlineViewSupport {

        private EnumMap<AsciiDoctorSectionTitleKeyWords, ImageDescriptor> imgMap = new EnumMap<>(AsciiDoctorSectionTitleKeyWords.class);

        public HeadlineViewSupport() {
            for (AsciiDoctorSectionTitleKeyWords keyword : AsciiDoctorSectionTitleKeyWords.values()) {
                String path = "section_" + keyword.name().toLowerCase() + ".png";
                imgMap.put(keyword, createToolbarImageDescriptor(path));
            }
        }

        public ImageDescriptor getDescriptor(AsciiDoctorSectionTitleKeyWords keyword) {
            return imgMap.get(keyword);
        }

        public String getText(AsciiDoctorSectionTitleKeyWords keyword) {
            return keyword.getLabel();
        }

        public Action buildAction(AsciiDoctorSectionTitleKeyWords keyword) {
            Action action = new FormatTextAction(asciiDoctorEditor, getText(keyword), getDescriptor(keyword)) {

                @Override
                public void run() {
                    super.run();
                    valueVisibleOnToolbar = keyword;
                    initImageAndText();
                }

                @Override
                protected String formatPrefix() {
                    return keyword.getText() + " ";
                }

                @Override
                protected String formatPostfix() {
                    return "\n";
                }
            };
            return action;
        }
    }

}

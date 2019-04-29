/*
 * Copyright 2000-2019 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.artur.dndtest.board;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.EffectAllowed;
import com.vaadin.flow.component.html.H6;

public class CardComponent extends Button implements DragSource<CardComponent> {

    private Status status;

    public CardComponent(String name, Status status) {
        addClassName("card");

        add(new H6(name));

        setDraggable(true);
        setStatus(status);
        setEffectAllowed(EffectAllowed.MOVE);
    }

    private void add(Component c) {
        getElement().appendChild(c.getElement());
    }

    @Override
    public CardComponent getDragSourceComponent() {
        return this;
    }

    public Status getStatus() {
        return status;
    }

	public void setStatus(Status status) {
        this.status = status;

	}
}

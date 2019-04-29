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

import java.util.Arrays;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dnd.DragEndEvent;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.dnd.DragStartEvent;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropEvent;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@StyleSheet("frontend://styles.css")
@Route(value = "")
public class KanbanBoard extends FlexLayout {

    public static final String HIGHLIGHTED = "highlighted";

    public KanbanBoard() {
        super();
        getStyle().set("flex-wrap", "wrap");

        Arrays.stream(Status.values()).forEach(this::createNewColumn);

        getColumn(Status.INBOX).add(new NewCard(this::addNewCard));

        addInitialData();
    }

    private void createNewColumn(Status status) {
        VerticalLayout container = createColumnContainer();
        container.add(new H4(status.toString().replace("_", " ")));
        setColumnStatus(container, status);

        DropTarget<VerticalLayout> dropTarget = DropTarget.of(container);
        // By default we don't allow any drops
        dropTarget.setDropEffect(DropEffect.NONE);
        dropTarget.addDropListener(this::onDrop);

        add(container);
    }

    private void setColumnStatus(Component container, Status status) {
        ComponentUtil.setData(container, Status.class, status);
    }

    private Status getColumnStatus(Component component) {
        return ComponentUtil.getData(component, Status.class);
    }

    private VerticalLayout getColumn(Status status) {
        return (VerticalLayout) getChildren().filter(component -> getColumnStatus(component) == status).findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find correct status column"));
    }

    private VerticalLayout createColumnContainer() {
        VerticalLayout container = new VerticalLayout();
        container.setMargin(true);
        container.addClassName("column");
        container.setWidth("300px");
        container.setMinHeight("600px");
        return container;
    }

    private void onDrop(DropEvent<VerticalLayout> dropEvent) {
        if (dropEvent.getDragSourceComponent().isPresent()) {
            VerticalLayout targetColumn = dropEvent.getComponent();
            CardComponent sourceCard = (CardComponent) dropEvent.getDragSourceComponent().get();
            sourceCard.setStatus(getColumnStatus(targetColumn));
            if (sourceCard.getStatus() == Status.DONE) {
                // don't allow dnd for done cards
                DragSource.of(sourceCard, false);
            }
            targetColumn.add(sourceCard);
        }
    }

    private void addNewCard(CardComponent cardComponent) {
        getColumn(Status.INBOX).add(cardComponent);
        cardComponent.addDragStartListener(this::onDragStart);
        cardComponent.addDragEndListener(this::onDragEnd);
    }

    private void onDragStart(DragStartEvent<CardComponent> dragStartEvent) {
        CardComponent card = dragStartEvent.getComponent();
        // iterate all columns and mark acceptable drop locations
        getChildren().forEach(column -> {
            Status columnStatus = getColumnStatus(column);
            boolean validTransition = columnStatus.isValidTransition(card.getStatus());
            DropTarget<Component> dropTarget = DropTarget.of(column, validTransition);
            column.getElement().getClassList().set(HIGHLIGHTED, validTransition);
            if (validTransition) {
                dropTarget.setDropEffect(DropEffect.MOVE);
            }
        });
    }

    private void onDragEnd(DragEndEvent<CardComponent> dragEndEvent) {
        getChildren().forEach(column -> {
            DropTarget.of(column, false).setDropEffect(DropEffect.NONE);
            column.getElement().getClassList().set(HIGHLIGHTED, false);
        });
    }

    private void addInitialData() {
        CardComponent card = new CardComponent("Work hard", Status.INBOX);
        addNewCard(card);
    }

}

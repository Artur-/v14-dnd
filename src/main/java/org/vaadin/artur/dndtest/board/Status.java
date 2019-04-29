package org.vaadin.artur.dndtest.board;

public enum Status {
        INBOX, BACKLOG, WIP, REVIEW, DONE;

        public boolean isValidTransition(Status status) {
            return this == DONE || this.ordinal() - status.ordinal() == 1;
        }
    }

package com.envyclient.core.impl.events;

import me.ihaq.eventmanager.Event;
import me.ihaq.eventmanager.type.Cancellable;

public class KeepSprintEvent extends Event implements Cancellable {

    private boolean cancelled;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}

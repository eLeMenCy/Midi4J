package com.elemency.Midi4J.Examples;

import java.util.function.Supplier;

public enum AppOptionType {
    CALLBACKS(WithCallbacks::new),
    BROADCASTER(WithBroadcaster::new),
    BOTH(WithBoth::new),
    SEQUENCER(SimpleSequencer::new);

    private final Supplier<AppOption> option;

    AppOptionType(Supplier<AppOption> option) {
        this.option = option;
    }

    public Supplier<AppOption> getOption() {
        return this.option;
    }
}

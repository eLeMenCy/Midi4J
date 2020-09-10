package com.elemency.Midi4J.Examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class AppOptionsFactory {
    private final static Logger logger = LoggerFactory.getLogger(AppOptionsFactory.class);

    //    public static AppOption getAppOption(AppOptionType optionType) {
    public static AppOption getAppOption(String optionType) {

        AppOptionType appOptionType;

        try {
            appOptionType = AppOptionType.valueOf(optionType.toUpperCase());

        } catch (IllegalArgumentException iae) {

            logger.error(
                    "\n---------------------------------------------------------------------------------------------------\n" +
                    "The argument passed to run --args=\"" + optionType + "\" is invalid!\n\n" +
                    "Please either do:\n" +
                    "\trun --args=\"\" or simply 'run' (without quotes) to launch the sample with 'broadcaster' (default).\n" +
                    "\trun --args=\"callbacks\" to launch the sample with callback(s).\n" +
                    "\trun --args=\"both\" to launch the sample with both a callback and a broadcaster.\n" +
                    "\trun --args=\"sequencer\" to launch the simple sequencer sample.\n\n" +
                    "Reverting to default method (--args=\"broadcaster\")\n" +
                    "---------------------------------------------------------------------------------------------------\n"
            );

            appOptionType = AppOptionType.valueOf("BROADCASTER");
        }

        return appOptionType.getOption().get();
    }

    private enum AppOptionType {
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
}

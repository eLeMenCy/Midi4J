package com.elemency.Midi4J.Examples;

public class AppOptionsFactory {
//    public static AppOption getAppOption(AppOptionType optionType) {
    public static AppOption getAppOption(String optionType) {

        AppOptionType appOptionType = AppOptionType.valueOf(optionType.toUpperCase());
        return appOptionType.getOption().get();
    }
}

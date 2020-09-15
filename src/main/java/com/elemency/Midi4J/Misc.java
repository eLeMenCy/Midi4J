/*
 * Copyright (C) 2020 - eLeMenCy
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.elemency.Midi4J;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

    public static int getSymbolIndex(String text) {
        int index = -1;

        // Find the first occurrence of a non alphanumeric char.
        String pattern = "([^A-Za-z0-9])";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);

        // A non alpha numeric char is found...
        if (m.find()) {
            index = text.indexOf(m.group(0));
        }

        return index;
    }

    public static String clearSymbol(String text) {
        String result = text;

        int index = getSymbolIndex(text);
        text.replaceFirst("([^A-Za-z0-9])", " ");

        return result;
    }

    public static String getFirstWord(String name) {
        /* Sentence to decipher:
         Midi Through:Midi Through Port-0 14:0
         or
         Midi Through:Midi Through Port-0
         or
         Midi4J_OUT:OUT 45:0
         Midi4J@IN:IN 48:1
        */

//        String result = name;
//
//        // Find the firs occurence of a non alphanumeric char.
//        String pattern = "([^A-Za-z0-9])";
//        Pattern p = Pattern.compile(pattern);
//        Matcher m = p.matcher(name);
//
//        // A non alpha numeric char is found...
//        if (m.find()) {
//            // Find its position...
//            int separator = name.indexOf(m.group(0));
//            // Extract the substring from char 0 to symbol position - 1
//            result = name.substring(0, separator);
//        }

        String result = name;
        int index = getSymbolIndex(name);

        if (index != -1) {
            result = name.substring(0, index);
        }

        return result;
    }

    public static String findPattern(String data, String regex) {

        // regex to extract ALSA client:port ids: "\\w+:\\w+$"

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(data);
        String result = "";

        if (matcher.find()) {
            result = data.substring(matcher.start(), matcher.end());
//            System.out.println("client:port -> " + result);
        }

        return result;
    }
}


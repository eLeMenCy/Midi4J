/*
 * Copyright (C) 2020 - eLeMenCy, All Rights Reserved
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MidiException extends RuntimeException {
    private final Logger logger = LoggerFactory.getLogger(MidiException.class);

    /**
     * <code>MidiException</code> instance created without detailed message.
     */
    public MidiException() {
    }


    /**
     * <code>MidiException</code> instance created with local detailed message.
     *
     * @param msg detailed message.
     */
    public MidiException(String msg) {
        super(msg);
    }

    /**
     * <code>MidiException</code> instance created with link to current cause.
     *
     * @param cause exception current cause.
     */
    public MidiException(Throwable cause) {
        super(cause);
    }

    /**
     * <code>MidiException</code> instance created with detailed message and link to current cause.
     *
     * @param msg   detailed message.
     * @param cause exception current cause.
     */
    public MidiException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

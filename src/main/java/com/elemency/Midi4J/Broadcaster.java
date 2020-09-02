package com.elemency.Midi4J;

import com.sun.jna.Pointer;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Broadcaster implements Serializable
{
    private static final long serialVersionUID = 1L;

    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final LinkedList<BroadcastListener> listeners = new LinkedList<>();

    public static synchronized void register(BroadcastListener listener)
    {
        listeners.add(listener);
    }

    public static synchronized void unregister(BroadcastListener listener)
    {
        listeners.remove(listener);
        executorService.shutdown();
    }

    public static synchronized void broadcast(UUID uuid, MidiMessage midiMessage, Pointer userData)
    {
        for (final BroadcastListener listener : listeners)
        {
            executorService.execute(
                    () -> {
                        listener.receiveMessage(uuid, midiMessage, userData);
                    }
            );
        }
    }

    public interface BroadcastListener
    {
        void receiveMessage(UUID uuid, MidiMessage midiMessage, Pointer userData);
    }
}

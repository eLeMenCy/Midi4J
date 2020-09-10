package com.elemency.Midi4J;

import com.sun.jna.Pointer;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The Broadcaster/listener couple when used, receives all messages coming from Midi4J own internal native callback(s),
 * dispatch them to all subscribed listener(s) which themselves reroute them to the relevant connected target device.
 * See the "WithBroadcaster" sample.
 */
public class Broadcaster implements Serializable
{
    private static final long serialVersionUID = 1L;

    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final LinkedList<BroadcastListener> listeners = new LinkedList<>();

    /**
     * Add a Broadcastlistener to the list.
     *
     * @param listener  BroadcastListener
     */
    public static synchronized void register(BroadcastListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a Broadcastlistener from the list.
     *
     * @param listener  BroadcastListener
     */
    public static synchronized void unregister(BroadcastListener listener)
    {
        listeners.remove(listener);

    }

    /**
     * Shutdown the broadcaster.
     */
    public static void shutdownBroadcaster() {
        executorService.shutdown();
    }

    /**
     * Broadcast a midi message with its uuid and userData (if any).
     *
     * @param uuid          UUID
     * @param midiMessage   MidiMessage
     * @param userData      USerData
     */
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

    /**
     * Implement this interface in your application to receive all midi messages from internal native callback(s)
     * subscribed to this broadcaster..
     */
    public interface BroadcastListener
    {
        void receiveMessage(UUID uuid, MidiMessage midiMessage, Pointer userData);
    }
}

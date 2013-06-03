/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.util.ArrayList;

/**
 *
 * @author jsanchez
 */
public class Clock {
    private int tstates;
    private int timeout;
    private final ArrayList<ClockTimeoutListener> clockListeners = new ArrayList<ClockTimeoutListener>();

    public Clock() {

    }

    /**
     * Adds a new event listener to the list of event listeners.
     *
     * @param listener The new event listener.
     *
     * @throws NullPointerException Thrown if the listener argument is null.
     */
    public void addClockTimeoutListener(final ClockTimeoutListener listener) {

        if (listener == null) {
            throw new NullPointerException("Error: Listener can't be null");
        }

        if (!clockListeners.contains(listener)) {
            clockListeners.add(listener);
        }
    }

    /**
     * Remove a new event listener from the list of event listeners.
     *
     * @param listener The event listener to remove.
     *
     * @throws NullPointerException Thrown if the listener argument is null.
     * @throws IllegalArgumentException Thrown if the listener wasn't registered.
     */
    public void removeClockTimeoutListener(final ClockTimeoutListener listener) {

        if (listener == null) {
            throw new NullPointerException("Internal Error: Listener can't be null");
        }

        if (clockListeners.contains(listener)) {
            clockListeners.remove(listener);
        }
    }

    /**
     * @return the tstates
     */
    public int getTstates() {
        return tstates;
    }

    /**
     * @param states the tstates to set
     */
    public void setTstates(int states) {
        tstates = states;
    }

    public void addTstates(int states) {
        tstates += states;

        if (timeout > 0) {
            timeout -= states;
            if (timeout < 1) {
                for (final ClockTimeoutListener listener : clockListeners) {
                    listener.clockTimeout();
                }
            }
        }
    }

    public void reset() {
        tstates = 0;
    }

    public void setTimeout(int ntstates) {
        timeout = ntstates > 0 ? ntstates : 1;
    }
  
}

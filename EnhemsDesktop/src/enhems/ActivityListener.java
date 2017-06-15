package enhems;

import org.jnativehook.GlobalScreen;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by TeoLenovo on 3/25/2017.
 * This class is used for notifying server if this user is inactive
 * by detecting if mouse wasn't moved for some time
 */
public class ActivityListener implements NativeMouseMotionListener{

    private ScheduledExecutorService scheduler;
    private static final int minuteInteval = 15;

    private boolean mouseMoved;

    /*When this class is initialized, it will automatically
    * start listening to mouse activity*/
    public ActivityListener() {
       applicationStated();
    }

    public void applicationStated() {

        //to tell library not to write any log
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        //start the global hook and listener
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseMotionListener(this);
        } catch (Exception e) {
            MyLogger.log("Error registering native hook", e);
        }

        /* Note: JNativeHook does *NOT* operate on the event dispatching thread.
		 * Because Swing components must be accessed on the event dispatching
		 * thread, you *MUST* wrap access to Swing components using the
		 * SwingUtilities.invokeLater() or EventQueue.invokeLater() methods.
		 */
        GlobalScreen.setEventDispatcher(new SwingDispatchService());

        mouseMoved = false;
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new MouseMotion(), 1, minuteInteval, TimeUnit.MINUTES);
    }


    private class MouseMotion implements Runnable {
        @Override
        public void run() {
            ServerService.sendActivity(mouseMoved);
            mouseMoved = false;
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        mouseMoved = true;
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
        mouseMoved = true;
    }
}

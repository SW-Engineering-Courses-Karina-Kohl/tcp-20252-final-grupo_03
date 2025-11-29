import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.*;

public class GameTimer {
    private long startTime;
    private long elapsedTime;
    private boolean isRunning;
    private Timer timer;
    private SimpleDateFormat timeFormat;
    
    public GameTimer() {
        this.elapsedTime = 0;
        this.isRunning = false;
        
        // Set up time format for MM:SS
        timeFormat = new SimpleDateFormat("mm:ss");
        timeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        // Create timer that updates every second
        timer = new Timer(1000, e -> {
            if (isRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;
            }
        });
    }
    
    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime;
            isRunning = true;
            timer.start();
        }
    }
    
    public void stop() {
        if (isRunning) {
            isRunning = false;
            timer.stop();
            elapsedTime = System.currentTimeMillis() - startTime;
        }
    }
    
    public void reset() {
        stop();
        elapsedTime = 0;
    }
    
    public String getFormattedTime() {
        return timeFormat.format(new Date(elapsedTime));
    }
    
    public long getElapsedTime() {
        return elapsedTime;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void draw(Graphics g, int x, int y) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Time: " + getFormattedTime(), x, y);
    }
}

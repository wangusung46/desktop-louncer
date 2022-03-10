package desktoplouncer;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DesktopLouncer {

    private static final Logger LOG = Logger.getLogger(DesktopLouncer.class.getName());

    private static final Properties prop = new Properties();

    public static void main(String[] args) throws AWTException, IOException, ParseException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    long startTime = System.nanoTime();
                    Integer i = new Integer(0);
                    for (i = 1; i <= Integer.parseInt(getPropValues("TAB")); i++) {
                        altTab(i);
                        sleep(100);
                        position(Integer.parseInt(getPropValues("REFRESH_X")), Integer.parseInt(getPropValues("REFRESH_Y")));
                        click();
                    }
                    altTab(Integer.parseInt(getPropValues("TAB")));
                    sleep(Integer.parseInt(getPropValues("REFRESH")));
                    for (i = 1; i <= Integer.parseInt(getPropValues("TAB")); i++) {
                        position(Integer.parseInt(getPropValues("CLICK_X")), Integer.parseInt(getPropValues("CLICK_Y")));
                        scroll(Integer.parseInt(getPropValues("SCROLL")));
                        altTab(i);
                        sleep(100);
                        click();
                    }
                    long endTime = System.nanoTime();
                    long totalTime = endTime - startTime;
                    LOG.log(Level.INFO, "Total execution = {0} Millisecond", (totalTime) / 1000000);
                    System.exit(0);
                } catch (IOException | AWTException ex) {
                    LOG.getLogger(DesktopLouncer.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(0);
                }
            }
        }, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(now.format(dtf) + " " + getPropValues("TIME")));
        
    }

    public static void position(Integer x, Integer y) throws AWTException {
        Robot bot = new Robot();
        bot.mouseMove(x, y);
    }

    public static void click() throws AWTException {
        Robot bot = new Robot();
        bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void scroll(Integer role) throws AWTException {
        Robot r = new Robot();
        for (int i = 0; i < role; i++) {
            r.mouseWheel(1);
            sleep(1);
        }
    }

    public static void sleep(Integer sleap) {
        try {
            Thread.sleep(sleap);
        } catch (InterruptedException e) {
            LOG.info(e.getMessage());
        }
    }

    public static void altTab(Integer altTab) throws AWTException {
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ALT);
        for (int i = 0; i < altTab; i++) {
            robot.keyPress(KeyEvent.VK_TAB);
        }
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    public static String getPropValues(String value) throws IOException {
        String result = new String();
        try (InputStream inputStream = DesktopLouncer.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                LOG.info("Not Found");
            }
            result = prop.getProperty(value);
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }
        return result;
    }

}

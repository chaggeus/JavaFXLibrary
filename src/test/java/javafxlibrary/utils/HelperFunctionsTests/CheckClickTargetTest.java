package javafxlibrary.utils.HelperFunctionsTests;

import javafx.geometry.BoundingBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafxlibrary.TestFxAdapterTest;
import javafxlibrary.exceptions.JavaFXLibraryNonFatalException;
import javafxlibrary.utils.HelperFunctions;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testfx.service.query.BoundsQuery;

import java.util.ArrayList;
import java.util.List;

public class CheckClickTargetTest extends TestFxAdapterTest {

    private List<Window> windows;
    private Button button;

    @Mocked
    Stage stage;

    @Before
    public void setup() {
        windows = new ArrayList<>();
        windows.add(stage);
        button = new Button();
        HelperFunctions.setWaitUntilTimeout(0);
    }

    private void setupStageTests(int x, int y, int width, int height) {
        new Expectations() {
            {
                getRobot().listWindows(); result = windows;stage.isShowing(); result = true;
                stage.getX(); result = 250;
                stage.getY(); result = 250;
                stage.getWidth(); result = 250;
                stage.getHeight(); result = 250;
                getRobot().bounds((Button) any);
                BoundsQuery bq = () -> new BoundingBox(x, y, width, height);
                result = bq;
            }
        };
    }

    @Test
    public void checkClickTarget_WithinVisibleWindow() {
        setupStageTests(300, 300, 50, 50);
        Button result = (Button) HelperFunctions.checkClickTarget(button);
        Assert.assertEquals(button, result);
    }

    @Test
    public void checkClickTarget_OutsideVisibleWindow() {
        setupStageTests(480, 480, 50, 50);

        try {
            HelperFunctions.checkClickTarget(button);
            Assert.fail("Expected a JavaFXLibraryNonFatalException to be thrown");
        } catch (JavaFXLibraryNonFatalException e) {
            String target = "Can't click Button at [505.0, 505.0]: out of window bounds. To enable clicking outside " +
                    "of visible window bounds use keyword SET SAFE CLICKING | OFF";
            Assert.assertEquals(target, e.getMessage());
        }
    }

    @Test
    public void checkClickTarget_UsingStringLocator() {
        new Expectations() {
            {
                getRobot().lookup(".button").query(); result = button;
            }
        };
        HelperFunctions.setWaitUntilTimeout(1);
        setupStageTests(300, 300, 50, 50);
        Button result = (Button) HelperFunctions.checkClickTarget(".button");
        Assert.assertEquals(button, result);
    }

    @Test
    public void checkClickTarget_Disabled() {
        button.setDisable(true);
        try {
            HelperFunctions.checkClickTarget(button);
            Assert.fail("Expected a JavaFXLibraryNonFatalException to be thrown");
        } catch (JavaFXLibraryNonFatalException e) {
            String target = "Given target \"" + button + "\" did not become enabled within given timeout of 0 seconds.";
            Assert.assertEquals(target, e.getMessage());
        }
    }

    @Test
    public void checkClickTarget_NotVisible() {
        button.setVisible(false);
        try {
            HelperFunctions.checkClickTarget(button);
            Assert.fail("Expected a JavaFXLibraryNonFatalException to be thrown");
        } catch (JavaFXLibraryNonFatalException e) {
            String target = "Given target \"" + button + "\" did not become visible within given timeout of 0 seconds.";
            Assert.assertEquals(target, e.getMessage());
        }
    }
}

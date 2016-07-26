import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;

public class Class1{
    private WebDriver driver;
    private FirefoxProfile f;

    @BeforeMethod
    public void setUp(){
        f = new FirefoxProfile();
        f.setPreference("browser.download.dir", "d:");
        f.setPreference("browser.download.folderList", 2);
        f.setPreference("browser.helperApps.alwaysAsk.force", false);
        f.setPreference("browser.download.manager.showWhenStarting", false);
        driver = new FirefoxDriver(f);
    }

    @Test
    public void downloadTest() throws IOException, AWTException, InterruptedException {
        driver.get("https://the-internet.herokuapp.com/download");
        WebElement link = driver.findElement(By.cssSelector(".example a:nth-of-type(1)"));
        String str = link.getAttribute("href");
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpHead request = new HttpHead(str);
        HttpResponse response = httpClient.execute(request);
        String contentType = response.getFirstHeader("Content-Type").getValue();
        int contentLength = Integer.parseInt(response.getFirstHeader("Content-Length").getValue());

        Assert.assertThat(contentType, is("application/octet-stream"));
        Assert.assertThat(contentLength, is(not(0)));

        link.click();
        downloadFile();
        File file = new File("D:\\some-file.txt");

        Assert.assertTrue(file.exists());
    }

    @Test
    public void uploadTest(){
        try {
            driver.get("https://the-internet.herokuapp.com/upload");
            WebElement upload = driver.findElement(By.cssSelector("#file-upload"));
            upload.clear();
            upload.click();
            uploadFile("D:\\file.txt");
            WebElement submit = driver.findElement(By.cssSelector("#file-submit"));
            submit.click();
            WebElement result = driver.findElement(By.cssSelector("#uploaded-files"));
            Assert.assertEquals("file.txt",result.getText());
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void tearDown(){
        driver.close();
    }

    public void downloadFile() throws AWTException, InterruptedException {
        Robot robot = new Robot();
        Thread.sleep(1000);
        robot.keyPress(KeyEvent.VK_DOWN);
        robot.keyRelease(KeyEvent.VK_DOWN);
        Thread.sleep(1000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(1000);
    }

    public void copyClipboard(String path){
        StringSelection stringSelection = new StringSelection(path);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
    }

    public void uploadFile(String path) throws AWTException, InterruptedException {
        copyClipboard(path);
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        Thread.sleep(1000);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
}
package be.ac.ulb.infof307.g03.viewsControllers;

import com.sandec.mdfx.MDFXNode;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

/**
 * Class controlling the MarkdownView
 */
public class MarkdownViewController extends BaseViewController {

    private final Stage stage;

    public MarkdownViewController(String mdName) {
        stage = new Stage();
        StringBuilder mdfx = new StringBuilder();
        InputStream is = getClass().getClassLoader().getResourceAsStream("assets/"+mdName);
        if (is != null) {
            try(Scanner s = new Scanner(is).useDelimiter("\n")){
                while (s.hasNext()) {
                    mdfx.append(s.next());
                }
            } catch (IllegalStateException e) {
                showError("An error occurred", e.getMessage(), false);
            }
        }

        MDFXNode mdfxNode = new MDFXNode(mdfx.toString()) {

            @Override
            public void setLink(Node node, String link, String description) {
                node.setCursor(Cursor.HAND);
            }

            @Override
            public Node generateImage(String url) {

                if(url.equals("node://colorpicker")) {
                    return new ColorPicker();
                } else if (url.contains("guideline")) {
                    return new ImageView(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(url))));
                } else {
                    return super.generateImage(url);
                }
            }
        };
        mdfxNode.getStylesheets().add("be/ac/ulb/infof307/g03/views/assets/markdown.css");
        ScrollPane content = new ScrollPane(mdfxNode);
        content.setFitToWidth(true);
        Scene scene = new Scene(content, 1100,700);
        stage.setScene(scene);

    }

    public void show(){
        stage.show();
    }

}

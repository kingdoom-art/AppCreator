package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

//ну чтож, моя любимая часть, комментарии
//вообще их не люблю, но в этой работе они необходимы
public class Main extends Application {

    public static Pane linkRootPane;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //васе по стандарту, создаем родительскую форму
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Main.linkRootPane = (Pane)root.getChildrenUnmodifiable().get(1);
        //прикручиваем к ней обработчик нажатия клавиш
        //почему к ней?
        //по тому что только родитеський элемент шарит за нажатия
        //а надо нам это чтобы по кнопке делит удалять выделенные объекты
        root.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                //если выбран хоть ожин элемент
                if(Controller.chouse.size()!=0){
                    //и нажали нопку делит
                    if(keyEvent.getCode() == KeyCode.DELETE){
                        //получим форму, на которой располагаются маленькие
                        //диаграммики классов
                        Pane pane = (Pane)root.getChildrenUnmodifiable().get(1);
                        //идем по выбранным и удаляем
                        for(FormClassDiagram p : Controller.chouse){
                            pane.getChildren().remove(p);
                        }
                        //чистим сам список выбранных
                        Controller.chouse.clear();
                    }
                }
            }
        });

        primaryStage.setTitle("UML");
        primaryStage.setScene(new Scene(root, 600,400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }


}

package sample;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.io.Serializable;

//класс аля юзерконтрол
//по сути это форма созданная без разметки
//это было нужно, чтобы обращаться в объекту этого класса логично же
//если серьезно, то при создании разметки и привинчивании к нему контроллера
//сложно обратится к самому контроллеру, ссылку на него придется куда-то записать
//и этолишь часть, того гемороя, что я избежал
public class FormClassDiagram extends Pane{

    public TextField className;
    public TextField extendsClass;
    //поля класса
    private ListView<FieldClass> fieldClass;
    private Pane thisPane;
    private FormClassDiagram thisObj;

    //функции ниже просто создают форму в том виде, который будет представлен
    public FormClassDiagram(){
        super();
        this.setPrefSize(207,198);
        this.setStyle("-fx-background-color: DARKGRAY;");

        this.getChildren().add(initiliazeGridPane());
        thisPane = this;
        thisObj = this;

        //элемент зажат
        thisPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //говорим, что зажата именно эта форма
                Controller.itsIm = thisPane;
                //говорим, где зажата
                Controller.startX = mouseEvent.getX();
                Controller.startY = mouseEvent.getY();
            }
        });
        //элемент опущен
        thisPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //чистим - чистим трубочиста
                Controller.itsIm = null;
            }
        });
        //выбор этого элемента
        thisPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //если дабл клик - выделяем, просто обводим элемент линеей
                //темного цвета иначе, удаляем выделения
                if(mouseEvent.getClickCount()==2){
                    thisPane.setStyle("-fx-border-color: gray; -fx-border-width: 2;-fx-background-color: DARKGRAY;");
                    Controller.chouse.add(thisObj);
                }else{
                    thisPane.setStyle("-fx-background-color: DARKGRAY;");
                    Controller.chouse.remove(thisObj);
                }
            }
        });
    }

    private GridPane initiliazeGridPane(){
        GridPane gridPane = new GridPane();
        gridPane.getColumnConstraints().add(new ColumnConstraints(207));
        gridPane.setAlignment(Pos.CENTER_LEFT);
        gridPane.getRowConstraints().add(new RowConstraints(28));
        gridPane.getRowConstraints().add(new RowConstraints(28));
        gridPane.getRowConstraints().add(new RowConstraints(141-28)); //в падлу считать
        gridPane.getRowConstraints().add(new RowConstraints(28));

        gridPane.add(initiliazeClassNameRow(),0,0);
        gridPane.add(initiliazeExtendsClassRow(),0,1);
        initiliazeListFieldClassRow();
        gridPane.add(fieldClass,0,2);
        gridPane.add(initiliazeAddFieldRow(),0,3);
        return gridPane;
    }

    private HBox initiliazeClassNameRow(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        Label name = new Label("Class name");
        HBox.setMargin(name,new Insets(0,5,0,5));
        className = new TextField();
        className.setPrefSize(116,25);
        HBox.setMargin(className,new Insets(0,5,0,5));
        hBox.getChildren().addAll(name, className);
        return hBox;
    }

    private HBox initiliazeExtendsClassRow(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        Label name = new Label("Extends class");
        HBox.setMargin(name,new Insets(0,5,0,5));
        extendsClass = new TextField();
        extendsClass.setPrefSize(116,25);
        HBox.setMargin(extendsClass,new Insets(0,5,0,5));
        hBox.getChildren().addAll(name, extendsClass);
        return hBox;
    }

    private Button initiliazeAddFieldRow(){
        Button add = new Button("Add Field");
        add.setPrefSize(203,25);
        add.setOnAction(e->{addField();});
        GridPane.setMargin(add,new Insets(0,2,0,2));
        return add;
    }

    private void initiliazeListFieldClassRow(){
        fieldClass = new ListView<>();
        fieldClass.setPrefSize(203,141);
        GridPane.setMargin(fieldClass,new Insets(0,2,0,2));
    }


    private void addField(){
        FieldClass dC = new FieldClass();
        fieldClass.getItems().add(dC);
    }

    //извлечение из сохраненных
    public void setDataSources(DiagramSave ds){
        this.className.setText(ds.className);
        this.extendsClass.setText(ds.extendsClass);
        for(Pair<String,String> s:ds.fields){
            FieldClass dC = new FieldClass();
            dC.setDataSources(s);
            fieldClass.getItems().add(dC);
        }
    }

    //изначально просто планировалось записать в файл
    //но я же ленивый чтобы потом парсить при извлечении
    //по этому создал класс для сериализации, по скольюку данный класс я не могу серилизовать
    //он не реализует интерфейс
    //по этому я создал еще 5-6 функций для сохранения и извлечения
    //и класс сохранятель
    //а так не я не из ленивых
    @Override
    public String toString(){
        String result=className.getText()+"\n";
        result+=extendsClass.getText()+"\n";
        for (FieldClass d:fieldClass.getItems()) {
            result+=d.toString();
        }
        return result+"~";
    }

    public DiagramSave toDiagramSave(){
        DiagramSave clas = new DiagramSave(className.getText(),extendsClass.getText());
        for (FieldClass d:fieldClass.getItems()) {
            clas.fields.add(d.toPair());
        }
        return clas;
    }

}

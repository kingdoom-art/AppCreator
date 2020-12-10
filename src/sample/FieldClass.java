package sample;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.util.Pair;

//поля нашего класса
//наследуем от ашбокса, чтобы норм размещать
public class FieldClass extends HBox{

    public ComboBox<String> typeData;
    public TextField nameField;

    public FieldClass(){
        super();
        typeData = new ComboBox<String>();
        nameField = new TextField();
        initilaize();
    }

    public void initilaize(){
        //ибо нужна сязка 1-много сделаем взможность вытащить
        //типы других классов, которые есть на диаграмме
        typeData.setOnShown(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                typeData.getItems().clear();
                typeData.getItems().addAll("int","double","String");
                //в С# потребовалась бы 1 строка ибо есть такая штука, как
                //linq в джаве аналогов не нашел(((, был варик с stream api,
                //но не робит со строками он, а жаль
                for (Node f:Main.linkRootPane.getChildren()
                ) {
                    if(f instanceof FormClassDiagram){
                        if(((FormClassDiagram) f).className.getText().length()!=0){
                            typeData.getItems().add(((FormClassDiagram) f).className.getText());
                        }
                    }
                }
            }
        });

        typeData.getItems().addAll("int","double","String");
        typeData.setMaxSize(100,20);
        nameField.setMaxSize(100,20);
        this.getChildren().addAll(typeData,nameField);
    }

    public void setDataSources(Pair<String,String> p){
        typeData.setValue(p.getKey());
        nameField.setText(p.getValue());
    }

    @Override
    public String toString(){
        return typeData.getValue()+" "+nameField.getText()+"\n";
    }

    public Pair<String, String> toPair(){
        return new Pair<String, String>(typeData.getValue(),nameField.getText());
    }

}

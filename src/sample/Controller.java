package sample;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.util.LinkedList;

public class Controller {
    //поле нужно чтобы двигать дочерние элементы относительно себя
    //ибо координаты мы считываем именно с родительского элемента
    //это зажатый дочерний элемент
    public static Pane itsIm;
    //наш список выбранных элементов
    public static LinkedList<FormClassDiagram> chouse = new LinkedList<>();
    public static LinkedList<DiagramSave> obj = new LinkedList<>();
    //еще поля для движения объектов
    public static double startX, startY;
    public AppCreater newProject;

    public Pane root;
    public ComboBox<String> doAct;

    //подумал я нахрена эти милион кнопок и сделал комбобокс
    public void selectedItems(){
        System.out.println(doAct.getSelectionModel().getSelectedItem());
        //после сбороса выбранного элемента комбобокса
        //если здесь не присунуть проверку на нул, то полетит вся прога к
        //по причинному месту
        switch (doAct.getSelectionModel().getSelectedItem()==null ?
        "" : doAct.getSelectionModel().getSelectedItem()){
            case "Добавить класс":addClassDiagram(); break;
            case "Сохранить диаграмму": saveDiagram(); break;
            case "Наследовать": inheritanceClass(); break;
            case "Открыть диаграмму": openDiagram(); break;
            case "Создать проект": createApp(); break;
            case "Сохранить классы в проект": saveClassInProject(); break;
            case "Обновить классы проекта": f5Class(); break;
            case "Открыть проект" : openProject(); break;
            default:return;
        }
    }

    //сохраняем все классы
    public void saveClassInProject(){
        if(newProject != null){
            newProject.createGroovyClass();
            System.out.println("классы сохранены");
        }
    }

    //обновим классы, по скольку незя просто взять и запихнуть их в проект
    public void f5Class(){
        if(newProject != null){
            newProject.setMasClass(getChilgren());
            newProject.f5Class();
            System.out.println("классы обновлены");
        }
    }

    public void addClassDiagram(){
        //все понятно, добавляет новый элемент управления
        root.getChildren().add(new FormClassDiagram());
    }

    public void saveDiagram(){
        //по сути тут все понятно, единственное оговорка
        //LinkedList нам нужен для сериализации ибо он реализует нужный интерфейс

        LinkedList<DiagramSave> mas = getChilgren();
        if(mas.size()>0){
            FileChooser file = new FileChooser();
            File f = file.showSaveDialog(((Stage)root.getScene().getWindow()));
            System.out.println(Save.saveDiagram(mas, f.getAbsolutePath()));
            System.out.println("диаграмма сохранена");
        }
    }

    //вынес получение наследников, ибо этот код уже юзается в 2-х функциях
    public LinkedList<DiagramSave> getChilgren(){
        LinkedList<DiagramSave> mas = new LinkedList<>();
        for(Node p: root.getChildren()){
            if(p instanceof FormClassDiagram)
            {
                mas.add(((FormClassDiagram) p).toDiagramSave());
            }
        }
        return  mas;
    }

    public void openDiagram(){
        //с извлечением тоже все понятно
        //только при создании нового класса указываю аля источник данных,
        //который был получен из файла
        FileChooser file = new FileChooser();
        File f = file.showOpenDialog(((Stage)root.getScene().getWindow()));
        if(f==null){
            return;
        }
        LinkedList<DiagramSave> mas = Save.openDiagram(f.getAbsolutePath());
        if(mas==null) return;
        for(DiagramSave ds:mas){
            FormClassDiagram form = new FormClassDiagram();
            form.setDataSources(ds);
            root.getChildren().add(form);
        }
        System.out.println("Диаграмма классов открыта");
    }

    public void inheritanceClass(){
        //ооо, наследование, самая веселая фигня
        //как это работает: выбираем несколько объектов
        //тыкаем наследовать и вуаля, готово, они все наследники первого
        if(Controller.chouse.size() > 1) {
            for(int i = 1;i<Controller.chouse.size();i++){
                //запишем во все диаграмки их родителя
                Controller.chouse.get(i).extendsClass.setText(Controller.chouse.get(0).className.getText());
            }
            System.out.println("Успешно унаследован");
        }
    }

    public void createApp(){
        DirectoryChooser file = new DirectoryChooser();
        File f = file.showDialog(((Stage)root.getScene().getWindow()));
        if(f==null){
            return;
        }
        String path = f.getAbsolutePath();
        String nameProj = f.getName();
        newProject = new AppCreater(getChilgren(), path,nameProj);
        newProject.start();
        System.out.println("create proj "+path);
    }

    public void openProject(){
        DirectoryChooser file = new DirectoryChooser();
        File f = file.showDialog(((Stage)root.getScene().getWindow()));
        if(f==null){
            return;
        }
        String path = f.getAbsolutePath();
        String nameProj = f.getName();
        newProject = new AppCreater(getChilgren(), path,nameProj);
        System.out.println("open proj "+path);
    }

    @FXML
    void initialize(){
        //чистим выбраный элемент при выборе комбобокса
        //ибо 2 раза одно и тоже действие не выбрать без чистки
        doAct.setOnShown(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                doAct.getSelectionModel().clearSelection();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //если элемент выбран (зажат правой кнопкой мыши)
                //реализация это в классе FormClassDiagram
                if(itsIm!=null){
                    //двигаем дочерний элемент при этом учтем
                    //вычитание нужно, чтобы фтома двигалась относительно координат
                    //мыши на выбранном элемента, а не от начала координат
                    itsIm.setLayoutX(mouseEvent.getX()-startX);
                    itsIm.setLayoutY(mouseEvent.getY()-startY);
                }
            }
        });
    }

}
//класс для сериализации
class Save{

    public static boolean saveDiagram(LinkedList<DiagramSave> obj, String path){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path+".ser")))
        {
            oos.writeObject(obj);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    public static LinkedList<DiagramSave> openDiagram(String path){
        LinkedList<DiagramSave> obj;
        try(ObjectInputStream oos = new ObjectInputStream(new FileInputStream(path)))
        {
            obj = (LinkedList<DiagramSave>) oos.readObject();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            return null;
        }

        return obj;
    }
}
package sample;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

public class AppCreater {
    LinkedList<DiagramSave> masClass;
    //className - какнить потом вытащу из списка выше
    String startCmd = "cmd /c start cmd.exe /K ";
    String path; //путь до папки проекта
    String projName; //название проекта
    String cdProg = "cd ";//+projectName
    String createProject = "grails create-app "; //+projectName
    String createController = "grails generate-all \"*\"";//+className
    String startApp = "grails run-app ";

    public AppCreater(LinkedList<DiagramSave> masClass, String path, String projName) {
        //чекаем чтобы не передался пустой массив, чтобы программа не упала
        //как и моя вера в свои силы после стольких танцев с бубном для
        //выполнения проги
        this.masClass = masClass==null ? new LinkedList<DiagramSave>():masClass;
        this.path = path;
        this.projName = projName;
    }

    //создается заготовка проекта без классов
    public void start(){
        try
        {
            Process proc = Runtime.getRuntime().exec(getStringCreater());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        createBatFile();
    }

    //няя, логика такая:
    //содаеем проект с классами, а потом будем их перезаписывать
    //точнее их содержимое
    public String getStringCreater(){
        String classes = getClassName();

        String result = startCmd + "\""+ //запуск цмд
                cdProg+path+
                " && " + createProject+projName+ // создадим проект с переданным названием
                " && "+cdProg+path+"\\"+projName+
                classes+
                "\"";
        return  result;
    }

    public String getClassName(){
        String classes = "";
        for (DiagramSave d: masClass
        ) {
            if (d.className.length() == 0) {
                continue;
            }
            classes+=" && grails create-domain-class "+d.className;
        }
        return classes;
    }

    //создаем файл для создания контроеллеров и запуска приложения
    //ибо каждое сохранение запускать прогу бдет не тру
    public String getBatFile(){
        String result = cdProg + path + //перейдем к проекту
                " && " + cdProg + projName +
                " && " + createController+//создадим контроллеры
                " && " + startApp + //запускаем приложение
                "\n pause;";
        //перемещам созданные классы
        //" && md "+path+"\\" + projName+"\\grails-app\\domain\\"+projName+
        //" && move " +path+"\\" + projName + "Class\\*.* "+
        //path+"\\" + projName+"\\grails-app\\domain\\"+projName+"\\"+
        return result;
    }

    public void createBatFile(){
        //создадим батник, для компилинга классов и запуска приложения
        try(FileWriter writer = new FileWriter(path + "\\"+projName+".bat", false))
        {
            writer.write(getBatFile());
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    //самое гоморное, по скольку процесс создания проекта еще не завершился
    //в цмд, а классы уже надо куда-то записать, по этому последовательно функции не вызвать
    public void createGroovyClass(){
        for (DiagramSave d: masClass
             ) {
            if(d.className.length()==0){
                continue;
            }
            //идем по всем классам, которые названы и сод=здаем их в указанном месте
            try(FileWriter writer = new FileWriter(path+"\\"+
                    projName+"\\grails-app\\domain\\"+projName+"\\"+
                    d.className+
                    ".groovy", false))
            {
                String res = "package "+projName+"\n\n";
                writer.write(res+d.getGroovyClass());
                writer.flush();
            }
            catch(IOException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public void f5Class(){
        String classes = getClassName();
        try
        {
            Process proc = Runtime.getRuntime().exec(startCmd + "\""+cdProg+path+" && "+cdProg+path+"\\"+projName+
                    classes);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setMasClass(LinkedList<DiagramSave> masClass) {
        this.masClass = masClass;
    }
}

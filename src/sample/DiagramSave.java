package sample;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.LinkedList;

//класс скилет нашено класса на диаграмме классов
public class DiagramSave implements Serializable {

    private static final long serialVersionUID = 1L;

    public String className;
    public String extendsClass;
    public LinkedList<Pair<String, String>> fields;

    public DiagramSave(String className,String extendsClass){
        this.className = className;
        this.extendsClass = extendsClass;
        fields = new LinkedList<>();
    }

    //вернем текст нашего будущего класса
    public String getGroovyClass(){
        if(className.length()==0){
            return "";
        }

        String result = "class "+className+" {\n";
        String constraints = "static constraints = {\n";
        String field ="";
        //если вдруг в классе не будет полей, то ту сринг вернет пустую строку
        String toString = "\"\"";
        //идем по полям, если они заполены кидаем в класс
        for (Pair<String, String> d:fields) {
            if(d.getKey().length()!=0 && d.getValue().length()!=0){
                constraints+=d.getValue()+"()\n";
                field+=d.getKey()+" "+d.getValue()+"\n";
                //чекаем, чтобы у тустринга были поля ток основных классов
                //для того чтобы не тянуть инфу из многих ко многим
                if("Stringintdouble".contains(d.getKey()))
                    toString+="+\" \"+"+d.getValue();
            }
        }
        constraints +="}\n";

        result += constraints+field;

        //надо для реализации 1 ко многим
        result += "String toString(){\n\treturn "+toString+";\n}\n";

        return result+"}";
    }
}

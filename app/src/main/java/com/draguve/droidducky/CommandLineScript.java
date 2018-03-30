package com.draguve.droidducky;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Draguve on 3/30/2018.
 */

public class CommandLineScript {
    private String _id;
    private String _name;
    private String _code;
    private String _lang;
    private OperatingSystem _os;

    public CommandLineScript(String id,String name,String code,String lang,OperatingSystem os){
        _id = id;
        _name = name;
        _code = code;
        _lang = lang;
        _os = os;
    }

    public CommandLineScript(String name,String code,String lang,OperatingSystem os){
        _id = UUID.randomUUID().toString();
        _name = name;
        _code = code;
        _lang = lang;
        _os = os;
    }

    public void setName(String name){
        _name = name;
    }

    public void setCode(String code){
        _code = code;
    }
    
    public void setLang(String lang) { _lang = lang; }

    public void setOS(OperatingSystem os){
        _os = os;
    }

    public String getName(){
        return  _name;
    }

    public String getCode(){
        return _code;
    }

    public String getID(){
        return _id;
    }

    public String getLang() { return _lang;}

    public OperatingSystem getOS(){
        return _os;
    }

    public enum OperatingSystem{
        LINUX,
        WINDOWS,
        DARWIN;

        public static OperatingSystem fromInteger(int x) {
            switch(x) {
                case 0:
                    return LINUX;
                case 1:
                    return WINDOWS;
                case 2:
                    return DARWIN;
            }
            return null;
        }
    }
}

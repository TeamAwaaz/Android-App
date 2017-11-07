package com.teamkassvi.ascend;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kartik1 on 17-10-2017.
 */

public class FetchSongs {

    boolean fetchstatus=false;
    ArrayList<File> songs=new ArrayList<File>();
    FetchSongs(){

    }
    public ArrayList<File> findSongs(File root){
        ArrayList<File> al=new ArrayList<File>();
        File[] files=root.listFiles();
        if(files!=null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    al.addAll(findSongs(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".wav")) {
                        al.add(singleFile);
                    }
                }
            }
            songs = al;
        }
        fetchstatus = true;
        return al;
    }
    public boolean getfetchstatus(){
        return fetchstatus;
    }
    public ArrayList<File> getsonglist(){
        return songs;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvol;

/**
 *
 * @author Shawkey
 */
public class Profile {
    private String name,Desc;
    
    public Profile(String name, String Desc) {
        this.name = name;
        this.Desc = Desc;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDescription() {
        return Desc;
    }
}

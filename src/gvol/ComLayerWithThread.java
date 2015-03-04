/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gvol;

/**
 *
 * @author User
 */
public interface ComLayerWithThread {
    public void addToConsole(String line,int id);
    public void threadClosed(int id);
}

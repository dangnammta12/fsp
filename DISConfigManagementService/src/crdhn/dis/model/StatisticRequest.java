/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.dis.model;

/**
 *
 * @author nguyen
 */
public class StatisticRequest {
    
    private int numberofRequestUp;
    private int numberofRequestDown;
    private long sizeofAllFileUp;
    private long sizeofAllFileDown;
    private long totalSize;

    public StatisticRequest() {
    }

    public StatisticRequest(int numberofRequestUp, int numberofRequestDown, long sizeofAllFileUp, long sizeofAllFileDown, long totalSize) {
        this.numberofRequestUp = numberofRequestUp;
        this.numberofRequestDown = numberofRequestDown;
        this.sizeofAllFileUp = sizeofAllFileUp;
        this.sizeofAllFileDown = sizeofAllFileDown;
        this.totalSize = totalSize;
    }

    public int getNumberofRequestUp() {
        return numberofRequestUp;
    }

    public void setNumberofRequestUp(int numberofRequestUp) {
        this.numberofRequestUp = numberofRequestUp;
    }

    public int getNumberofRequestDown() {
        return numberofRequestDown;
    }

    public void setNumberofRequestDown(int numberofRequestDown) {
        this.numberofRequestDown = numberofRequestDown;
    }

    public long getSizeofAllFileUp() {
        return sizeofAllFileUp;
    }

    public void setSizeofAllFileUp(long sizeofAllFileUp) {
        this.sizeofAllFileUp = sizeofAllFileUp;
    }

    public long getSizeofAllFileDown() {
        return sizeofAllFileDown;
    }

    public void setSizeofAllFileDown(long sizeofAllFileDown) {
        this.sizeofAllFileDown = sizeofAllFileDown;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    
    
}

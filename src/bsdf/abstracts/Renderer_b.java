/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.abstracts;

import bitmap.core.AbstractDisplay;
import bsdf.Scene_b;

/**
 *
 * @author user
 * @param <T>
 */
public interface Renderer_b<T extends AbstractDisplay> {
    public boolean prepare(Scene_b scene, int w, int h);
    public void startExecution(T display);
    public void stop();
    public void pause();
    public void resume();
    public void updateDisplay();
    public void trigger();
    public boolean isRunning();
}

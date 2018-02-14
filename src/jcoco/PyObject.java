/**
 * PyObject.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 * 
 * License:
 * Please read the LICENSE file in this distribution for details regarding
 * the licensing of this code. This code is freely available for educational
 * use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * 
 * Description:
 * In the C++ implementation of coco, inheritance was used extensively to 
 * implement Python's magic methods. However, as functions are not first class
 * objects in Java as they are in C++, we choose a different approach to
 * implementing them in Java. The easiest way was to make PyObjects an interface
 */

package jcoco;

import java.util.ArrayList;
import java.util.HashMap;

public interface PyObject {
    public String str();
    public PyType getType();
    
    public void set(String key, PyObject value) ;
    public PyObject get(String key) ;
    public PyObject callMethod(PyCallStack callStack, String name, ArrayList<PyObject> args) ;
}
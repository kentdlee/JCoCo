/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Mar 3, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description: This is an internal class used to encapsulate a hash map
 * mapping strings to PyObjects. The locals, globals, and other maps like
 * this are all of this type HashMap, but sometimes need to be passed like 
 * a PyObject. For instance, during class creation the dict object needs to be
 * passed from the PyClass to the PyFunction so that the dict gets populated 
 * with the correct methods during class instantiation. See the PyBuildClass
 * module (__call__ method) and the STORE_LOCALS instruction.
 * 
 * This is used in place of PyDict because the dictionary is dependent on other
 * Python types while the PyMap is not. This is to avoid a circular reference.
 */
package jcoco;

import java.util.HashMap;
import jcoco.PyType.PyTypeId;

public class PyMap extends PyObjectAdapter {
    private HashMap<String, PyObject> map;

    public PyMap(HashMap<String, PyObject> map) {
        this.map = map;
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyMapType);
    }
    
    @Override
    public String str() {
        return "PyMap()";
    }
    
    public HashMap<String, PyObject> getMap() {
        return map;
    }
}

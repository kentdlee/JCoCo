/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on March 3, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description: This class implements the super class proxy object that 
 * redirects calls from the subclass to the base class. 
 */
package jcoco;

import java.util.ArrayList;

public class PySuper extends PyObjectAdapter {
    private PyObjectInst obj;
    
    public PySuper(PyObjectInst obj) {
        super();
        this.obj = obj;

    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyType.PyTypeId.PySuperTypeId);
    }
    
    @Override
    public String str() {
        return "<super: <class '" + obj.getType().str() + "'>, <" + obj.getType().str() + " object>>" ;
    }
    
    @Override
    public void set(String key, PyObject value)  {
        throw new PyException(PyException.ExceptionType.PYATTRERROR, 
                                    "AttributeError: Cannot set attribute" + key + " in base using super().");
    }
    
    @Override
    public PyObject get(String key)  {
        return new PyMethod(key, obj,(PyCallable)((PyClass)obj.getType()).getBaseClass().get(key));
    } 
}


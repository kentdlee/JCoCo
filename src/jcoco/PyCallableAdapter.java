/**
 * PyCallableAdapter.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The class implementation of the PyCallable interface. 
 * 
 * This is used as a class that all PyCallable classes inherit from.This class is 
 * necessary to add the call method to the dictionary for the object. 
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;

public class PyCallableAdapter extends PyObjectAdapter implements PyCallable {

    public PyCallableAdapter() {
        PyCallableAdapter self = this;
        this.dict.put("__call__", new PyBaseCallable() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
                return self.__call__(callStack, args);
            }
        });
    }
    
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Cannot call __call__ on PyCallableAdapter object");
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyType.PyTypeId.PyFunctionType);
    } 
}

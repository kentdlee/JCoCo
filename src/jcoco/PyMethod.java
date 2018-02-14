/**
 * PyMethod.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * This class is used to store functions that are part of a class in an instance
 * of a class. It has the same functionality as the PyFunction class, except 
 * that it includes a reference to the class instance object (i.e. self) and 
 * adds it to the arguments when calling the function. 
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyType.PyTypeId;

public class PyMethod extends PyCallableAdapter {
    private PyObject self;
    private PyCallable fun;
    private String funName;
    
    public PyMethod(String funName, PyObject self, PyCallable fun) {
        this.self = self;
        this.fun = fun;
        this.funName = funName;
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyMethodType);
    }
    
    @Override
    public String str() {
        return "<method '"+funName+"' of object " + self.str() +">";
    }
    
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        args.add(this.self);
        PyObject result = fun.__call__(callStack, args);
        //take self back out of args because when a method
        //is called no one would suspect that a side-effect
        //was that args was mutated.
        args.remove(args.size()-1);
        
        return result;
        
    }
    
}

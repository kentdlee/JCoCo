/**
 * PyBuiltInBuildClass.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * This class is necessary to build classes when the class construction is via
 * a function. This occurs when a class has free variables that are accessed 
 * from outside the class. See the test program classesinherit.py and the 
 * disassembled code for the class B. 
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyType.PyTypeId;
import jcoco.PyException.ExceptionType;
import java.util.HashMap;

public class PyBuildClass extends PyCallableAdapter {
    private HashMap<String, PyObject> globals;

    public PyBuildClass(HashMap<String, PyObject> globals) {
        super();
        this.globals = globals;
    }
    
    // There are two arguments passed to this function, the closure is at TOS1 before the 
    // call. The name of the class is at TOS. So, the name is at args[0] and the closure
    // is at args[1].
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        // This call method is passed two things. The PyFunction closure representing 
        // the class' function (that is used for class instantiation) and the name 
        // of the class. The name is at TOS and the function is at TOS1. 
        // This method then creates a dictionary mapping "__name__" to TOS. It places this 
        // dictionary in the locals of the class function (passed as an argument) and then
        // calls the class function to complete the instantiation of the class. 
        // The STORE_LOCALS instruction (in the class' instantiation function) 
        // goes through the dictionary found at TOS and maps 
        // each key to its value in the new class instance. 
        // The LOAD_NAME goes to the class instance (not objects of the class, but the actual 
        // class instance) and loads an attribute of the class. 
        // STORE_NAME stores the value at TOS in the name associated with its operand as
        // an attribute of the class instance. 
        if (args.size()<2 || args.size()>3) {
             throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                                    "TypeError: expected 2 or 3 arguments, got "+args.size());
        }
        
        String baseName = "";
        int argsOffset = 0;
        
        if (args.size()==3) {
            baseName = ((PyClass)args.get(0)).getName();
            argsOffset+=1;
        }
            

        
        PyStr name = (PyStr) args.get(0+argsOffset); // the name of the class
        PyFunction fun = (PyFunction) args.get(1+argsOffset); // the function for building the class
        
        PyClass newClass = new PyClass(name.str(), new ArrayList<PyObject>(), baseName, globals);
        
        // Put the class' dictionary in a PyMap to be passed as an argument to the
        // function that intializes the class.
        PyMap map = new PyMap(newClass.attrs); 
        ArrayList<PyObject> classargs = new ArrayList<PyObject>();
        classargs.add(map);
        globals.put(name.str(), newClass);
        fun.__call__(callStack, classargs);
 
        return newClass;
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }
    
    @Override
    public String str() {
        return "<built-in function build_class>";
    }
    
}

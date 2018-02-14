/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * This is the built-in input function from Python. It takes one argument, 
 * a string prompt, and returns a string read from standard input. Only one of
 * these objects is created when JCoCo starts and "input" is mapped to it in the
 * globals. 
 */
package jcoco;

import java.util.ArrayList;
import java.util.Scanner;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyBuiltInInput extends PyCallableAdapter {

    public PyBuiltInInput() {
        super();
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }
    
    @Override
    public String str() {
        return "<built-in function input>";
    }
    
    @Override 
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        PyObject x;
        PyStr y;
        String line;

        if (args.size() != 1) {
            throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                                "TypeError: expected 1 arguments, got"+args.size());
        }
        
        x = args.get(0);
        
        if (x.getType().typeId() != PyTypeId.PyStrType) {
            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Invalid argument to input(): expected str, found "+x.getType().str());
        }
        
        y = (PyStr)x;
        
        System.out.print(y.str());
        
        //Only one scanner can be created in Java due to some problem when 
        //creating multiple. 
        line = JCoCo.scanner.nextLine();
        
        return new PyStr(line);     
    } 
}

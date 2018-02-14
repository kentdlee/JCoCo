/**
 * PyCallable.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * PyCallable is an interface for callable objects in JCoCo.
 * 
 * The __call__ function is implemented by every object that is callable. This 
 * interface is necessary to get around the circular dependency introduced in 
 * PyObjectAdapter and PyCallableAdapter. 
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;

public interface PyCallable extends PyObject {
    
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) ;
}

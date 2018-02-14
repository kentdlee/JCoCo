/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Jan 1, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 */
package jcoco;

import java.util.ArrayList;

/**
 *
 * @author leekentd
 */
public class PyTurtleType extends PyType {

    public PyTurtleType() {
        super("Turtle", PyType.PyTypeId.PyTurtleType);
    }

    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
        if (args.size() != 0) {
            throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 0 arguments, got " + args.size());
        }
        
        return new PyTurtle();
    }
}
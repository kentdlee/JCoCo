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
public class PyBuiltInOpen extends PyCallableAdapter {

    public PyBuiltInOpen() {
        super();
    }

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyType.PyTypeId.PyBuiltInType);
    }

    @Override
    public String str() {
        return "<built-in function open>";
    }

    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
        ArrayList<PyObject> iterArgs = new ArrayList<PyObject>();
        PyStr mode = new PyStr("r");
        PyStr filename;

        if (args.size() == 0 || args.size() > 2) {
            throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                    "TypeError: expected 1 arguments, got " + args.size());
        }

        filename = (PyStr) args.get(args.size()-1);
        
        if (args.size()>1) {
            mode = (PyStr) args.get(1);
        }

        return new PyFile(filename.str(), mode.str());
    }

}

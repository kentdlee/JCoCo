/**
 * PyAttr.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
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
import jcoco.PyType.PyTypeId;

public class PyAttr extends PyCallableAdapter {
    private String field;
    private PyObject self;
    
    public PyAttr(PyObject self, String field) {
        this.self = self;
        this.field = field;
    }
    
    @Override
    public String str() {
        try {
            return this.self.get(this.field).str();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "PyAttr Object";
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }
    
    @Override 
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
        args.add(0, self);
        return self.callMethod(callStack,field, args);
    }
}

/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Mar 3, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description: This class represents all object instances of user-defined
 * classes. 
 */
package jcoco;

public class PyObjectInst extends PyObjectAdapter {
    private PyClass classInst = null;
    
    public PyObjectInst(PyClass classInst) {
        super();
        this.classInst = classInst;
    }
    
    @Override
    public String str() {
        return "<" + classInst.callName() + " object at 0x" + Integer.toHexString(System.identityHashCode(this)) + ">";
    }
    
    @Override
    public PyType getType() {
        return classInst;
    }
}

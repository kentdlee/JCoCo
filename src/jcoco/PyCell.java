/**
 * PyCell.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description: This is for indirect references used in closures. 
 */
package jcoco;

import jcoco.PyType.PyTypeId;

class PyCell extends PyObjectAdapter {
    private PyObject ref;
    
    public PyCell(PyObject ref) {
        super();
        this.ref = ref;
    }

    public void set(PyObject ref) {
        this.ref = ref;
    }

    public PyObject deref() {
        return this.ref;
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyCellType);
    }
}

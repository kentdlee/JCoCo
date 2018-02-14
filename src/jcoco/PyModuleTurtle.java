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
import static jcoco.JCoCo.PyTypes;

/**
 *
 * @author leekentd
 */
public class PyModuleTurtle extends PyObjectAdapter {
    
    PyModuleTurtle() {
        super("<module 'turtle' (built-in)>", PyType.PyTypeId.PyModuleType);  
        this.set("Turtle", PyTypes.get(PyType.PyTypeId.PyTurtleType));
        
        
    }
}

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

import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author leekentd
 */
public class PyModuleSys extends PyObjectAdapter {
    
    public PyModuleSys() {
        super("<module 'sys' (built-in)>", PyType.PyTypeId.PyModuleType);
        this.set("stdin", new PyFile(new Scanner(System.in)));
        this.set("stdout", new PyFile(new PrintWriter(System.out)));           
    }
    
}

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

public class PyMarker extends PyObjectAdapter {

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyType.PyTypeId.PyMarkerType);
    }

    @Override
    public String str() {
        return "Marker";
    }

}

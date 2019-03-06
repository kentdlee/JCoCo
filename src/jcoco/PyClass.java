/**
 * PyClass.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * This class defines user defined classes in JCoCo. 
 * 
 * On initialization, each PyObject in the classesandfuns is either a PyCode or PyClass
 * object from the PyParser. They get added as such to the dictionary for the class.
 * 
 * When the class is called, a class instance is created as a PyObject. At this
 * point, the code stored in the dictionary is used to create a PyMethod which is
 * stored in the dictionary of the class instance object. All other attributes that
 * are not methods are simply added to the dictionary of the class instance object.
 */

package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyClass extends PyType {
    private String name;
    private String baseClass;
    private ArrayList<PyObject> classesandfuns;
    private HashMap<String, PyObject> globals;
    
    public PyClass(String name, ArrayList<PyObject> nestedclassesandfuns, String baseClass, HashMap<String, PyObject> globals)  {
        super(name, PyTypeId.PyClassType);
        this.baseClass = baseClass;
        this.name = name;
        this.classesandfuns = nestedclassesandfuns;
        this.globals = (HashMap<String, PyObject>)globals;

        // This attribute is assumed for class instantiation, and probably by all types. 
        this.attrs.put("__name__", new PyStr(name));
        
        //put all functions and classes in the dictionary
        for (int i = 0; i < classesandfuns.size(); i++) {
            if (classesandfuns.get(i).getType().typeId() == PyTypeId.PyCodeType) {
                PyCode code = (PyCode) classesandfuns.get(i);
                ArrayList<PyObject> env = new ArrayList<PyObject>();
                for (int j = 0; j<code.getFreeVars().size(); j++) {
                    String freeVar = code.getFreeVars().get(j);
                    if (freeVar.equals("__class__")) {
                        env.add(new PyCell(this));
                    } else {
                        throw new PyException(ExceptionType.PYMATCHEXCEPTION, 
                                        "Error: Found unexpected freevar in class declaration.");
                    }
                }
                PyFunction fun = new PyFunction((PyCode) classesandfuns.get(i), globals, new PyTuple(env));
                this.attrs.put(fun.callName(), fun);
            } else if (classesandfuns.get(i).getType().typeId() == PyTypeId.PyTypeType) {
                PyClass cls = (PyClass) classesandfuns.get(i);
                this.attrs.put(cls.getName(), cls);
            } else {
                throw new PyException(ExceptionType.PYMATCHEXCEPTION, 
                                        "TypeError: expected a Function or Class, got "+classesandfuns.get(i).getType().str());
            }
        }
    }
    
    public PyObject getBaseClass() {
        return globals.get(baseClass);
    }
    
    public void setGlobals(HashMap<String, PyObject> globals) {
        this.globals = globals;
    }
    
    public ArrayList<PyObject>  getClassesAndFuns() {
        return this.classesandfuns;
    }

    public String getName() {
        return this.name;
    }
    
    public String callName() {
        return this.getName();
    }
    
    public String prettyString(String indent, boolean lineNumbers)  {
        StringBuffer ss = new StringBuffer();
        
        ss.append(indent);
        ss.append("Class: ");
        ss.append(this.name);
        ss.append("\n");
        ss.append(indent+"BEGIN\n");
        for (int i = 0; i < this.classesandfuns.size(); i++ ){
            if (this.classesandfuns.get(i).getType().typeId() == PyTypeId.PyTypeType) {
                ss.append(((PyClass)this.classesandfuns.get(i)).prettyString(indent+"\t", lineNumbers));
            } else if (this.classesandfuns.get(i).getType().typeId() == PyTypeId.PyCodeType){
                ss.append(((PyCode)this.classesandfuns.get(i)).prettyString(indent+"\t", lineNumbers));
            } else {
                throw new PyException(ExceptionType.PYMATCHEXCEPTION, 
                                        "TypeError: cannot make pretty string from "+this.classesandfuns.get(i).getType().str()+" object.");
            }
        }
        ss.append("\n");
        ss.append(indent+"END\n");
        return ss.toString();
    }
    
    public void initInstance(PyObjectAdapter obj)  {
        // The following lines are needed to support inheritance. We 
        // start by initializing base classes because subclasses should
        // override base classes. 
        if (!baseClass.equals("")) {
            ((PyClass)globals.get(baseClass)).initInstance(obj);
        }
        
        for (String name : this.attrs.keySet()) {
            if (this.attrs.get(name).getType().typeId() == PyTypeId.PyFunctionType) {
                obj.dict.put(name, new PyMethod(name, obj, (PyCallable)this.attrs.get(name)));
            }
        }  
    }
    
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {       
        PyObjectAdapter obj = new PyObjectInst(this);
        initInstance(obj);
        ((PyMethod) obj.dict.get("__init__")).__call__(callStack, args);
        
        return obj;
    }
    
    @Override 
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyTypeType);
    }
}

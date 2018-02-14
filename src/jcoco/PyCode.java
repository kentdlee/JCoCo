/**
 * PyCode.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * PyCode objects hold the various parts of an assembly language function. Each
 * function has a name, a number of arguments, byte code, and several lists 
 * which are outlined below. 
 * 
 * Constants - Constant values that are used within the function.
 * 
 * Locals - The list of local variables used in the function. 
 * 
 * FreeVars - The list of variables not initialized in this function. They are
 * referenced from an enclosing scope. 
 * 
 * CellVars - The list of cell variables for local variables that are referenced
 * outside of this function.
 * 
 * Globals - The list of globals like "str" and other functions that are 
 * defined globally and used in this function.
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

class PyCode  extends PyObjectAdapter {

    private String name;
    private ArrayList<PyObject> nestedClassFunctions;
    private ArrayList<String> locals;
    private ArrayList<String> freevars;
    private ArrayList<String> cellvars;
    private ArrayList<String> globals;
    private ArrayList<PyObject> consts;
    private ArrayList<PyByteCode> instructions;
    private int argCount;
    
    public PyCode(String name, ArrayList<PyObject> nestedClassFunctionList, ArrayList<PyObject> constants, ArrayList<String> locals, ArrayList<String> freevars, ArrayList<String> cellvars, ArrayList<String> globals, ArrayList<PyByteCode> instructions, int argCount) {
        this.name = name;
        this.locals = locals;
        this.freevars = freevars;
        this.cellvars = cellvars;
        this.globals = globals;
        this.consts = constants;
        this.instructions = instructions;
        this.argCount = argCount;
        this.nestedClassFunctions = nestedClassFunctionList;
    }

    public String getName() {
        return this.name;
    }
    
    public ArrayList<PyObject> getNestedClassFunctions() {
        return this.nestedClassFunctions;
    }
    
    public ArrayList<String> getLocals() {
        return this.locals;
    }
    
    public ArrayList<String> getFreeVars() {
        return this.freevars;
    }
    
    public ArrayList<String> getCellVars() {
        return this.cellvars;
    }
    
    public ArrayList<String> getGlobals() {
        return this.globals;
    }
    
    public ArrayList<PyObject> getConsts() {
        return this.consts;
    }
    
    public ArrayList<PyByteCode> getInstructions() {
        return this.instructions;
    }
    
    public int getArgCount() {
        return this.argCount;
    }
    
    private String vectorValsToString(String name, ArrayList<PyObject> lst)  {
        String result = "";
        
        if (lst != null && lst.size() > 0) {
            result += name;
            
            for (int i = 0; i < lst.size(); i++) {
                PyObject obj = lst.get(i);
                
                if (obj.getType().typeId() == PyTypeId.PyStrType) {
                    result += "'"+obj.str() + "'";
                } else {
                    result += lst.get(i).str();
                }
                
                if (i < lst.size()-1) {
                    result+= ", ";
                }
            }
            result += "\n";
        }
        
        return result;
    }
    
    private String vectorToString(String name, ArrayList<String> lst) {
        String result = "";
        
        if (lst.size() > 0) {
            result += name;
            
            for (int i = 0; i < lst.size(); i++) {
                result += lst.get(i);
                
                if (i < lst.size()-1){
                    result += ", ";
                }
            }
            
            result += "\n";
        }
        return result;
    }
    
    public String prettyString(String indent, boolean lineNumbers)  {
        StringBuffer ss = new StringBuffer();
        
        ss.append(indent);
        ss.append("Function: ");
        ss.append(this.name);
        ss.append("/"+this.argCount+"\n");            
        
        if (!lineNumbers) {
            for (int i = 0; i < this.nestedClassFunctions.size(); i++) {
                if (this.nestedClassFunctions.get(i).getType().typeId() == PyTypeId.PyCodeType){
                    ss.append(((PyCode)this.nestedClassFunctions.get(i)).prettyString(indent + "\t", lineNumbers));
                } else if (this.nestedClassFunctions.get(i).getType().typeId() == PyTypeId.PyTypeType) {
                    ss.append(((PyClass)this.nestedClassFunctions.get(i)).prettyString(indent + "\t", lineNumbers));
                } else {
                    throw new PyException(ExceptionType.PYMATCHEXCEPTION, 
                                            "TypeError: cannot make string from "+this.nestedClassFunctions.get(i).getType().str()+" type.");
                }
            }
        }
        
        ss.append(this.vectorValsToString(indent + "Constants: ", this.consts));
        ss.append(this.vectorToString(indent + "Locals: ", this.locals));
        ss.append(this.vectorToString(indent + "FreeVars: ", this.freevars));
        ss.append(this.vectorToString(indent + "CellVars: ", this.cellvars));
        ss.append(this.vectorToString(indent + "Globals: ", this.globals));
        ss.append(indent + "BEGIN\n");

        for (int i = 0; i < this.instructions.size(); i++) {
            if (lineNumbers) {
                ss.append(i+": ");
                if (i<10) {
                    ss.append(" ");
                }
                if (i < 100){
                    ss.append(" ");
                }
            }

            ss.append(indent+this.instructions.get(i).toString()+"\n");
        }
        
        ss.append(indent);
        ss.append("END\n");
    
        return ss.toString();
    }
    
    @Override 
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyCodeType);
    }
    
    @Override
    public String str() {
        return "code(" + this.name +")";
    }
}

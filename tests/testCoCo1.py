import disassembler
import sys

def funA(x,y):
    z = 0
    while x < y:
        z = z + x
        print(x,y,z)
        x = x + 1
        
    return z

def main():
    x = int(input("Please enter an integer: "))
    y = int(input("Please enter an integer: "))
    
    z = funA(x,y)
    print("The answer is",z)
    
 
if len(sys.argv) == 1:
    main()
else:
    disassembler.disassemble(funA)  
    disassembler.disassemble(main)

# If 1 2 3 4 is entered 10 is printed.

from disassembler import *
import sys

def main():
    def g(aVal):
        def f(x):
            return aVal + lstInts[0] + x

        return f
    
    x = input("Please enter a list of integers: ")
    lst = x.split()
    
    lstInts = []
    for y in lst:
        lstInts.append(int(y))
    
    myFun = g(6)
    
    print(myFun(lstInts[2]))

if len(sys.argv) == 1:
    main()
else:
    disassemble(main)
        
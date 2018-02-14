from disassembler import *
import sys

def fib(n):
    lastVal = 0
    currentVal = 1
    i = 1
    
    while True:
        if i == n:
            break  
        nextVal = lastVal + currentVal
        lastVal = currentVal
        currentVal = nextVal   
        i += 1
        
    return currentVal

def main():
    x = int(input("Please enter an integer: "))
    print("fib("+str(x)+") =",fib(x))
 
if len(sys.argv) == 1:
    main()
else:   
    disassemble(fib)
    disassemble(main)

        
        
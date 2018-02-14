from disassembler import *
import sys

def factorial(n):
    if n==0:
        return 1
    
    return n*factorial(n-1)

def main():
    x = int(input("Please enter an integer: "))
    y = factorial(x)
    
    print("Factorial of", x, "is", str(y)+".")
    print(type(x))
    print(type(factorial))
    z = type(factorial)
    print(z)
    print(type(z))
    print(type(type(z)))
    print(type(print))

 
if len(sys.argv) == 1:
    main()
else: 
    disassemble(factorial)
    disassemble(main)
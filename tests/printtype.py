from disassembler import *
import sys

def factorial(n):
    if n==0:
        return 1
    
    return n*factorial(n-1)

def main():
    print(type(factorial))
    
if len(sys.argv) == 1:
	main()
else:
	disassemble(factorial)
	disassemble(main)

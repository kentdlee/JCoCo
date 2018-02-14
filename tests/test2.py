from disassembler import *
import sys

def main():
    
    def f(x):
    
        print(type(x))
    
    f(2)
  
if len(sys.argv) == 1:
	main()
else:  
	disassemble(main)
    

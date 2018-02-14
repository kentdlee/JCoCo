from disassembler import *
import sys

def main():
    
    print(str.__add__("hi","there"))
  
if len(sys.argv) == 1:
	main()
else:  
	disassemble(main)
    

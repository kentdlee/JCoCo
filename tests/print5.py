from disassembler import *
import sys

def main():
    x = 5
    print(x)
  
if len(sys.argv) == 1:
	main()
else:  
	disassemble(main)
    
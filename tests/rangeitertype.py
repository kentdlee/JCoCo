from disassembler import *
import sys

def main():
    x = iter(range(5))
    print(x)

if len(sys.argv) == 1:
	main()
else:    
	disassemble(main)
from disassembler import *
import sys

def main():
    x = input("Enter a string: ")
    
    for a in x:
        print(a)

if len(sys.argv) == 1:
	main()
else:     
	disassemble(main)
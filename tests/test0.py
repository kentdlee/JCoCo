import disassembler
import sys

def main():
    print(5+5)
 
if len(sys.argv) == 1:
	main()
else:   
	disassembler.disassemble(main)
    
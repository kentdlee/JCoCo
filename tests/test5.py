import disassembler
import sys

def main():
    x=5
    print((x+7,x))
    
if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

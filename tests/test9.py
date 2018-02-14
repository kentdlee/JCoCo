import disassembler
import sys

def main():
    x = int(input("Please enter an integer: "))
    print(x+1)
    
if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

    
    
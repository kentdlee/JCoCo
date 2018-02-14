import disassembler
import sys

def main():
    x = 5
    y = x + 5
    print(y+5)
   
if len(sys.argv) == 1:
	main()
else: 
	disassembler.disassemble(main)
    
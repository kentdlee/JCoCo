import disassembler
import sys

def main():
    def f(x):
        return x + 1
    
    print(f(2))

if len(sys.argv) == 1:
	main()
else:  
	disassembler.disassemble(main)
    

import disassembler
import sys

def main():
    def anon_f0(x):
        return x + 1
    
    anon_f0(6)

 
if len(sys.argv) == 1:
	main()
else:  
	disassembler.disassemble(main)
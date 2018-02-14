import disassembler
import sys

def main():
    def f(x):
        
        return x + 1
    
    def g(y):
        z = f(y)
        
        return z
    
    print(g(g(6)))
    
if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)
import disassembler
import sys

def main():
    def f(x):
        def g(y):
            return x + y
        return g
    print (f(5)(4))

if len(sys.argv) == 1:
	main()
else:    
	disassembler.disassemble(main)

    
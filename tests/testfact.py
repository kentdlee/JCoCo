import disassembler
import sys

def fact(n):
    if n==0:
        return 1
    else:
        return n * fact(n-1)
def main():
    print(fact(5))

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(fact)
	disassembler.disassemble(main)

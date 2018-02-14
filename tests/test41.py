
import disassembler
import sys

def main():

    x = [1,2,3]
    print(list.__setitem__(x,2,5))
    print(x)
    print(x.__setitem__(2,6))
    print(x)
    x[2] = 100
    print(x)


if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

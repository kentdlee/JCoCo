import disassembler
import sys

def main():
    x = 10
    y = 0
    print(x/y)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

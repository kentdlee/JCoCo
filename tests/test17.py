import disassembler
import sys
def main():
    print((lambda x: x**2)(6))

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

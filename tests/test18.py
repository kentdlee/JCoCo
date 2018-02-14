import disassembler
import sys

def main():
    x = 5
    y = 4

    if x > y:
      z = x
    else:
      z = y

    print(z)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

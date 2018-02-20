import disassembler
import sys

def main():
    x = int(input("Please enter an integer:"))
    total = x
    for i in range(x,-1,-1):
        try:
            total = total/i
        except Exception as ex:
            print(ex)

    print(total)


if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

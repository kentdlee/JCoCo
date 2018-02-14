import disassembler
import sys

def main():
    try:

        raise Exception("hi there")

    except Exception as ex:
        print(ex)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

import disassembler
import sys

def main():
    try:
        raise Exception("Hello World!")
    except Exception as ex:
        print(ex)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)

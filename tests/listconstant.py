import disassembler
import sys

def main():
	lst = ["hello","world"]
	print(lst)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(main)